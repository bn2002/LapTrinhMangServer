package org.example.dtos;

import lombok.*;

import java.util.ArrayList;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AttemptQuestionHistoryDto {
    private String questionContent;
    private Integer questionId;
    private ArrayList<AttemptAnswerDto> answers;
    private int selectedAnswer = 0;
    private int correctAnswer = 0;
}
