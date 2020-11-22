package com.iavorskyi.controllers;

import com.iavorskyi.domain.Role;
import com.iavorskyi.domain.User;
import com.iavorskyi.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Map;

@Controller
@RequestMapping("/user")
@PreAuthorize("hasAuthority('ADMIN')")
public class UserController {
    final
    UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public String userList(Model model, @AuthenticationPrincipal User user) {
        model.addAttribute("userList", userService.findAll());
        model.addAttribute("user", user);
        return "userList";
    }

    @GetMapping("{user}/edit")
    public String userEdit(@PathVariable User user, Model model) {
        model.addAttribute("user", user);
        model.addAttribute("roles", Role.values());
        return "userEdit";
    }

    @PatchMapping("/{id}")
    public String userEdit(@PathVariable("id") long id,
                           @ModelAttribute ("user") @Valid User user,
                           BindingResult bindingResult,
                           @RequestParam Map<String, String> form) {
        if(bindingResult.hasErrors()){
            return "userEdit";
        }
        userService.update(user, form);
        return "redirect:/user";
    }

    @DeleteMapping("/{id}")
    public String userDelete(@PathVariable("id") long id){
        userService.delete(id);
        return "redirect:/user";
    }
}
