package com.example.JavaWEB.security;

import com.example.JavaWEB.model.Permission;
import com.example.JavaWEB.model.User;
import com.example.JavaWEB.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component("userSecurity")
public class UserSecurity {
    private final UserRepository userRepository;

    @Autowired
    public UserSecurity(UserRepository userRepository) {
        this.userRepository = userRepository;
    }
//@PostAuthorize()
    public boolean hasUserId(Authentication authentication, Long userId) {
        //User user = (User) authentication.getPrincipal();
        User user = userRepository.findByUsername(authentication.getName());
        if (user==null) return false;
        if (userId.equals(user.getId())) return true;
        return false;
    }

    public boolean isAdmin() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = userRepository.findByUsername(authentication.getName());
        if (user.getRole().getPermissions().contains(Permission.DEVELOPERS_WRITE)) return true;
        return false;
    }

    public boolean hasAuthority(SimpleGrantedAuthority authority) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        User user = userRepository.findByUsername(authentication.getName());
        if (user.getRole().getAuthorities().contains(authority)) return true;
        return false;
    }
}
