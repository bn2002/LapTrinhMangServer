package org.example.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.entities.keys.AttemptQuestionId;
import org.example.entities.keys.QuestionRoomId;
import org.hibernate.annotations.CreationTimestamp;

import java.sql.Timestamp;

@Data
@Builder
@Entity(name = "attempt_questions")
@AllArgsConstructor
@NoArgsConstructor
public class AttemptQuestion {

    @EmbeddedId
    private AttemptQuestionId attemptQuestionId;

    @Column(name = "answer_id")
    private int answerId;

    @Column(name = "created_at")
    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    private Timestamp createdAt;
}
