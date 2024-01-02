package org.example.services;

import org.example.dtos.InputErrorDto;
import org.example.dtos.RoomDto;
import org.example.entities.Request;
import org.example.entities.Response;
import org.example.entities.Room;
import org.example.entities.User;
import org.example.repositories.RoomRepository;
import org.example.repositories.UserRepository;
import org.example.utils.DBUtil;
import org.example.utils.JsonUtil;
import org.example.utils.MapperUtil;
import org.example.utils.ValidationUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;

public class ExamManagementService {
    Logger logger = LoggerFactory.getLogger(ExamManagementService.class);
    @Transactional
    public String create(Request request, User user) {
        try {
            RoomDto roomDto = MapperUtil.mapFromObject(request.getData(), RoomDto.class);
            ArrayList<InputErrorDto> errors = ValidationUtil.runValidation(roomDto);
            if(errors.size() > 0) {
                return JsonUtil.buidResponse(new Response("error","exam.create.validation_error", "Thông tin bạn nhập không hợp lệ", errors));
            }

            Room room = MapperUtil.mapFromObject(roomDto, Room.class);
            room.getQuestions().forEach(question -> {
                question.setOwnerId(user.getUserId());
                question.getAnswers().forEach(answer -> answer.setQuestion(question));
            });
            RoomRepository roomRepository = DBUtil.getContext().getBean(RoomRepository.class);

            roomRepository.saveAndFlush(room);
            logger.info("Tạo phòng thi thành công");
            return JsonUtil.buidResponse(new Response("success", "exam.create.success", "Tạo phòng thi thành công", ""));

        } catch(Exception e) {
            logger.info("Tạo phòng thi: " + e.getMessage());
            return JsonUtil.buidResponse(new Response("error", "exam.create.unknown_error", "Có lỗi không xác định đã xảy ra khi tạo phòng thi, hãy thử lại", ""));
        }


    }
}
