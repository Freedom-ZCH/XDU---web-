package org.example.librarysystem.repository;

import org.example.librarysystem.entity.Borrow;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface BorrowRepository extends JpaRepository<Borrow, Long> {

    // 当前用户所有借阅记录（给“我的借阅”和用户仪表盘用）
    List<Borrow> findByUserId(Long userId);

    // 判断该用户是否已经借了这本书且未还
    Optional<Borrow> findByUserIdAndBookIdAndStatus(Long userId, Long bookId, String status);

    // 限借用：统计用户当前借了多少本未还的
    long countByUserIdAndStatus(Long userId, String status);

    // 管理员 / 仪表盘用
    long countByStatus(String status);

    List<Borrow> findByStatus(String status);

    @Query("select count(distinct b.userId) from Borrow b where b.status = :status")
    long countDistinctUserIdByStatus(@Param("status") String status);
}
