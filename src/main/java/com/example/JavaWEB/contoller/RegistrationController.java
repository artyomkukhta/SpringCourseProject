package com.example.JavaWEB.contoller;

import com.example.JavaWEB.model.User;
import com.example.JavaWEB.repository.UserRepository;
import com.example.JavaWEB.serviceImpl.UserServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import javax.validation.Valid;
import java.util.Map;

@Controller
public class RegistrationController {

    private UserRepository userRepository;
    private UserServiceImpl userServiceImpl;
    @Autowired
    public RegistrationController(UserRepository userRepository, UserServiceImpl userServiceImpl) {
        this.userRepository = userRepository;
        this.userServiceImpl = userServiceImpl;
    }


    @GetMapping("/registration")
    public String registration(Model model){
        model.addAttribute("user", new User());
        return "registration";
    }

    @PostMapping("/registration")
    public String addUser(@Valid User user, Errors errors, Map<String, Object> model) {

        User userfromdb = userServiceImpl.findByUsername(user.getUsername());
        if (userfromdb != null){
            model.put("message", "User already exists");
            return "registration";
        }

        if (errors.hasErrors()){
            return "registration";
        }
        userServiceImpl.add(user);

        return "redirect:/login";
    }
}
