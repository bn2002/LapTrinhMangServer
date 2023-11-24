package org.example;
import org.example.controllers.ClientHandlerController;
import org.example.utils.HibernateUtil;
import org.hibernate.SessionFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

import java.net.*;
import java.util.ArrayList;
import java.util.HashMap;

@SpringBootApplication
public class Main {
    public static ApplicationContext context;
    public static void main(String[] args) {
        context = SpringApplication.run(Main.class, args);
        SessionFactory factory = HibernateUtil.getSessionFactory();
        ArrayList<Socket> clients = new ArrayList<>();
        HashMap<Socket, String> clientNameList = new HashMap<Socket, String>();
        try (ServerSocket serversocket = new ServerSocket(5001)) {
            System.out.println("Server is started...");
            while (true) {
                Socket socket = serversocket.accept();
                clients.add(socket);
                ClientHandlerController ThreadServer = new ClientHandlerController(socket, clients, clientNameList);
                ThreadServer.start();
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

    }
}