package com.social_portfolio_db.demo.naveen.Jpa;

import com.social_portfolio_db.demo.naveen.Entity.VotingApplication;
import com.social_portfolio_db.demo.naveen.Entity.ActiveContest;
import com.social_portfolio_db.demo.naveen.Entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface VotingApplicationRepository extends JpaRepository<VotingApplication, Long> {

    List<VotingApplication> findByContestAndStatus(ActiveContest contest, String status);

    List<VotingApplication> findByUserAndContest(Users user, ActiveContest contest);

    Optional<VotingApplication> findByUserId(Long userId);

    @Query("SELECT v.status FROM VotingApplication v WHERE v.user.id = :userId ORDER BY v.id DESC")
    List<String> findApplicationStatusesByUserIdOrderByIdDesc(Long userId);
}
