package com.social_portfolio_db.demo.naveen.Jpa;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.social_portfolio_db.demo.naveen.Entity.ActiveContest;

public interface VotingContestRepository extends JpaRepository<ActiveContest, Long > {

    List<ActiveContest> findByIsActiveTrue();

}
