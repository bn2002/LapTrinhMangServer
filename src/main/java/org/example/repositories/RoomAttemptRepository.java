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
import java.util.HashMap;
import java.util.Optional;

@Repository
public interface RoomAttemptRepository extends JpaRepository<RoomAttempt, Integer> {
    public Optional<RoomAttempt> findByRoom_RoomIdAndUserIdAndAttemptStatus(int roomId, int userId, int status);

    public  RoomAttempt findByAttemptId(int attemptId);

    public ArrayList<RoomAttempt> findAllByUserIdAndAttemptStatusOrderByAttemptIdDesc(int userId, int attemptStatus);

    @Query(value = "SELECT attempt_id FROM room_attempts WHERE room_id = :id", nativeQuery = true)
    public ArrayList<Integer> getAttemptByRoomId(@Param("id") int roomId);

    @Modifying
    @Transactional
    @Query(value = "DELETE FROM room_attempts WHERE room_id = :ids", nativeQuery = true)
    public void deleteByRoomId(@Param("ids") int ids);

    public ArrayList<RoomAttempt> getByRoom_RoomId(int roomId);

    @Query(value = "SELECT `score`, COUNT(attempt_id) as count_attempt from room_attempts WHERE room_id = :roomId group by `score`;", nativeQuery = true)
    public ArrayList<Object> getHistogramChart(@Param("roomId") int roomId);
}
