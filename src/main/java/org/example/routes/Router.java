package org.example.routes;

import org.example.services.ExamManagementService;
import org.example.services.RoomService;
import org.example.services.UserService;
import org.example.delegates.UserControllerDelegate;
import org.example.entities.Request;
import org.example.entities.Response;
import org.example.entities.User;
import org.example.utils.JsonUtil;
import org.example.utils.SocketUtil;

import java.lang.reflect.Method;
import java.net.Socket;
import java.util.HashMap;

public class Router implements UserControllerDelegate {
    private static HashMap<String, HashMap<String, Method>> privateRoutes = new HashMap<>();
    private static HashMap<String, HashMap<String, Method>> publicRoutes = new HashMap<>();
    private User currentUser;
    public void handlerRequest(Socket socket, Request request) {
        String controller = request.getController();
        String method = request.getMethod();
        UserService userService = new UserService();
        userService.delegate = this;

        // handler chưa login
        if(currentUser == null) {
            if(method.equals("login")) {
                String response = userService.login(request);
                SocketUtil.sendResponse(socket, response);
            } else if(method.equals("register")) {
                String response = userService.register(request);
                SocketUtil.sendResponse(socket, response);
            }
            return;
        }

        // handler các routes đã login
        if(controller.equals("user")) {
            if(method.equals("profile")) {
                SocketUtil.sendResponse(socket, currentUser.getEmail());
            }
        }

        if(controller.equals("exam")) {
            RoomService roomService = new RoomService();
            if(method.equals("attempt")) {
                String response = roomService.attempt(request, this.currentUser);
                SocketUtil.sendResponse(socket, response);
            } else if(method.equals("list")) {
                String response = roomService.list(request, this.currentUser);
                SocketUtil.sendResponse(socket, response);
            }
        }

        if(controller.equals("examManagement")) {
            ExamManagementService examManagementService = new ExamManagementService();
            if(method.equals("create")) {
                String response = examManagementService.create(request, this.currentUser);
                SocketUtil.sendResponse(socket, response);
            }else if(method.equals("list")) {
                String response = examManagementService.list(request, this.currentUser);
                SocketUtil.sendResponse(socket, response);
            }
        }

    }

    public void sendError(Socket socket, String message) {
        Response response = new Response("error", "request_invalid" ,message, "");
        SocketUtil.sendResponse(socket, JsonUtil.buidResponse(response));
    }
    @Override
    public void loginResponse(User user) {
        this.currentUser = user;
    }
}
