package com.iavorskyi.domain;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;


class UserTest {

    @Test
    void isAdmin() {
        User user = new User();
        Set<Role> set = new HashSet<>();
        set.add(Role.USER);
        user.setRoles(set);
        assertFalse(user.isAdmin());
    }

    @Test
    void isActive() {
        User user = new User();
        assertFalse(user.isActive());
    }

    @Test
    void getRoles() {
    }
}