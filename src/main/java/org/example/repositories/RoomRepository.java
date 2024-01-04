package org.example.repositories;

import org.example.entities.Room;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.ArrayList;

public interface RoomRepository extends JpaRepository<Room, Integer> {

    public ArrayList<Room> findAllByOwnerId(int ownerId);
}
