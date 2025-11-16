package org.example.services;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.example.entities.UserEntity;
import org.example.repositories.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    public void save(UserEntity userEntity) {
        try {
            if (userEntity == null) {
                throw new IllegalArgumentException("UserEntity не может быть null");
            }
            userRepository.save(userEntity);
        } catch (Exception e) {
            throw new RuntimeException("Ошибка при сохранении пользователя", e);
        }
    }

    public Optional<UserEntity> getByEmail(String email) {
        try {
            if (email == null || email.isEmpty()) {
                throw new IllegalArgumentException("Email не может быть null");
            }
            return userRepository.findByEmail(email);
        } catch (Exception e) {
            throw new RuntimeException("Ошибка при получении пользователя по email: " + email, e);
        }
    }

    public List<UserEntity> getAll() {
        try {
            return userRepository.findAll();
        } catch (Exception e) {
            throw new RuntimeException("Ошибка при получении списка пользователей", e);
        }
    }

    @Transactional
    public void deleteByEmail(String email) {
        try {
            if (email == null || email.isEmpty()) {
                throw new IllegalArgumentException("Email не может быть null");
            }
            userRepository.deleteByEmail(email);
        } catch (Exception e) {
            throw new RuntimeException("Ошибка при удалении пользователя с email: " + email, e);
        }
    }

    public Optional<UserEntity> updateUserByEmail(String email, UserEntity newData) {
        try {
            if (email == null || email.isEmpty()) {
                throw new IllegalArgumentException("Email не может быть null");
            }
            if (newData == null) {
                throw new IllegalArgumentException("NewData не может быть null");
            }

            Optional<UserEntity> existingUserOpt = userRepository.findByEmail(email);

            if (existingUserOpt.isEmpty()) {
                return Optional.empty();
            }

            UserEntity existingUser = existingUserOpt.get();

            existingUser.setName(newData.getName());
            existingUser.setEmail(newData.getEmail());
            existingUser.setAge(newData.getAge());

            userRepository.save(existingUser);
            return Optional.of(existingUser);
        } catch (Exception e) {
            throw new RuntimeException("Ошибка при обновлении пользователя с email: " + email, e);
        }
    }
}
