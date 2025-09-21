package com.social_portfolio_db.demo.naveen.Jpa;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.EntityGraph;

import com.social_portfolio_db.demo.naveen.Entity.Projects;

public interface ProjectsRepository extends JpaRepository<Projects, Long> {
    List<Projects> findByUserId(Long userId);

    @EntityGraph(attributePaths = {"user"})
    Optional<Projects> findById(Long id);
}

