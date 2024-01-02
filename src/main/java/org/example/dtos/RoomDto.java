package org.example.dtos;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.example.entities.Question;
import org.hibernate.validator.constraints.Range;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;

@Getter
@Setter
public class RoomDto {
    private int roomId;

    @NotEmpty(message = "Tên phòng không được bỏ trống")
    private String roomName;

    @NotEmpty(message = "Danh sách lớp")
    private String listClassId;

    @NotNull(message = "Thời gian bắt đầu không được bỏ trống")
    private Timestamp startTime;

    @NotNull(message = "Thi gian kết thúc không được bỏ trống")
    private Timestamp endTime;

    @Range(min = 0, message = "Thời gian thi không hợp lệ")
    private int duration;

    @Range(min = 0, message = "Số lượng câu hỏi không hợp lệ")
    private int countQuestion;

    private int isPractice;

    private Collection<Question> questions;
}
