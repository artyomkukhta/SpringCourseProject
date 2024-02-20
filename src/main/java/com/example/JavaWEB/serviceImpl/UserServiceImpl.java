package com.example.JavaWEB.serviceImpl;

import com.example.JavaWEB.model.Card;
import com.example.JavaWEB.model.Role;
import com.example.JavaWEB.model.RoleClass;
import com.example.JavaWEB.model.User;
import com.example.JavaWEB.repository.RoleRepository;
import com.example.JavaWEB.repository.UserRepository;
import com.example.JavaWEB.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service("userService")
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private PasswordEncoder passwordEncoder;
    @Autowired
    public UserServiceImpl(UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public Iterable<User> findAll(){
        return userRepository.findAll();
    }

    public Optional<User> findById(Long id){
        Optional<User> user = userRepository.findById(id);
        if (user.isPresent()) return user;
        return null;
    }
    public User findByUsername(String username){
        User user = userRepository.findByUsername(username);
        if (user!=null) return user;
        return null;
    }

    public Iterable<Card> findCardsByUserId(Long userId){

        Optional<User> user = userRepository.findById(userId);
        List<Card> cards = user.get().getCards();
        return cards;
    }

    public List<Card> findAllUserCards(){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = userRepository.findByUsername(auth.getName());
        return user.getCards();
    }

    public boolean add(User user){
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        RoleClass role = roleRepository.findRoleClassByRole(Role.USER);
        user.setRole(role);
        userRepository.save(user);
        return true;
    }

    public boolean delete(User user){
        userRepository.delete(user);
        return true;
    }
    public boolean deleteById(Long id){
        userRepository.deleteById(id);
        return true;
    }

    public boolean edit(User user, String username, String email, String password){
        user.setUsername(username);
        user.setPassword(password);
        user.setEmail(email);
        userRepository.save(user);
        return true;
    }
    public boolean editById(Long id, String username, String email, String password){
        Optional<User> user = userRepository.findById(id);
        user.get().setUsername(username);
        user.get().setPassword(password);
        user.get().setEmail(email);
        userRepository.save(user.get());
        return true;
    }
    public User findUserByAuth(){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return userRepository.findByUsername(auth.getName());
    }
}