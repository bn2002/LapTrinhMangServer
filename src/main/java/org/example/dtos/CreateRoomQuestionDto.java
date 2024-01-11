package org.example.dtos;

import lombok.*;
import org.example.entities.Answer;

import java.util.Collection;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CreateRoomQuestionDto {
    private String questionContent;
    private Collection<Answer> answers;
    private int score;
}
