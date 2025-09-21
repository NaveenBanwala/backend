package com.social_portfolio_db.demo.naveen.Jpa;

import com.social_portfolio_db.demo.naveen.Entity.QuizSubmission;
import org.springframework.data.jpa.repository.JpaRepository;


import java.util.List;

// @Repository
public interface QuizSubmissionRepository extends JpaRepository<QuizSubmission, Long> {

    // Get all submissions for a given contest
    List<QuizSubmission> findByContestId(Long contestId);

    // Get submissions ordered by score for ranking
    List<QuizSubmission> findByContestIdOrderByTotalScoreDesc(Long contestId);
}
