package org.example.dtos;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Range;

import java.sql.Timestamp;

@Getter
@Setter
public class RoomDto {
    private int roomId;

    @NotEmpty(message = "Tên phòng không được bỏ trống")
    private String roomName;

    @NotEmpty(message = "Mô tả phòng thi không được bỏ trống")
    private String roomDescription;

    @NotNull(message = "Thời gian bắt đầu không được bỏ trống")
    private Timestamp startTime;

    @NotNull(message = "Thi gian kết thúc không được bỏ trống")
    private Timestamp endTime;

    @Range(min = 0, message = "Thời gian thi không hợp lệ")
    private int duration;

    private int isPractice;
}
