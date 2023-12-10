package org.example.utils;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

public class SocketUtil {
    public static void sendResponse(Socket socket, String response)
    {
        try {
            PrintWriter printWriter = new PrintWriter(socket.getOutputStream(), true);
            printWriter.println(response);
        } catch (IOException e) {
        }
    }
}
