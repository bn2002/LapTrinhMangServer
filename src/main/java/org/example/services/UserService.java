package org.example.services;

import org.example.delegates.UserControllerDelegate;
import org.example.entities.Request;
import org.example.entities.Response;
import org.example.entities.User;
import org.example.repositories.UserRepository;
import org.example.utils.DBUtil;
import org.example.utils.JsonUtil;

public class UserService {
    public UserControllerDelegate delegate;
    public String login(Request request) {
        try {
            User user = JsonUtil.getObject(request.getData(), User.class);
            UserRepository userRepository = DBUtil.getContext().getBean(UserRepository.class);

            if(user == null) {
                return JsonUtil.buidResponse(new Response("error", "Yêu cầu đăng nhập không hợp lệ", ""));
            }

            String username = user.getUsername();
            String password = user.getPassword();

            if(username.isBlank() || password.isBlank()) {
                return JsonUtil.buidResponse(new Response("error", "Vui lòng nhập username và password", ""));
            }

            User userInfo = userRepository.findUserByUsernameAndPassword(username, password);
            if(userInfo == null) {
                return JsonUtil.buidResponse(new Response("error", "Thông tin đăng nhập không chính xác", ""));
            }
            if(this.delegate != null) {
                this.delegate.loginResponse(userInfo);
            }
            return JsonUtil.buidResponse(new Response("success", "Đăng nhập thành công", ""));

        } catch(Exception e) {
            return JsonUtil.buidResponse(new Response("error", "Có lỗi không xác định đã xảy ra, hãy thử lại", ""));
        }
    }

    public String register(Request request) {
        UserRepository userRepository = DBUtil.getContext().getBean(UserRepository.class);
        User user = JsonUtil.getObject(request.getData(), User.class);
        if(user == null) {
            return JsonUtil.buidResponse(new Response("error", "Yêu cầu đăng ký không hợp lệ", ""));
        }
        if(user.getUsername().isBlank() || user.getPassword().isBlank() || user.getEmail().isBlank()) {
            return JsonUtil.buidResponse(new Response("error", "Vui lòng nhập đầy đủ thông tin", ""));
        }

        if(userRepository.existsByUsername(user.getUsername())) {
            return JsonUtil.buidResponse(new Response("error", "Tài khoản này đã tồn tại trong hệ thống", ""));
        }

        if(userRepository.existsByEmail(user.getEmail())) {
            return JsonUtil.buidResponse(new Response("error", "Email này đã tồn tại trong hệ thống", ""));
        }

        userRepository.save(user);
        return JsonUtil.buidResponse(new Response("success", "Đăng ký thành công", ""));
    }

    public boolean logout() {
        return true;
    }
}
