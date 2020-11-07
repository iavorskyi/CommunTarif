package com.iavorskyi.service;

import com.iavorskyi.domain.User;
import com.iavorskyi.repos.UserRepo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@TestPropertySource(locations="classpath:applicationTest.properties")
class UserServiceTest {
    @Autowired
    UserRepo userRepo;
    @Autowired
    UserService userService;
    User user;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setActive(true);
        user.setPassword("12345");
        user.setUsername("username1");
    }

    @Test
    void testAddUser() {
        userService.addUser(user);
        assertNotNull(userRepo.findById(user.getId()));
        userRepo.delete(user);
    }

    @Test
    void findAll() {
        userService.addUser(user);
        List<User> users = userService.findAll();
        assertEquals(1, users.size());
        userRepo.delete(user);
    }

    @Test
    void saveUser() {
    }

    @Test
    void getOne() {
        userRepo.save(user);
        User testUser = userService.getOne(user.getId());
        assertNotNull(testUser);
        assertEquals(user.getUsername(), testUser.getUsername());
        userRepo.delete(user);
    }

    @Test
    void saveProfile() {
        userService.saveProfile("1", user);
        assertNotNull(userRepo.findById(user.getId()));
        userRepo.delete(user);

    }

    @Test
    void update() {
    }

    @Test
    void delete() {
    }
}