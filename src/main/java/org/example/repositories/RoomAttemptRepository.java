package org.example.repositories;

import org.example.entities.QuestionRoom;
import org.example.entities.Room;
import org.example.entities.RoomAttempt;
import org.example.entities.keys.QuestionRoomId;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Optional;

@Repository
public interface RoomAttemptRepository extends JpaRepository<RoomAttempt, Integer> {
    public Optional<RoomAttempt> findByRoom_RoomIdAndUserId(int roomId, int userId);

    public  RoomAttempt findByAttemptId(int attemptId);

    public ArrayList<RoomAttempt> findAllByUserIdAndAttemptStatusOrderByAttemptIdDesc(int userId, int attemptStatus);

    @Query(value = "SELECT attempt_id FROM room_attempts WHERE room_id = :id", nativeQuery = true)
    public ArrayList<Integer> getAttemptByRoomId(@Param("id") int roomId);

    @Modifying
    @Transactional
    @Query(value = "DELETE FROM room_attempts WHERE room_id = :ids", nativeQuery = true)
    public void deleteByRoomId(@Param("ids") int ids);

    @Query(value = "SELECT room_attempts.*, users.fullname, users.username FROM room_attempts INNER JOIN users ON users.user_id = room_attempts.user_id WHERE room_id = :roomId", nativeQuery = true)
    public ArrayList<RoomAttempt> getByRoom_RoomId(int roomId);
}
