package org.example.utils;

import org.example.dto.UserDto;
import org.example.entities.UserEntity;
import org.springframework.stereotype.Service;


@Service
public class MappingUtils {
    public UserDto mapToUserDto(UserEntity user) {
        UserDto dto = new UserDto();
        dto.setId(user.getId());
        dto.setName(user.getName());
        dto.setEmail(user.getEmail());
        dto.setAge(user.getAge());
        dto.setCreatedAt(user.getCreatedAt());
        return dto;
    }

    public UserEntity mapToUSerEntity(UserDto dto){
        UserEntity entity = new UserEntity();
        entity.setId(dto.getId());
        entity.setName(dto.getName());
        entity.setEmail(dto.getEmail());
        entity.setAge(dto.getAge());
        entity.setCreatedAt(dto.getCreatedAt());
        return entity;
    }
}
