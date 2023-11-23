package org.example.controllers;

import com.google.gson.Gson;
import org.example.models.Request;
import org.example.routes.Router;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.HashMap;

public class ClientHandlerController extends Thread{
    private Socket socket;
    private ArrayList<Socket> clients;
    private HashMap<Socket, String> clientNameLists;

    public ClientHandlerController(Socket socket, ArrayList<Socket> clients, HashMap<Socket, String> clientNameLists) {
        this.socket = socket;
        this.clients = clients;
        this.clientNameLists = clientNameLists;
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

                if (!clientNameLists.containsKey(socket)) {
                    String[] messageString = outputString.split(":", 2);
                    clientNameLists.put(socket, messageString[0]);
                    System.out.println(messageString[0] + messageString[1]);
                    showMessageToAllClients(socket, messageString[0] + messageString[1]);
                } else {
                    String[] messageString = outputString.split(":", 2);
                    Gson gson = new Gson();
                    Request response = new Request();
                    response = gson.fromJson(messageString[1], response.getClass());
                    Router.handlerRequest(socket, response);
                    System.out.println(messageString[1]);
                    showMessageToAllClients(socket, outputString);
                }


            }
        } catch (SocketException e) {
            String printMessage = clientNameLists.get(socket) + " left the chat room";
            System.out.println(printMessage);
            showMessageToAllClients(socket, printMessage);
            clients.remove(socket);
            clientNameLists.remove(socket);
        } catch (Exception e) {
            System.out.println(e.getStackTrace());
        }
    }

    private void showMessageToAllClients(Socket sender, String outputString) {
        Socket socket;
        PrintWriter printWriter;
        int i = 0;
        while (i < clients.size()) {
            socket = clients.get(i);
            i++;
            try {
                if (socket != sender) {
                    printWriter = new PrintWriter(socket.getOutputStream(), true);
                    printWriter.println(outputString);
                }
            } catch (IOException ex) {
                System.out.println(ex);
            }
        }
    }

}
