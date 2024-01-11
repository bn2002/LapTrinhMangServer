package org.example.dtos;

import lombok.*;

@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AttemptAnswerDto {
    private int answerId;
    private String answerContent;
}
