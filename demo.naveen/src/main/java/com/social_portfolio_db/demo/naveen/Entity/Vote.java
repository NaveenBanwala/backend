package com.social_portfolio_db.demo.naveen.Entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
public class Vote {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "voter_id")
    private Users voter;

    @ManyToOne
    @JoinColumn(name = "application_id")
    private VotingApplication application;

    private LocalDateTime votedAt;

    // Getters and setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Users getVoter() { return voter; }
    public void setVoter(Users voter) { this.voter = voter; }
    public VotingApplication getApplication() { return application; }
    public void setApplication(VotingApplication application) { this.application = application; }
    public LocalDateTime getVotedAt() { return votedAt; }
    public void setVotedAt(LocalDateTime votedAt) { this.votedAt = votedAt; }
} 