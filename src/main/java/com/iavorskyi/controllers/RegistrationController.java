package com.iavorskyi.controllers;

import com.iavorskyi.domain.Role;
import com.iavorskyi.domain.User;
import com.iavorskyi.repos.UserRepo;
import com.iavorskyi.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.Collections;

@Controller
public class RegistrationController {
    @Autowired
    UserService userService;
    @Autowired
    UserRepo userRepo;

    @GetMapping("/registration")
    public String registration(@ModelAttribute User user){
        return "registration";
    }

    @PostMapping("/registration")
    public String addUser(@ModelAttribute User user, Model model){
        User userFromDB = userRepo.findByUsername(user.getUsername());
        if(userFromDB!=null){
            model.addAttribute("message", "Пользователь уже существует!");
        } else{
            user.setActive(true);
            user.setRoles(Collections.singleton(Role.USER));
            userService.addUser(user);
        }

        return "redirect:login";
    }
}
