package org.example.librarysystem.service;

import org.example.librarysystem.entity.User;
import org.example.librarysystem.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class AuthService {
    private final UserRepository userRepo;

    public AuthService(UserRepository userRepo) {
        this.userRepo = userRepo;
    }

    public String register(String username, String password) {
        if (username == null || username.isBlank() ||
                password == null || password.isBlank()) {
            return "用户名或密码不能为空";
        }

        if (userRepo.findByUsername(username).isPresent()) {
            return "用户名已存在";
        }

        User u = new User();
        u.setUsername(username);
        u.setPassword(password); // 作业版：明文
        u.setRole("USER");
        userRepo.save(u);

        return "OK";
    }

    public User login(String username, String password) {
        return userRepo.findByUsername(username)
                .filter(u -> u.getPassword().equals(password))
                .orElse(null);
    }
}
