package org.example.dtos;

import lombok.*;

import java.util.ArrayList;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AttemptQuestionDto {
    private String questionContent;
    private Integer questionId;
    private ArrayList<AttemptAnswerDto> answers;
    private int selectedAnswer = 0;
}
