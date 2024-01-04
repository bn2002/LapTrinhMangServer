package org.example.repositories;

import org.example.entities.AttemptQuestion;
import org.example.entities.QuestionRoom;
import org.example.entities.keys.AttemptQuestionId;
import org.example.entities.keys.QuestionRoomId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.ArrayList;



public interface AttemptQuestionRepository extends JpaRepository<AttemptQuestion, AttemptQuestionId> {

}