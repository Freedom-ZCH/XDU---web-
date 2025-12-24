package org.example.librarysystem.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name="book")
public class Book {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String author;
    private String category;
    private Integer total;
    private Integer available;

    // ✅ 新增：创建时间，用于统计“本周新增图书”
    private LocalDateTime createTime;
}
