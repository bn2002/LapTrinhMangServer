package org.example.dtos;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.sql.Time;
import java.sql.Timestamp;

@Getter
@Setter
@Builder
public class AttemptResultDto {
    private int score;
    private int totalCorrect;
    private int duration;
    private Timestamp startTime;
    private Timestamp endTime;
}
