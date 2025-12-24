package org.example.librarysystem.controller;

import org.example.librarysystem.entity.Book;
import org.example.librarysystem.entity.User;
import org.example.librarysystem.service.BookService;
import org.example.librarysystem.service.BorrowService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/admin")
public class AdminBookController {
    private final BookService bookService;
    private final BorrowService borrowService;

    public AdminBookController(BookService bookService, BorrowService borrowService) {
        this.bookService = bookService;
        this.borrowService = borrowService;
    }

    private boolean isAdmin(HttpSession session){
        User u = (User) session.getAttribute("user");
        return u != null && "ADMIN".equals(u.getRole());
    }

    @GetMapping("/books")
    public String books(HttpSession session, Model model){
        if (!isAdmin(session)) return "redirect:/login";

        model.addAttribute("books", bookService.listAll());
        return "admin/books";
    }

    @GetMapping("/book/add")
    public String addPage(HttpSession session){
        if (!isAdmin(session)) return "redirect:/login";
        return "admin/book_add";
    }

    @PostMapping("/book/add")
    public String add(HttpSession session, Book book){
        if (!isAdmin(session)) return "redirect:/login";

        if (book.getAvailable() == null) book.setAvailable(book.getTotal());
        bookService.save(book);
        return "redirect:/admin/books";
    }

    @GetMapping("/book/edit/{id}")
    public String editPage(@PathVariable Long id, HttpSession session, Model model){
        if (!isAdmin(session)) return "redirect:/login";

        model.addAttribute("book", bookService.get(id));
        return "admin/book_edit";
    }

    @PostMapping("/book/edit")
    public String edit(HttpSession session, Book book){
        if (!isAdmin(session)) return "redirect:/login";

        bookService.save(book);
        return "redirect:/admin/books";
    }

    @GetMapping("/book/delete/{id}")
    public String delete(@PathVariable Long id, HttpSession session){
        if (!isAdmin(session)) return "redirect:/login";

        bookService.delete(id);
        return "redirect:/admin/books";
    }

    @GetMapping("/borrows")
    public String borrows(HttpSession session, Model model){
        if (!isAdmin(session)) return "redirect:/login";

        model.addAttribute("borrows", borrowService.allBorrows());
        return "admin/borrows";
    }
    @GetMapping("/books/search")
    public String search(@RequestParam String keyword, HttpSession session, Model model){
        if (!isAdmin(session)) return "redirect:/login";
        model.addAttribute("books", bookService.search(keyword));
        return "admin/books";
    }

}
