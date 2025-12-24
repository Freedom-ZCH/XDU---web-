package org.example.librarysystem.service;

import org.example.librarysystem.entity.Book;
import org.example.librarysystem.repository.BookRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class BookService {
    private final BookRepository bookRepo;

    public BookService(BookRepository bookRepo) {
        this.bookRepo = bookRepo;
    }

    public List<Book> listAll() {
        return bookRepo.findAll();
    }

    public List<Book> search(String keyword) {
        if (keyword == null || keyword.isBlank()) return listAll();
        return bookRepo.findByTitleContainingOrAuthorContainingOrCategoryContaining(
                keyword, keyword, keyword);
    }

    public void save(Book b) {
        // ✅ 新增图书时自动设置创建时间
        if (b.getId() == null && b.getCreateTime() == null) {
            b.setCreateTime(LocalDateTime.now());
        }
        bookRepo.save(b);
    }

    public Book get(Long id) {
        return bookRepo.findById(id).orElse(null);
    }

    public void delete(Long id) {
        bookRepo.deleteById(id);
    }
}
