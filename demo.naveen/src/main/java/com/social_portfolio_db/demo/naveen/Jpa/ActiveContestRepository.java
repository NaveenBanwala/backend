package com.social_portfolio_db.demo.naveen.Jpa;

import com.social_portfolio_db.demo.naveen.Entity.ActiveContest;
import org.springframework.data.jpa.repository.JpaRepository;

import com.social_portfolio_db.demo.naveen.Enum.ContestType;

import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ActiveContestRepository extends JpaRepository<ActiveContest, Long> {

    // Find contests by type (QUIZ / VOTING)
    List<ActiveContest> findByType(ContestType type);

    // Find only active quiz contests
    List<ActiveContest> findByTypeAndIsActiveTrue(ContestType type);
}
