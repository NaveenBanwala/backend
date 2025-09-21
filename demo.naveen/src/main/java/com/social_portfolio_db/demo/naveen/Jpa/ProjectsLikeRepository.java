package com.social_portfolio_db.demo.naveen.Jpa;

import org.springframework.data.jpa.repository.JpaRepository;

import com.social_portfolio_db.demo.naveen.Entity.ProjectsLike;

public interface ProjectsLikeRepository extends JpaRepository<ProjectsLike, Long> {
    boolean existsByUserIdAndProjectId(Long userId, Long projectId);
    long countByProjectId(Long projectId);
}

