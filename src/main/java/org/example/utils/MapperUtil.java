package org.example.utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.dtos.RoomDto;
import org.modelmapper.ModelMapper;
import org.modelmapper.jackson.JsonNodeValueReader;

public class MapperUtil {
    public static <T> T mapFromString(String data, Class<T> type) {
        try {
            ModelMapper modelMapper = new ModelMapper();
            modelMapper.getConfiguration().addValueReader(new JsonNodeValueReader());
            JsonNode jsonNode = new ObjectMapper().readTree(data);
            T object = modelMapper.map(jsonNode, type);
            return object;
        } catch (Exception e) {
            return null;
        }
    }

    public static<T> T mapFromObject(Object object, Class<T> type) {
        ModelMapper modelMapper = new ModelMapper();
        T result = modelMapper.map(object, type);
        return result;
    }
}
