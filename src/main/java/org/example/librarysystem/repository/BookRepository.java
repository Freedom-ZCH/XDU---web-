package org.example.librarysystem.repository;

import org.example.librarysystem.entity.Book;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BookRepository extends JpaRepository<Book, Long> {
    List<Book> findByTitleContainingOrAuthorContainingOrCategoryContaining(
            String t, String a, String c);
}
