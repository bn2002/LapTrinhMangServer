package org.example.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
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
    @JsonIgnore
    private AttemptQuestionId attemptQuestionId;

    @ManyToOne
    @JoinColumn(name = "attempt_id", insertable = false, updatable = false)
    private RoomAttempt roomAttempt;

    @Column(name = "answer_id")
    private int selectedAnswerId;

    @Column(name = "created_at")
    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    private Timestamp createdAt;
}
