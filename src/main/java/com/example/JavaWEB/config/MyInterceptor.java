package com.example.JavaWEB.config;

import com.example.JavaWEB.model.User;
import com.example.JavaWEB.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component("myInterceptor")
public class MyInterceptor extends HandlerInterceptorAdapter {
    private final UserRepository userRepository;

    @Autowired
    public MyInterceptor(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = userRepository.findByUsername(auth.getName());
       // if (user == null) response.sendRedirect(request.getContextPath() + "/error");
        if (user != null) request.setAttribute("userId", user.getId());

        return super.preHandle(request, response, handler);
    }
}