package org.example.entities.keys;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Objects;

@Embeddable
@Data
@AllArgsConstructor
@NoArgsConstructor
public class QuestionRoomId implements Serializable {

    @Column(name = "question_id")
    private Integer questionId;
    @Column(name = "room_id")
    private Integer roomId;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass())
            return false;

        QuestionRoomId that = (QuestionRoomId) o;
        return Objects.equals(questionId, that.questionId) &&
                Objects.equals(roomId, that.roomId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(questionId, roomId);
    }
}
