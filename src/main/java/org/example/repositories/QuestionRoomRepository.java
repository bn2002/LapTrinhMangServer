package org.example.repositories;

import org.example.entities.QuestionRoom;
import org.example.entities.Room;
import org.example.entities.keys.QuestionRoomId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;

public interface QuestionRoomRepository extends JpaRepository<QuestionRoom, QuestionRoomId> {
    public ArrayList<QuestionRoom> findAllByRoom(Room room);

    @Modifying
    @Transactional
    @Query(value = "DELETE FROM room_questions WHERE question_question_id IN :ids", nativeQuery = true)
    public void deleteByQuestionId(@Param("ids") ArrayList<Integer> ids);

    @Modifying
    @Transactional
    public void deleteByQuestionRoomId_RoomId(int id);
}
