package com.iavorskyi.controllers;

import com.iavorskyi.domain.Role;
import com.iavorskyi.domain.User;
import com.iavorskyi.repos.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/user")
@PreAuthorize("hasAuthority('ADMIN')")
public class UserController {
    @Autowired
    UserRepo userRepo;

    @GetMapping
    public String userList(Model model){
        model.addAttribute("userList", userRepo.findAll());
        return "userList";
    }

    @GetMapping("{user}")
    public String userList(@PathVariable User user, Model model){
        model.addAttribute("user", user);
        model.addAttribute("roles", Role.values());
        return "userEdit";
    }

    @PostMapping
    public String userEdit(@RequestParam ("userId") User user,
                           @RequestParam String username,
                           @RequestParam Map<String, String> form){
        user.setUsername(username);
        Set<String> roles= Arrays.stream(Role.values())
                                 .map(Role::name)
                                 .collect(Collectors.toSet());//Весь перечень ролей переводим в список String-ов
        user.getRoles().clear();
        form.keySet().forEach(key -> {if(
            roles.contains(key)){
            user.getRoles().add(Role.valueOf(key));
        }
        });
        userRepo.save(user);
        return "redirect:/user";
    }
}
