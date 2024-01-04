package org.example.entities.keys;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Objects;

@Embeddable
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AttemptQuestionId implements Serializable {

    @Column(name = "attempt_id")
    private Integer attemptId;
    @Column(name = "question_id")
    private Integer questionId;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass())
            return false;

        AttemptQuestionId that = (AttemptQuestionId) o;
        return Objects.equals(attemptId, that.attemptId) &&
                Objects.equals(questionId, that.questionId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(questionId, attemptId);
    }
}