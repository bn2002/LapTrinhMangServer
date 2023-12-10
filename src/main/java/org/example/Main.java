package org.example;
import org.example.services.ClientHandlerService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

import java.net.*;
import java.util.ArrayList;

@SpringBootApplication
public class Main {
    public static ApplicationContext context;
    public static void main(String[] args) {
        context = SpringApplication.run(Main.class, args);
        ArrayList<Socket> clients = new ArrayList<>();
        try (ServerSocket serversocket = new ServerSocket(5001)) {
            System.out.println("Server is started...");
            while (true) {
                Socket socket = serversocket.accept();
                clients.add(socket);
                ClientHandlerService ThreadServer = new ClientHandlerService(socket, clients);
                ThreadServer.start();
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

    }
}