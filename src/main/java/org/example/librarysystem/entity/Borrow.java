package org.example.librarysystem.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name="borrow")
public class Borrow {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;
    private Long bookId;

    private LocalDateTime borrowTime;
    private LocalDateTime returnTime;

    private String status; // BORROWED / RETURNED
}
