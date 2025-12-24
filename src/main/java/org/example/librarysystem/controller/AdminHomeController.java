package org.example.librarysystem.controller;

import org.example.librarysystem.entity.Book;
import org.example.librarysystem.entity.Borrow;
import org.example.librarysystem.repository.BorrowRepository;
import org.example.librarysystem.service.BookService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Controller
@RequestMapping("/admin")
public class AdminHomeController {

    private final BookService bookService;
    private final BorrowRepository borrowRepository;

    // ✅ 从 application.yml 读取逾期天数（默认 30 天）
    @Value("${library.overdue-days:30}")
    private int overdueDays;

    public AdminHomeController(BookService bookService,
                               BorrowRepository borrowRepository) {
        this.bookService = bookService;
        this.borrowRepository = borrowRepository;
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model) {

        // 1. 图书相关统计
        List<Book> allBooks = bookService.listAll();

        // 总册数 = 所有图书 total 字段求和（如果 total 为空算 0）
        long totalBooks = allBooks.stream()
                .map(Book::getTotal)
                .filter(Objects::nonNull)
                .mapToLong(Integer::longValue)
                .sum();

        // 本周新增图书数：createTime 在过去 7 天内的数量
        LocalDateTime oneWeekAgo = LocalDateTime.now().minusDays(7);
        long newBooksThisWeek = allBooks.stream()
                .map(Book::getCreateTime)
                .filter(Objects::nonNull)
                .filter(t -> t.isAfter(oneWeekAgo))
                .count();

        // 2. 借阅相关统计
        long borrowingCount = borrowRepository.countByStatus("BORROWED");   // 在借图书数量
        long returnedCount  = borrowRepository.countByStatus("RETURNED");   // 已还图书数量

        long borrowingUserCount = borrowRepository.countDistinctUserIdByStatus("BORROWED");
        long returnedUserCount  = borrowRepository.countDistinctUserIdByStatus("RETURNED");

        // 3. 逾期统计：借出超过 overdueDays 天且未归还
        LocalDateTime overdueThreshold = LocalDateTime.now().minusDays(overdueDays);
        List<Borrow> borrowingList = borrowRepository.findByStatus("BORROWED");

        long overdueCount = borrowingList.stream()
                .filter(b -> b.getBorrowTime() != null
                        && b.getBorrowTime().isBefore(overdueThreshold))
                .count();

        long overdueUserCount = borrowingList.stream()
                .filter(b -> b.getBorrowTime() != null
                        && b.getBorrowTime().isBefore(overdueThreshold))
                .map(Borrow::getUserId)
                .distinct()
                .count();

        // 4. 数据塞进模型，给 Thymeleaf 用
        model.addAttribute("totalBooks", totalBooks);
        model.addAttribute("newBooksThisWeek", newBooksThisWeek);

        model.addAttribute("borrowingCount", borrowingCount);
        model.addAttribute("borrowingUserCount", borrowingUserCount);

        model.addAttribute("returnedCount", returnedCount);
        model.addAttribute("returnedUserCount", returnedUserCount);

        model.addAttribute("overdueCount", overdueCount);
        model.addAttribute("overdueUserCount", overdueUserCount);

        return "admin/dashboard";
    }
}
