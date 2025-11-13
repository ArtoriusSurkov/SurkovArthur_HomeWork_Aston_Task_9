package org.example.utils;

import static org.junit.jupiter.api.Assertions.*;

import org.example.dto.UserDto;
import org.example.entities.UserEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

class MappingUtilsTest {

    private MappingUtils mappingUtils;

    @BeforeEach
    void setUp() {
        mappingUtils = new MappingUtils();
    }

    @Test
    void testMapToUserDto() {
        UserEntity userEntity = new UserEntity("Name1" ,"Name1@mail.ru", 18);
        UserDto dto = mappingUtils.mapToUserDto(userEntity);
        assertNotNull(dto);
        assertEquals(userEntity.getName(), dto.getName());
        assertEquals(userEntity.getEmail(), dto.getEmail());
        assertEquals(userEntity.getAge(), dto.getAge());
    }

    @Test
    void testMapToUserEntity() {
        UserDto dto = new UserDto("Name2" ,"Name2@mail.ru", 18);
        UserEntity entity = mappingUtils.mapToUSerEntity(dto);
        assertNotNull(entity);
        assertEquals(dto.getName(), entity.getName());
        assertEquals(dto.getEmail(), entity.getEmail());
        assertEquals(dto.getAge(), entity.getAge());
    }
}
