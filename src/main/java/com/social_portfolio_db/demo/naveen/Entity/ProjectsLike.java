package com.social_portfolio_db.demo.naveen.Entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;

import java.time.LocalDateTime;

import jakarta.persistence.Column;

// CREATE TABLE project_likes (
//     id BIGINT AUTO_INCREMENT PRIMARY KEY,
//     user_id BIGINT,
//     project_id BIGINT,
//     liked_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
//     UNIQUE (user_id, project_id),
//     FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
//     FOREIGN KEY (project_id) REFERENCES projects(id) ON DELETE CASCADE
// );

@Entity
@Table(name = "project_likes")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProjectsLike {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "liked_at", nullable = false)
    private LocalDateTime likedAt = LocalDateTime.now();

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private Users user;

    @ManyToOne
    @JoinColumn(name = "project_id", nullable = false)
    private Projects project;
}
