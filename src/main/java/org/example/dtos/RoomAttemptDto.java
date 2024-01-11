package org.example.dtos;

import lombok.*;

import java.sql.Timestamp;

@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RoomAttemptDto {
    private int attemptId;
    private String roomName;
    private int isPractice;
    private Timestamp startTime;
    private Timestamp endTime;
    private int score;
}
