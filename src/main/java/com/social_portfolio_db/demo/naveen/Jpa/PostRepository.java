package com.social_portfolio_db.demo.naveen.Jpa;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.social_portfolio_db.demo.naveen.Entity.Post;

public interface PostRepository extends JpaRepository<Post, Long> {
    List<Post> findByUserId(Long userId);
    List<Post> findByUserIdOrderByCreatedAtDesc(Long userId);
    List<Post> findAllByOrderByCreatedAtDesc();
    boolean existsByLikedBy_IdAndId(Long userId, Long postId);
    long countByLikedBy_Id(Long postId);
}

