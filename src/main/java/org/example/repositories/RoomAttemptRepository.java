package org.example.repositories;

import org.example.entities.QuestionRoom;
import org.example.entities.Room;
import org.example.entities.RoomAttempt;
import org.example.entities.keys.QuestionRoomId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.ArrayList;
import java.util.Optional;

public interface RoomAttemptRepository extends JpaRepository<RoomAttempt, Integer> {

    public Optional<RoomAttempt> findByRoomIdAndUserId(int roomId, int userId);

    public boolean existsByRoomIdAndUserIdAndAndAttemptStatus(int roomId, int userId, int status);
}
