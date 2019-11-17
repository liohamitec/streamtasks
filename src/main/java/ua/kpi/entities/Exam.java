package ua.kpi.entities;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@AllArgsConstructor(staticName = "of")
@Getter
@EqualsAndHashCode
@ToString
public class Exam {
    public enum Type{
        ENGLISH, MATH
    }
    private Type type;
    private double score;
}
