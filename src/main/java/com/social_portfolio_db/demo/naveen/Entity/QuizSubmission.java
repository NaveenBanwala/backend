package com.social_portfolio_db.demo.naveen.Entity;

import java.time.LocalDateTime;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "quiz_submission")
public class QuizSubmission {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private Users userId;

    private int totalScore;

    private LocalDateTime submittedAt; // Optional for tracking time

    @ManyToOne
    @JoinColumn(name = "contest_id")
    private ActiveContest contest; // THis is active contest Later need to cchange this name
}
