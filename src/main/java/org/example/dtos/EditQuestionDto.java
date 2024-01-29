package org.example.dtos;

import lombok.*;
import org.example.entities.Answer;

import java.util.Collection;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EditQuestionDto {
    private int questionId;
    private String questionContent;
    private Collection<EditAnswerDto> answers;
    private int score;
}
