package org.example.librarysystem.controller;

import jakarta.servlet.http.HttpSession;
import org.example.librarysystem.entity.User;
import org.example.librarysystem.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Optional;

@Controller
public class AuthController {

    private final UserRepository userRepo;
    private final PasswordEncoder passwordEncoder;

    public AuthController(UserRepository userRepo,
                          PasswordEncoder passwordEncoder) {
        this.userRepo = userRepo;
        this.passwordEncoder = passwordEncoder;
    }

    /** 登录 / 注册页面 */
    @GetMapping("/auth")
    public String authPage() {
        return "auth";   // 对应 templates/auth.html
    }

    /** 登录 */
    @PostMapping("/login")
    public String doLogin(@RequestParam String username,
                          @RequestParam String password,
                          HttpSession session,
                          Model model) {

        // 仓库方法返回 Optional<User>
        Optional<User> optionalUser = userRepo.findByUsername(username);

        if (!optionalUser.isPresent()) {
            model.addAttribute("loginError", "用户不存在");
            return "auth";
        }

        User u = optionalUser.get();

        // 使用 BCrypt 校验密码
        if (!passwordEncoder.matches(password, u.getPassword())) {
            model.addAttribute("loginError", "密码错误");
            return "auth";
        }

        // 登录成功，写入 session
        session.setAttribute("user", u);

        if ("ADMIN".equals(u.getRole())) {
            return "redirect:/admin/dashboard";
        } else {
            return "redirect:/user/dashboard"; // 或 /user/books
        }
    }

    /** 注册普通用户 */
    @PostMapping("/register")
    public String doRegister(@RequestParam String username,
                             @RequestParam String password,
                             Model model) {

        Optional<User> exist = userRepo.findByUsername(username);
        if (exist.isPresent()) {
            model.addAttribute("registerError", "用户名已存在");
            return "auth";
        }

        User u = new User();
        u.setUsername(username);

        // 保存前做 BCrypt 加密
        u.setPassword(passwordEncoder.encode(password));
        u.setRole("USER");

        userRepo.save(u);

        model.addAttribute("loginSuccess", "注册成功，请登录");
        return "auth";
    }
    /**退出登录*/
    @GetMapping("/logout")
    public String logout(HttpSession session) {
        // 清空 session 登录态
        session.removeAttribute("user");

        // 也可以直接 session.invalidate(); 彻底使会话失效
        // session.invalidate();

        // 回到登录页
        return "redirect:auth";
    }
    /** 注销当前登录用户（删除数据库中的账号记录） */
    @PostMapping("/account/delete")
    public String deleteAccount(HttpSession session,
                                RedirectAttributes redirectAttributes) {

        // 1. 从 session 中取当前登录用户
        User loginUser = (User) session.getAttribute("user");
        if (loginUser == null) {
            // 没登录就想删，直接回登录页
            redirectAttributes.addFlashAttribute("loginError", "请先登录");
            return "redirect:/auth";
        }

        // 2. 再查一遍数据库，保证拿到的是最新的实体
        Optional<User> optionalUser = userRepo.findById(loginUser.getId());
        if (!optionalUser.isPresent()) {
            // 数据库里已经没有这个用户了
            session.invalidate();
            redirectAttributes.addFlashAttribute("loginError", "账号不存在或已被删除");
            return "redirect:/auth";
        }

        User dbUser = optionalUser.get();

        // 3. 从数据库删除这个用户
        userRepo.delete(dbUser);

        // 4. 清掉 session（相当于退出登录）
        session.invalidate();

        // 5. 带一个提示回登录页面
        redirectAttributes.addFlashAttribute("loginSuccess", "账号已成功注销");
        return "redirect:/auth";
    }

}
