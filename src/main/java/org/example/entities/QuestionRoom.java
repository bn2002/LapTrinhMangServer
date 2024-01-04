package org.example.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Data;
import org.example.entities.keys.QuestionRoomId;

import java.util.Objects;

@Entity
@Table(name = "room_questions")
@Data
public class QuestionRoom {

    @EmbeddedId
    private QuestionRoomId questionRoomId;

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @MapsId("questionId")
    @AttributeOverride(name="questionId", column=@Column(name="question_id"))
    private Question question;

    @ManyToOne
    @MapsId("roomId")
    @AttributeOverride(name = "roomId", column = @Column(name = "room_id"))
    private Room room;

    @Column(name = "question_point")
    private int questionPoint;

    public QuestionRoom() {}

    public QuestionRoom(Question question, Room room) {
        this.question = question;
        this.room = room;
        this.questionRoomId = new QuestionRoomId(question.getQuestionId(), room.getRoomId());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass())
            return false;

        QuestionRoom that = (QuestionRoom) o;
        return Objects.equals(question, that.question) &&
                Objects.equals(room, that.room);
    }

    @Override
    public int hashCode() {
        return Objects.hash(question, room);
    }
}
