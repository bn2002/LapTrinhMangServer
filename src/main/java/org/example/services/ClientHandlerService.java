package org.example.services;

import org.example.entities.Request;
import org.example.routes.Router;
import org.example.utils.JsonUtil;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;

public class ClientHandlerService extends Thread{
    private Socket socket;
    private ArrayList<Socket> clients;
    private Router router;
    public ClientHandlerService(Socket socket, ArrayList<Socket> clients) {
        this.socket = socket;
        this.clients = clients;
        this.router = new Router();
    }

    @Override
    public void run() {
        try {
            BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            while (true) {
                String outputString = input.readLine();
                if (outputString.equals("logout")) {
                    throw new SocketException();
                }

                Request request = JsonUtil.getObject(outputString, Request.class);
                if(request != null) {
                    this.router.handlerRequest(socket, request);
                } else {
                    this.router.sendError(socket, "Yêu cầu không hợp lệ");
                }
            }
        } catch (SocketException e) {
            // Thông báo đóng connect
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}
