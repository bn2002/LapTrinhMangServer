package org.example.utils;

import com.google.gson.Gson;
import org.example.entities.Response;

public class JsonUtil {
    public static String buidResponse(Response response) {
        Gson gson = new Gson();
        String json = gson.toJson(response);
        return json;
    }

    public static <T> T getObject(String json, Class<T> type) {
        try {
            Gson gson = new Gson();
            T object = gson.fromJson(json, type);
            return object;
        } catch(Exception e) {
            System.out.println(e.getMessage());
            return null;
        }
    }
}
