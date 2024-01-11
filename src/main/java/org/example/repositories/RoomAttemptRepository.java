package org.example.repositories;

import org.example.entities.QuestionRoom;
import org.example.entities.Room;
import org.example.entities.RoomAttempt;
import org.example.entities.keys.QuestionRoomId;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Optional;

@Repository
public interface RoomAttemptRepository extends JpaRepository<RoomAttempt, Integer> {
    public Optional<RoomAttempt> findByRoom_RoomIdAndUserId(int roomId, int userId);

    public  RoomAttempt findByAttemptId(int attemptId);

    public ArrayList<RoomAttempt> findAllByUserIdAndAttemptStatusOrderByAttemptIdDesc(int userId, int attemptStatus);
}
