package org.example.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.entities.Response;

public class JsonUtil {
    public static String buidResponse(Response response) {
        ObjectMapper objectMapper = new ObjectMapper();
        String json;
        try {
            json = objectMapper.writeValueAsString(response);
        } catch (Exception e) {
            json = "{\"status\":\"error\",\"message\":\"Lỗi json response\",\"data\":\"\"}";
        }

        return json;
    }

    public static <T> T getObject(String json, Class<T> type) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            T object = objectMapper.readValue(json, type);
            return object;
        } catch(Exception e) {
            System.out.println(e.getMessage());
            return null;
        }
    }
}
