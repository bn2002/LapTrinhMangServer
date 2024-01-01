package org.example.services;

import org.example.dtos.RoomDto;
import org.example.entities.Request;
import org.example.entities.Room;
import org.example.repositories.RoomRepository;
import org.example.repositories.UserRepository;
import org.example.utils.DBUtil;
import org.example.utils.MapperUtil;
import org.springframework.transaction.annotation.Transactional;

public class ExamManagementService {
    @Transactional
    public String create(Request request) {
        try {
            RoomDto roomDto = MapperUtil.mapFromObject(request.getData(), RoomDto.class);
            Room room = MapperUtil.mapFromObject(roomDto, Room.class);
            room.getQuestions().forEach(question -> {
                question.getAnswers().forEach(answer -> answer.setQuestion(question));
            });
            RoomRepository roomRepository = DBUtil.getContext().getBean(RoomRepository.class);

            roomRepository.saveAndFlush(room);
            return "";
        } catch(Exception e) {
            return "";
        }


    }
}
