package org.example.configs;

import org.example.dtos.RoomDto;
import org.example.dtos.UserDto;
import org.example.entities.Room;
import org.example.entities.User;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ModelMapperConfig {
    public void ModelMapperConfig(){};
    @Bean
    public ModelMapper modelMapper() {
        ModelMapper modelMapper = new ModelMapper();
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT).setAmbiguityIgnored(true);
        modelMapper.createTypeMap(Room.class, RoomDto.class);
        return modelMapper;
    }
}
