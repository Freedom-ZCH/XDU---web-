package org.example.librarysystem.config;

import lombok.RequiredArgsConstructor;
import org.example.librarysystem.entity.User;
import org.example.librarysystem.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.security.crypto.password.PasswordEncoder;

@Component
@RequiredArgsConstructor
public class AdminInitConfig implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {

        // findByUsername 返回 Optional<User>
        var adminOpt = userRepository.findByUsername("admin");

        if (adminOpt.isEmpty()) {  // ⭐ 正确判断 Optional 是否存在
            User admin = new User();
            admin.setUsername("admin");

            // ⭐ 密码加密
            admin.setPassword(passwordEncoder.encode("admin123"));

            // ⭐ 设置角色
            admin.setRole("ADMIN"); // 或者 "ROLE_ADMIN"，根据你项目的习惯

            userRepository.save(admin);
            System.out.println(">>> 已自动创建管理员：admin / admin123");
        }
    }
}
