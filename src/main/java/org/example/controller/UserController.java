package org.example.controller;

import lombok.AllArgsConstructor;
import org.example.dto.UserDto;
import org.example.entities.UserEntity;
import org.example.services.UserService;
import org.example.utils.MappingUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/users")
@AllArgsConstructor
public class UserController {

    private final UserService userService;
    private final MappingUtils mappingUtils;

    @GetMapping
    public ResponseEntity<List<UserDto>> getAllUser() {
        try {
            List<UserDto> users = userService.getAll().stream().map(mappingUtils::mapToUserDto).toList();
            return ResponseEntity.ok(users);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(null);
        }
    }


    @GetMapping("/{email}")
    public ResponseEntity<UserDto> getByEmail(@PathVariable String email) {
        try {
            return userService.getByEmail(email).map(user -> ResponseEntity.ok(mappingUtils.mapToUserDto(user))).orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            return ResponseEntity.status(500).build();
        }
    }

    @PostMapping
    public ResponseEntity<UserDto> createUser(@RequestBody UserDto userDto) {
        try {
            UserEntity entity = mappingUtils.mapToUSerEntity(userDto);
            userService.save(entity);
            return ResponseEntity.status(201).body(mappingUtils.mapToUserDto(entity));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(null);
        }
    }

    @PostMapping("/{email}")
    public ResponseEntity<UserDto> updateUser(@PathVariable String email, @RequestBody UserDto userDto) {
        try {
            UserEntity entity = mappingUtils.mapToUSerEntity(userDto);
            Optional<UserEntity> update = userService.updateUserByEmail(email, entity);
            return update.map(updateEntity -> ResponseEntity.ok(mappingUtils.mapToUserDto(updateEntity))).orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            return ResponseEntity.status(500).body(null);
        }
    }

    @DeleteMapping("/{email}")
    public ResponseEntity<Void> deleteUser(@PathVariable String email) {
        try {
            userService.deleteByEmail(email);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.status(500).build();
        }
    }
}
