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
    void testSave_NullUser_ThrowsException() {
        Exception exception = assertThrows(RuntimeException.class, () -> userService.save(null));
        assertTrue(exception.getMessage().contains("Ошибка при сохранении пользователя"));
    }

    @Test
    void testGetByEmail_NullEmail_ThrowsException() {
        Exception exception = assertThrows(RuntimeException.class, () -> userService.getByEmail(null));
        assertTrue(exception.getMessage().contains("Ошибка при получении пользователя по email"));
    }

    @Test
    void testGetByEmail_EmptyEmail_ThrowsException() {
        Exception exception = assertThrows(RuntimeException.class, () -> userService.getByEmail(""));
        assertTrue(exception.getMessage().contains("Ошибка при получении пользователя по email"));
    }

    @Test
    void testDeleteByEmail_NullEmail_ThrowsException() {
        Exception exception = assertThrows(RuntimeException.class, () -> userService.deleteByEmail(null));
        assertTrue(exception.getMessage().contains("Ошибка при удалении пользователя с email"));
    }

    @Test
    void testUpdateUserByEmail_NullEmail_ThrowsException() {
        UserEntity newData = new UserEntity("Name1","Name1@mail.ru",18);
        Exception exception = assertThrows(RuntimeException.class, () -> userService.updateUserByEmail(null, newData));
        assertTrue(exception.getMessage().contains("Ошибка при обновлении пользователя с email"));
    }

    @Test
    void testUpdateUserByEmail_NullNewData_ThrowsException() {
        UserEntity user = new UserEntity("Name1","Name1@mail.ru",20);
        userService.save(user);

        Exception exception = assertThrows(RuntimeException.class, () -> userService.updateUserByEmail("Name1@mail.ru", null));
        assertTrue(exception.getMessage().contains("Ошибка при обновлении пользователя с email"));
    }

    @Test
    void testSaveAndFindByEmail() {
        UserEntity user1 = new UserEntity("Name1","Name1@mail.ru",18);
        UserEntity user2 = new UserEntity("Name2","Name2@mail.ru",20);
        userService.save(user1);
        userService.save(user2);

        Optional<UserEntity> found1 = userService.getByEmail("Name1@mail.ru");
        Optional<UserEntity> found2 = userService.getByEmail("Name2@mail.ru");

        assertTrue(found1.isPresent());
        assertEquals("Name1", found1.get().getName());

        assertTrue(found2.isPresent());
        assertEquals("Name2", found2.get().getName());
    }

    @Test
    void testGetByEmail_NotFound() {
        Optional<UserEntity> found = userService.getByEmail("Name3@mail.ru");
        assertTrue(found.isEmpty());
    }

    @Test
    void testGetAll() {
        userService.save(new UserEntity("Name1","Name1@mail.ru",20));
        userService.save(new UserEntity("Name2","Name2@mail.ru",25));
        userService.save(new UserEntity("Name3","Name3@mail.ru",30));

        List<UserEntity> users = userService.getAll();
        assertEquals(3, users.size());
    }

    @Test
    void testDeleteByEmail() {
        UserEntity user = new UserEntity("Name1","Name1@mail.ru",30);
        userService.save(user);

        userService.deleteByEmail("Name1@mail.ru");

        Optional<UserEntity> deleted = userService.getByEmail("Name1@mail.ru");
        assertTrue(deleted.isEmpty());
    }

    @Test
    void testUpdateUserByEmail_UserExists() {
        UserEntity user = new UserEntity("Name1","Name1@mail.ru",18);
        userService.save(user);

        UserEntity newData = new UserEntity("Name2","Name2@mail.ru",21);
        Optional<UserEntity> updatedOpt = userService.updateUserByEmail("Name1@mail.ru", newData);

        assertTrue(updatedOpt.isPresent());
        UserEntity updated = updatedOpt.get();
        assertEquals("Name2", updated.getName());
        assertEquals("Name2@mail.ru", updated.getEmail());
        assertEquals(21, updated.getAge());
    }

    @Test
    void testUpdateUserByEmail_UserNotFound() {
        UserEntity newData = new UserEntity("Name3", "Name3@mail.ru",18);

        Optional<UserEntity> result = userService.updateUserByEmail("Name3@mail.ru", newData);
        assertTrue(result.isEmpty());
    }
}
