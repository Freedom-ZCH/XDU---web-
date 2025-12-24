package org.example.librarysystem.service;

import org.example.librarysystem.entity.Book;
import org.example.librarysystem.entity.Borrow;
import org.example.librarysystem.repository.BookRepository;
import org.example.librarysystem.repository.BorrowRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class BorrowService {

    private final BorrowRepository borrowRepo;
    private final BookRepository bookRepo;

    // 每个用户最多同时借几本
    private static final int MAX_BORROW_LIMIT = 5;

    public BorrowService(BorrowRepository borrowRepo,
                         BookRepository bookRepo) {
        this.borrowRepo = borrowRepo;
        this.bookRepo = bookRepo;
    }

    /** ▌借书逻辑（普通用户和你说的“图书管理界面里的借阅”都走它） */
    public String borrowBook(Long userId, Long bookId) {

        Book book = bookRepo.findById(bookId).orElse(null);
        if (book == null) return "图书不存在";

        if (book.getAvailable() == null || book.getAvailable() <= 0)
            return "库存不足";

        // 限借数量：只能借 MAX_BORROW_LIMIT 本未还的
        long nowBorrowing = borrowRepo.countByUserIdAndStatus(userId, "BORROWED");
        if (nowBorrowing >= MAX_BORROW_LIMIT)
            return "已达到最大借书数量（" + MAX_BORROW_LIMIT + " 本）";

        // 同一本书不能重复借（没还之前）
        boolean alreadyBorrowed = borrowRepo
                .findByUserIdAndBookIdAndStatus(userId, bookId, "BORROWED")
                .isPresent();
        if (alreadyBorrowed)
            return "你已借过这本书且尚未归还";

        // 扣减库存
        book.setAvailable(book.getAvailable() - 1);
        bookRepo.save(book);

        // 生成借阅记录
        Borrow br = new Borrow();
        br.setUserId(userId);
        br.setBookId(bookId);
        br.setBorrowTime(LocalDateTime.now());
        br.setStatus("BORROWED");

        borrowRepo.save(br);
        return "OK";
    }

    /** ▌还书逻辑（普通用户“我的借阅”页里的归还按钮） */
    public String returnBook(Long userId, Long borrowId) {
        Borrow br = borrowRepo.findById(borrowId).orElse(null);
        if (br == null) return "借阅记录不存在";

        // 不能还别人的书
        if (!br.getUserId().equals(userId))
            return "不能归还他人的借阅记录";

        if (!"BORROWED".equals(br.getStatus()))
            return "该记录已归还";

        // 还书 +1 库存
        Book book = bookRepo.findById(br.getBookId()).orElse(null);
        if (book != null) {
            book.setAvailable(book.getAvailable() + 1);
            bookRepo.save(book);
        }

        br.setStatus("RETURNED");
        br.setReturnTime(LocalDateTime.now());
        borrowRepo.save(br);

        return "OK";
    }

    /** ▌当前用户所有借阅记录（给“我的借阅”和用户仪表盘用） */
    public List<Borrow> myBorrows(Long userId) {
        return borrowRepo.findByUserId(userId);
    }

    /** ▌管理员查看所有借阅记录（admin 那边用，如果你有） */
    public List<Borrow> allBorrows() {
        return borrowRepo.findAll();
    }
}
