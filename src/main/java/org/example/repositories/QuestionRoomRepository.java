package org.example.repositories;

import org.example.entities.QuestionRoom;
import org.example.entities.Room;
import org.example.entities.keys.QuestionRoomId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.ArrayList;

public interface QuestionRoomRepository extends JpaRepository<QuestionRoom, QuestionRoomId> {
    public ArrayList<QuestionRoom> findAllByRoom(Room room);
}
