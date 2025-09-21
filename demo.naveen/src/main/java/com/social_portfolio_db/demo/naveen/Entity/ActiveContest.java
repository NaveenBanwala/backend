package com.social_portfolio_db.demo.naveen.Entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.social_portfolio_db.demo.naveen.Enum.ContestType;

@Entity
@Getter@Setter 
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "active_contest")
public class ActiveContest { 

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String title;
    private String description;

    @Enumerated(EnumType.STRING)
    private ContestType type;


    @OneToMany(mappedBy = "contest", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<VotingApplication> applications = new ArrayList<>();


    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private boolean isActive;

    // Getters and setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public LocalDateTime getStartTime() { return startTime; }
    public void setStartTime(LocalDateTime startTime) { this.startTime = startTime; }
    public LocalDateTime getEndTime() { return endTime; }
    public void setEndTime(LocalDateTime endTime) { this.endTime = endTime; }
    public boolean isActive() { return isActive; }
    public void setActive(boolean active) { isActive = active; }
} 