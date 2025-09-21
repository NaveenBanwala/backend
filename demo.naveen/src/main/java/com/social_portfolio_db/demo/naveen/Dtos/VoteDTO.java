package com.social_portfolio_db.demo.naveen.Dtos;

import java.time.LocalDateTime;

public class VoteDTO {
    private Long id;
    private Long voterId;
    private String voterName;
    private Long applicationId;
    private LocalDateTime votedAt;

    public VoteDTO() {}

    public VoteDTO(Long id, Long voterId, String voterName, Long applicationId, LocalDateTime votedAt) {
        this.id = id;
        this.voterId = voterId;
        this.voterName = voterName;
        this.applicationId = applicationId;
        this.votedAt = votedAt;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getVoterId() { return voterId; }
    public void setVoterId(Long voterId) { this.voterId = voterId; }
    public String getVoterName() { return voterName; }
    public void setVoterName(String voterName) { this.voterName = voterName; }
    public Long getApplicationId() { return applicationId; }
    public void setApplicationId(Long applicationId) { this.applicationId = applicationId; }
    public LocalDateTime getVotedAt() { return votedAt; }
    public void setVotedAt(LocalDateTime votedAt) { this.votedAt = votedAt; }
} 