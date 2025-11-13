package org.example.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class UserDto {
    private Integer id;
    private String name;
    private String email;
    private Integer age;
    private LocalDateTime createdAt;

    public UserDto() {

    }

    public UserDto(String name, String email, Integer age) {
        this.name = name;
        this.email = email;
        this.age = age;
    }
}
