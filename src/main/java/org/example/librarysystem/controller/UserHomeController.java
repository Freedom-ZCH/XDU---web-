package org.example.librarysystem.controller;

import jakarta.servlet.http.HttpSession;
import org.example.librarysystem.entity.Borrow;
import org.example.librarysystem.entity.User;
import org.example.librarysystem.service.BorrowService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/user")
public class UserHomeController {

    private final BorrowService borrowService;

    // 和管理员仪表盘保持一致，从配置里读逾期天数，默认 30
    @Value("${library.overdue-days:30}")
    private int overdueDays;

    public UserHomeController(BorrowService borrowService) {
        this.borrowService = borrowService;
    }

    @GetMapping("/dashboard")
    public String dashboard(HttpSession session, Model model) {
        User u = (User) session.getAttribute("user");
        if (u == null) {
            return "redirect:/auth"; // 或者 /login，看你现在用哪个
        }

        List<Borrow> allMyBorrows = borrowService.myBorrows(u.getId());

        long totalCount = allMyBorrows.size();
        long borrowingCount = allMyBorrows.stream()
                .filter(b -> "BORROWED".equals(b.getStatus()))
                .count();
        long returnedCount = allMyBorrows.stream()
                .filter(b -> "RETURNED".equals(b.getStatus()))
                .count();

        LocalDateTime overdueThreshold = LocalDateTime.now().minusDays(overdueDays);
        long overdueCount = allMyBorrows.stream()
                .filter(b -> "BORROWED".equals(b.getStatus()))
                .filter(b -> b.getBorrowTime() != null && b.getBorrowTime().isBefore(overdueThreshold))
                .count();

        // 最近 5 条记录（按借阅时间倒序）
        List<Borrow> recentBorrows = allMyBorrows.stream()
                .sorted(Comparator.comparing(
                        Borrow::getBorrowTime,
                        Comparator.nullsLast(Comparator.naturalOrder())
                ).reversed())
                .limit(5)
                .collect(Collectors.toList());

        model.addAttribute("totalCount", totalCount);
        model.addAttribute("borrowingCount", borrowingCount);
        model.addAttribute("returnedCount", returnedCount);
        model.addAttribute("overdueCount", overdueCount);
        model.addAttribute("overdueDays", overdueDays);
        model.addAttribute("recentBorrows", recentBorrows);

        return "user/dashboard";
    }
}
