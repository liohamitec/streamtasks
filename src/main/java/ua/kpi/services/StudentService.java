package ua.kpi.services;

import ua.kpi.entities.Exam;
import ua.kpi.entities.Exam.Type;
import ua.kpi.entities.Student;
import ua.kpi.repositories.StudentRepository;

import java.text.DecimalFormat;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.OptionalDouble;
import java.util.stream.Collectors;

public class StudentService {

  private StudentRepository studentRepository;

  public StudentService(StudentRepository studentRepository) {
    this.studentRepository = studentRepository;
  }


  public Optional<Student> findWithMaxExam(Type type) {
    List<Student> students = studentRepository.findAll();
    OptionalDouble maxExam = students.stream()
        .flatMap(student -> student.getExams().stream())
        .filter(exam -> exam.getType() == type)
        .mapToDouble(Exam::getScore)
        .max();

    if(!maxExam.isPresent())
      return Optional.empty();

    Exam exam = Exam.of(type,maxExam.getAsDouble());
    return students.stream()
            .filter(student -> student.getExams().contains(exam))
            .findFirst();
  }

  public List<Student> findWithEnoughExam(Type examType, double passRate) {
    return studentRepository.findAll().stream()
            .filter(student -> student.getExams()
                    .stream()
                    .anyMatch(exam -> exam.getType() == examType && exam.getScore() >= passRate ))
            .collect(Collectors.toList());
  }

  public List<Student> findWithExamsAmount(int examsAmount) { //5
    return studentRepository.findAll().stream()
            .filter(student -> student.getExams().size() == examsAmount)
            .collect(Collectors.toList());
  }

  public List<Student> findWithExamAndRating(Type examType, double rating) { //7
    return studentRepository.findAll().stream()
            .filter(student -> student.getExams().stream().anyMatch(exam -> exam.getType().equals(examType))
                    && student.getRating() >= rating)
            .collect(Collectors.toList());
  }

  public List<Student> findBestScoredStudents(int amountOfTopStudents, Type examType) {
    return studentRepository.findAll().stream()
            .filter(student -> student.getExams().stream()
                    .anyMatch(exam -> exam.getType().equals(examType)))
            .sorted(Comparator.comparingDouble((Student student) -> getExamScore(student, examType))
                    .reversed())
            .limit(amountOfTopStudents)
            .collect(Collectors.toList());
  }

  private double getExamScore(Student student, Type examType) {
    return student.getExams().stream()
            .filter(exam -> exam.getType().equals(examType))
            .mapToDouble(Exam::getScore)
            .findFirst()
            .orElse(0);
  }

  public double findAverageScore(Type examType) {
    OptionalDouble optionalAverage = studentRepository.findAll().stream()
            .flatMap(student -> student.getExams().stream())
            .filter(exam -> exam.getType() == examType)
            .mapToDouble(Exam::getScore)
            .average();

    return optionalAverage.isPresent() ? optionalAverage.getAsDouble() : 0;
  }

  public List<String> getAllStudentsReport() {
    List<Student> students = studentRepository.findAll();
    return students.stream()
            .map(student -> buildReportForStudent(
                    getExamScoreSum(student.getExams()),
                    student.getRating(),
                    student.getName()))
            .collect(Collectors.toList());
  }

  private double getExamScoreSum(List<Exam> exams) {
    return exams.stream()
            .mapToDouble(Exam::getScore)
            .sum();
  }

  private String buildReportForStudent(double examsScoreSum, double rating, String name) {
    DecimalFormat decimalFormat = new DecimalFormat("#0.00");
    return name + " exam sum: " + decimalFormat.format(examsScoreSum)
            + ", rating: " + decimalFormat.format(rating);
  }
}
