package org.example.services;

import org.example.entities.Request;
import org.example.entities.Response;
import org.example.entities.Room;
import org.example.repositories.RoomRepository;
import org.example.utils.DBUtil;
import org.example.utils.JsonUtil;

public class RoomService {
    public String createRoom(Request request) {
        try {
            Room room = JsonUtil.getObject(request.getData(), Room.class);
            RoomRepository roomRepository = DBUtil.getContext().getBean(RoomRepository.class);

            if(room == null) {
                return JsonUtil.buidResponse(new Response("error", "Yêu cầu tạo phòng của bạn không hợp lệ", ""));
            }

            return JsonUtil.buidResponse(new Response("success", "Đăng nhập thành công", ""));

        } catch(Exception e) {
            return JsonUtil.buidResponse(new Response("error", "Có lỗi không xác định đã xảy ra, hãy thử lại", ""));
        }
    }
}
