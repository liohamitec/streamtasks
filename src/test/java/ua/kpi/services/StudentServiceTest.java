package ua.kpi.services;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import ua.kpi.entities.Exam;
import ua.kpi.entities.Exam.Type;
import ua.kpi.entities.Student;
import ua.kpi.repositories.StudentRepository;

import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class StudentServiceTest {

  private static Student STUDENT_1 = Student.builder()
      .name("1")
      .rating(10)
      .exams(Arrays.asList(Exam.of(Exam.Type.ENGLISH, 181)))
      .build();
  private static Student STUDENT_2 = Student.builder()
      .name("2")
      .rating(11)
      .exams(Arrays.asList(Exam.of(Exam.Type.ENGLISH, 182),
          Exam.of(Exam.Type.MATH, 191)))
      .build();
  private static Student STUDENT_3 = Student.builder()
      .name("3")
      .rating(11)
      .exams(Arrays.asList(Exam.of(Exam.Type.ENGLISH, 183),
          Exam.of(Exam.Type.MATH, 190)))
      .build();
  private static Student STUDENT_4 = Student.builder()
      .name("4")
      .rating(11)
      .exams(Arrays.asList())
      .build();
  private static Student STUDENT_5 = Student.builder()
          .name("5")
          .rating(12)
          .exams(Arrays.asList(Exam.of(Exam.Type.ENGLISH, 183),
                  Exam.of(Exam.Type.MATH, 195)))
          .build();

  private DecimalFormat decimalFormat = new DecimalFormat("#.00");

  private static StudentRepository studentRepository;
  private static StudentService studentService;

  private static StudentRepository createStudentRepositoryWithAllStudents(){
    StudentRepository studentRepository = mock(StudentRepository.class);
    List<Student> allStudents = Arrays.asList(STUDENT_1, STUDENT_2, STUDENT_3, STUDENT_4, STUDENT_5);
    when(studentRepository.findAll()).thenReturn(allStudents);
    return studentRepository;
  }

  @BeforeAll
  static void init() {
    studentRepository = createStudentRepositoryWithAllStudents();
    studentService = new StudentService(studentRepository);
  }

  @Test
  void should_find_student_with_max_english(){
    StudentService studentService = new StudentService(studentRepository);
    Optional<Student> studentOpt = studentService.findWithMaxExam(Type.ENGLISH);
    assertEquals(Optional.of(STUDENT_3) , studentOpt );
  }

  @Test
  void should_not_find_student_with_max_math(){
    StudentRepository studentRepository = mock(StudentRepository.class);
    when(studentRepository.findAll()).thenReturn(Arrays.asList(STUDENT_1, STUDENT_4));
    StudentService studentService = new StudentService(studentRepository);
    Optional<Student> studentOpt = studentService.findWithMaxExam(Type.MATH);
    assertEquals(Optional.empty() , studentOpt );
  }

  @Test
  void should_find_students_who_have_enough_math_grade(){
    StudentService studentService = new StudentService(studentRepository);
    final double mathPassRate = 190.0;
    List<Student> studentsWithMath = studentService.findWithEnoughExam(Type.MATH, mathPassRate);
    assertThat(studentsWithMath, containsInAnyOrder(STUDENT_2, STUDENT_3, STUDENT_5));
  }

  @Test
  void should_not_find_students_who_have_enough_english_grade(){
    final double englishPassRate = 190.0;
    List<Student> studentsWithEnglish = studentService.findWithEnoughExam(Type.ENGLISH, englishPassRate);
    assertThat(studentsWithEnglish, hasSize(0));
  }

  @Test //task 5
  void findStudentsWithTwoExams() {
    List<Student> resultList = studentService.findWithExamsAmount(2);
    assertThat(resultList, containsInAnyOrder(STUDENT_2, STUDENT_3, STUDENT_5));
  }

  @Test //task 7
  void findWithEnglishExamAndRatingMoreThanValue() {
    List<Student> resultList = studentService.findWithExamAndRating(Type.ENGLISH, 11);
    assertThat(resultList, containsInAnyOrder(STUDENT_2, STUDENT_3, STUDENT_5));
  }

  @Test //task 10
  void findWithEnglishExamAndBestThanAverageMathResult() {
    double avgMathResult = studentService.findAverageScore(Type.MATH);
    List<Student> resultList = studentService.findWithEnoughExam(Type.MATH, avgMathResult);
    resultList.removeIf(student -> student.getExams().stream()
            .noneMatch(exam -> exam.getType().equals(Type.ENGLISH)));
    assertThat(resultList, contains(STUDENT_5));
  }

  @Test
  void findTopTwoEnglishStudents() { //task 12
    List<Student> resultList = studentService.findBestScoredStudents(2, Type.ENGLISH);
    assertThat(resultList, containsInAnyOrder(STUDENT_3, STUDENT_5));
  }

  @Test
  void checkToStringOutput() {
    List<String> resultList = studentService.getAllStudentsReport();
    resultList.forEach(System.out::println);
  }
}