package org.example.librarysystem.controller;

import jakarta.servlet.http.HttpSession;
import org.example.librarysystem.entity.Book;
import org.example.librarysystem.entity.Borrow;
import org.example.librarysystem.entity.User;
import org.example.librarysystem.service.BookService;
import org.example.librarysystem.service.BorrowService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Comparator;
import java.util.List;

@Controller
@RequestMapping("/user")
public class UserBookController {

    private final BookService bookService;
    private final BorrowService borrowService;

    public UserBookController(BookService bookService,
                              BorrowService borrowService) {
        this.bookService = bookService;
        this.borrowService = borrowService;
    }

    /** 工具方法：获取当前登录用户，没有就跳回 /auth */
    private User mustLogin(HttpSession session) {
        return (User) session.getAttribute("user");
    }

    /** ▌图书浏览页：/user/books */
    @GetMapping("/books")
    public String booksPage(@RequestParam(required = false) String keyword,
                            Model model,
                            HttpSession session) {

        User u = mustLogin(session);
        if (u == null) return "redirect:/auth";

        List<Book> books = bookService.search(keyword);
        model.addAttribute("books", books);
        model.addAttribute("keyword", keyword);

        return "user/books";   // 对应 templates/user/books.html
    }

    /** ▌借书：/user/borrow/{bookId} */
    @GetMapping("/borrow/{bookId}")
    public String borrowBook(@PathVariable Long bookId,
                             HttpSession session,
                             Model model) {

        User u = mustLogin(session);
        if (u == null) return "redirect:/auth";

        String result = borrowService.borrowBook(u.getId(), bookId);

        // 借书失败：带着错误信息回到图书列表页
        if (!"OK".equals(result)) {
            model.addAttribute("error", result);
            List<Book> books = bookService.search(null);
            model.addAttribute("books", books);
            model.addAttribute("keyword", null);
            return "user/books";
        }

        // 成功：刷新图书列表
        return "redirect:/user/books";
    }

    /** ▌我的借阅页：/user/my-borrows */
    @GetMapping("/my-borrows")
    public String myBorrowsPage(HttpSession session, Model model) {
        User u = mustLogin(session);
        if (u == null) return "redirect:/auth";

        List<Borrow> list = borrowService.myBorrows(u.getId());

        // 按借阅时间倒序（最近的在前面）
        list.sort(Comparator.comparing(Borrow::getBorrowTime,
                Comparator.nullsLast(Comparator.reverseOrder())));

        model.addAttribute("borrows", list);
        return "user/my_borrows";  // 对应 templates/user/my_borrows.html
    }

    /** ▌还书：/user/return/{borrowId} */
    @GetMapping("/return/{borrowId}")
    public String returnBook(@PathVariable Long borrowId,
                             HttpSession session,
                             Model model) {

        User u = mustLogin(session);
        if (u == null) return "redirect:/auth";

        String result = borrowService.returnBook(u.getId(), borrowId);

        if (!"OK".equals(result)) {
            model.addAttribute("error", result);
            List<Borrow> list = borrowService.myBorrows(u.getId());
            list.sort(Comparator.comparing(Borrow::getBorrowTime,
                    Comparator.nullsLast(Comparator.reverseOrder())));
            model.addAttribute("borrows", list);
            return "user/my_borrows";
        }

        return "redirect:/user/my-borrows";
    }
}
