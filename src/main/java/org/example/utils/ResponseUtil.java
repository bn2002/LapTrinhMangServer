package org.example.utils;

import com.google.gson.Gson;
import org.example.models.Response;

public class ResponseUtil {
    public static String buidResponse(Response response) {
        Gson gson = new Gson();
        String json = gson.toJson(response);
        return json;
    }
}
