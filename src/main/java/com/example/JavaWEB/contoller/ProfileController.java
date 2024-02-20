package com.example.JavaWEB.contoller;

import com.example.JavaWEB.serviceImpl.UserServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class ProfileController {
    private final UserServiceImpl userServiceImpl;

    @Autowired
    public ProfileController(UserServiceImpl userServiceImpl) {
        this.userServiceImpl = userServiceImpl;
    }

    @GetMapping("/userId{userId}")
    public String servicesList(@PathVariable(value = "userId") Long id, Model model) {
        model.addAttribute("user", userServiceImpl.findById(id).get());
        model.addAttribute("cards", userServiceImpl.findCardsByUserId(id));
        return "profile";
    }

    @GetMapping("/allusers")
    public String allUsers(Model model) {
        model.addAttribute("users", userServiceImpl.findAll());
        return "usersList";
    }
}
