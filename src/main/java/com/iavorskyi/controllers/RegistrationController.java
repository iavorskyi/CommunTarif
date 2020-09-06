package com.iavorskyi.controllers;

import com.iavorskyi.domain.Role;
import com.iavorskyi.domain.User;
import com.iavorskyi.repos.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.Collections;

@Controller
public class RegistrationController {
    @Autowired
    UserRepo userRepo;

    @GetMapping("/registration")
    public String registration(){
        return "registration";
    }

    @PostMapping("/registration")
    public String addUser(User user, Model model){
        System.out.println(user);
        User userFromDB = userRepo.findByUsername(user.getUsername());
        if(userFromDB!=null){
            model.addAttribute("message", "Пользователь уже существует!");
        } else{
            user.setActive(true);
            user.setRoles(Collections.singleton(Role.USER));
            System.out.println(user.getUsername());
            userRepo.save(user);
            System.out.println("User is saved");

        }

        return "redirect:login";
    }
}
