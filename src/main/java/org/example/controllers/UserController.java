package org.example.controllers;

import com.google.gson.Gson;
import org.example.models.Request;
import org.example.models.Response;
import org.example.models.User;
import org.example.repositories.UserRepository;
import org.example.utils.DBUtil;
import org.example.utils.ResponseUtil;

public class UserController {
    public static String login(Request request) {
        System.out.println("Login nayyyyyyyy");
        return "Login nayyyyyyyy";
    }

    public static String register(Request request) {
        User user;
        System.out.println("reposasdasdsa");
        UserRepository userRepository = DBUtil.getContext().getBean(UserRepository.class);
        try {
            Gson gson = new Gson();
            user = gson.fromJson(request.getData(), User.class);
        } catch(Exception e) {
            return "data bạn nhập không hợp lệ";
        }

        if(user.getUsername().isBlank() || user.getPassword().isBlank() || user.getEmail().isBlank()) {
            return ResponseUtil.buidResponse(new Response("error", "Vui lòng nhập đầy đủ thông tin", ""));
        }

        if(userRepository.existsByUsername(user.getUsername())) {
            return ResponseUtil.buidResponse(new Response("error", "Tài khoản này đã tồn tại trong hệ thống", ""));
        }

        if(userRepository.existsByEmail(user.getEmail())) {
            return ResponseUtil.buidResponse(new Response("error", "Email này đã tồn tại trong hệ thống", ""));
        }

        if(userRepository.existsByEmail(user.getEmail())) {
            return ResponseUtil.buidResponse(new Response("success", "Đăng ký thành công", ""));
        }

        userRepository.save(user);
        return "dsfsdfsdf";
    }

    public static boolean logout() {
        return true;
    }
}
