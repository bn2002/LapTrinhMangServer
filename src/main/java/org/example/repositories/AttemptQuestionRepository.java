package org.example.repositories;

import org.example.entities.AttemptQuestion;
import org.example.entities.QuestionRoom;
import org.example.entities.keys.AttemptQuestionId;
import org.example.entities.keys.QuestionRoomId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;



public interface AttemptQuestionRepository extends JpaRepository<AttemptQuestion, AttemptQuestionId> {
    @Query(value = "SELECT question_id FROM attempt_questions WHERE attempt_id = ?1", nativeQuery = true)
    public ArrayList<Integer> getListQuestionId(int attemptId);

    public AttemptQuestion getAttemptQuestionByAttemptQuestionId_AttemptIdAndAttemptQuestionId_QuestionId(int attemptId, int questionId);

    public ArrayList<AttemptQuestion> getAllByAttemptQuestionId_AttemptId(int attemptId);

    @Modifying
    @Transactional
    @Query(value = "DELETE FROM attempt_questions WHERE question_id  IN :ids", nativeQuery = true)
    public void deleteByQuestionId(@Param("ids") ArrayList<Integer> ids);
}