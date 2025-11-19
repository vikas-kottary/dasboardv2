package com.people10.dashboard.repository;

import com.people10.dashboard.model.Comment;
import com.people10.dashboard.model.Report;
import com.people10.dashboard.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
    
    // Find comments by report
    List<Comment> findByReport(Report report);
    
    // Find comments by report ID
    List<Comment> findByReportId(Long reportId);
    
    // Find comments by user
    List<Comment> findByUser(User user);
    
    // Find comments by user ID
    List<Comment> findByUserId(Long userId);
    
    // Find comments by report ID ordered by creation date (newest first)
    List<Comment> findByReportIdOrderByCreatedAtDesc(Long reportId);
    
    // Find comments by user ID ordered by creation date (newest first)
    List<Comment> findByUserIdOrderByCreatedAtDesc(Long userId);
    
    // Custom query to find comments with user and report details
    @Query("SELECT c FROM Comment c JOIN FETCH c.user JOIN FETCH c.report WHERE c.report.id = :reportId ORDER BY c.createdAt DESC")
    List<Comment> findByReportIdWithUserAndReportDetails(@Param("reportId") Long reportId);
    
    // Custom query to find comments by user with report details
    @Query("SELECT c FROM Comment c JOIN FETCH c.user JOIN FETCH c.report WHERE c.user.id = :userId ORDER BY c.createdAt DESC")
    List<Comment> findByUserIdWithUserAndReportDetails(@Param("userId") Long userId);
    
    // Count comments by report ID
    long countByReportId(Long reportId);
    
    // Count comments by user ID
    long countByUserId(Long userId);
}
