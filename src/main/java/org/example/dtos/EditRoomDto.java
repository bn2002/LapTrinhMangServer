package org.example.dtos;

import lombok.*;

import java.sql.Timestamp;
import java.util.ArrayList;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class EditRoomDto {
    private int roomId;
    private String roomName;
    private String listClassId;
    private Timestamp startTime;
    private Timestamp endTime;
    private int duration;
    private int countQuestion;
    private int isPractice;
    private ArrayList<EditQuestionDto> questions;
    private ArrayList<Integer> deletedQuestionId;
}
