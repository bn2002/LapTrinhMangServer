package org.example.repositories;

import org.example.entities.Question;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.ArrayList;
import java.util.List;

public interface QuestionRepository extends JpaRepository<Question, Integer> {

    public ArrayList<Question> findAllByQuestionIdIn(List<Integer> ids);
}
