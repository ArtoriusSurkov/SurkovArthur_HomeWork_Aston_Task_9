package org.example.services;

import org.example.entities.UserEntity;
import org.example.repositories.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
@Transactional
class UserServiceTest {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Test
    void testSaveAndFindByEmail() {
        UserEntity user = new UserEntity("Name1","Name1@mail.ru",18);
        userService.save(user);

        Optional<UserEntity> found = userService.getByEmail("Name1@mail.ru");
        assertTrue(found.isPresent());
        assertEquals("Name1", found.get().getName());
        assertEquals(18, found.get().getAge());
    }

    @Test
    void testGetByEmail_NotFound() {
        Optional<UserEntity> found = userService.getByEmail("notfound@mail.ru");
        assertTrue(found.isEmpty());
    }

    @Test
    void testGetAll() {
        userService.save(new UserEntity("User1","user1@mail.ru",20));
        userService.save(new UserEntity("User2","user2@mail.ru",25));

        List<UserEntity> users = userService.getAll();
        assertEquals(2, users.size());
    }

    @Test
    void testDeleteByEmail() {
        UserEntity user = new UserEntity("ToDelete","delete@mail.ru",30);
        userService.save(user);

        userService.deleteByEmail("delete@mail.ru");

        Optional<UserEntity> deleted = userService.getByEmail("delete@mail.ru");
        assertTrue(deleted.isEmpty());
    }

    @Test
    void testUpdateUserByEmail_UserExists() {
        UserEntity user = new UserEntity("OldName","update@mail.ru",18);
        userService.save(user);

        UserEntity newData = new UserEntity("NewName","new@mail.ru",21);
        Optional<UserEntity> updatedOpt = userService.updateUserByEmail("update@mail.ru", newData);

        assertTrue(updatedOpt.isPresent());
        UserEntity updated = updatedOpt.get();
        assertEquals("NewName", updated.getName());
        assertEquals("new@mail.ru", updated.getEmail());
        assertEquals(21, updated.getAge());
    }

    @Test
    void testUpdateUserByEmail_UserNotFound() {
        UserEntity newData = new UserEntity("Name7", "Name7@mail.ru",18);

        Optional<UserEntity> result = userService.updateUserByEmail("nonexistent@mail.ru", newData);
        assertTrue(result.isEmpty());
    }
}
