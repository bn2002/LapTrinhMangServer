package org.example.routes;

import org.example.controllers.UserController;
import org.example.models.Request;

import java.io.PrintWriter;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.Socket;
import java.util.HashMap;
import java.util.function.Function;

public class Router {
    private static HashMap<String, HashMap<String, Method>> routes = new HashMap<>();

    static {
        // User routes
        HashMap<String, Method> userRoutes = new HashMap<>();
        try {
            userRoutes.put("login", UserController.class.getMethod("login", Request.class));
            userRoutes.put("register", UserController.class.getMethod("register", Request.class));
        } catch (Exception e) {

        }
        routes.put("user", userRoutes);


        //
    }

    public static void handlerRequest(Socket socket, Request request) {
        String controller = request.getController();
        String method = request.getMethod();

        HashMap<String, Method> methods = routes.get(controller);
        Method function = methods.get(method);
        try {
            PrintWriter printWriter;
            String response = (String) function.invoke(null, request);
            printWriter = new PrintWriter(socket.getOutputStream(), true);
            printWriter.println(response);
        } catch(Exception e) {

        }

    }
}
