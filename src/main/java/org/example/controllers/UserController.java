package org.example.controllers;

import com.google.gson.Gson;
import org.example.models.Request;
import org.example.models.User;

public class UserController {
    public static String login(Request request) {
        System.out.println("Login nayyyyyyyy");
        return "Login nayyyyyyyy";
    }

    public static String register(Request request) {
        Gson gson = new Gson();
        User user = gson.fromJson(request.getData(), User.class);
        System.out.println(user);
        return "";
    }

    public static boolean logout() {
        return true;
    }
}
