package org.example.services;

import org.example.dtos.RoomDto;
import org.example.entities.Request;
import org.example.entities.Response;
import org.example.entities.Room;
import org.example.exceptions.InputNotValidException;
import org.example.repositories.RoomRepository;
import org.example.utils.DBUtil;
import org.example.utils.JsonUtil;
import org.example.utils.MapperUtil;
import org.example.utils.ValidationUtil;

import java.util.HashMap;

public class RoomService {
    public String createRoom(Request request) {
        try {
            RoomDto roomDto = (RoomDto)request.getData();
            ValidationUtil.runValidation(roomDto);


            Room room = MapperUtil.mapFromObject(roomDto, Room.class);
            RoomRepository roomRepository = DBUtil.getContext().getBean(RoomRepository.class);
            room = roomRepository.saveAndFlush(room);
            HashMap<String, String> response = new HashMap<>();
            int roomId = room.getRoomId();
            response.put("roomId", String.valueOf(roomId));
            response.put("roomName", String.valueOf(roomId));
            return JsonUtil.buidResponse(new Response("success", "exam.create.success", "Tạo phòng thi thành công", response));

        } catch (InputNotValidException e) {
            return JsonUtil.buidResponse(new Response("error", "exam.create.validate_error", "validate_error", e.errors));
        }
        catch(Exception e) {
            return JsonUtil.buidResponse(new Response("error", "exam.create.unknown_error", "Có lỗi không xác định đã xảy ra, hãy thử lại", ""));
        }
    }

    public String updateRoom(Request request) {
        return "";
    }
}
