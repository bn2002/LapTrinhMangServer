package org.example.services;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
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
import org.modelmapper.ModelMapper;
import org.modelmapper.jackson.JsonNodeValueReader;

import java.util.HashMap;
import java.util.Set;

public class RoomService {
    public String createRoom(Request request) {
        try {
            RoomDto roomDto = MapperUtil.mapFromString(request.getData(), RoomDto.class);
            ValidationUtil.runValidation(roomDto);
            if(roomDto.getRoomName().isBlank() || roomDto.getRoomDescription().isBlank() || roomDto.getStartTime() == null || roomDto.getEndTime() == null) {
                return JsonUtil.buidResponse(new Response("error", "Yêu cầu tạo phòng của bạn không hợp lệ", ""));
            }

            Room room = MapperUtil.mapFromObject(roomDto, Room.class);
            RoomRepository roomRepository = DBUtil.getContext().getBean(RoomRepository.class);
            room = roomRepository.saveAndFlush(room);
            HashMap<String, String> response = new HashMap<>();
            int roomId = room.getRoomId();
            response.put("roomId", String.valueOf(roomId));
            response.put("roomName", String.valueOf(roomId));
            return JsonUtil.buidResponse(new Response("success", "Tạo phòng thi thành công", response));

        } catch (InputNotValidException e) {
            return JsonUtil.buidResponse(new Response("error", "validate_error", e.errors));
        }
        catch(Exception e) {
            return JsonUtil.buidResponse(new Response("error", "Có lỗi không xác định đã xảy ra, hãy thử lại", ""));
        }
    }

    public String updateRoom(Request request) {
        return "";
    }
}
