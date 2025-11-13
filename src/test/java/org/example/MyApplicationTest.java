package org.example;

import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.springframework.boot.SpringApplication;

import static org.mockito.Mockito.mockStatic;

class MyApplicationTest {
    @Test
    void main_shouldCallSpringApplicationRun() {
        try (MockedStatic<SpringApplication> mockedSpringApp = mockStatic(SpringApplication.class)) {
            String[] args = {"arg1", "arg2"};
            MyApplication.main(args);
            mockedSpringApp.verify(() -> SpringApplication.run(MyApplication.class, args));
        }
    }
}