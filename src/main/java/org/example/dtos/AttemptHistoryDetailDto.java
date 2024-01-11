package org.example.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class AttemptHistoryDetailDto {
    private int attemptId;
    private String roomName;
    private int duration;
    private int score;
    private ArrayList<AttemptQuestionHistoryDto> questions;
}
