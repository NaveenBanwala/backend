package com.social_portfolio_db.demo.naveen.Controllers;

import com.social_portfolio_db.demo.naveen.Entity.ActiveContest;
import com.social_portfolio_db.demo.naveen.Entity.QuizSubmission;
import com.social_portfolio_db.demo.naveen.Enum.ContestType;
import com.social_portfolio_db.demo.naveen.Jpa.UserJpa;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/quizzes")
public class QuizContestController {

    @Autowired
    private com.social_portfolio_db.demo.naveen.Jpa.ActiveContestRepository contestRepo;

    @Autowired
    private com.social_portfolio_db.demo.naveen.Jpa.QuizSubmissionRepository submissionRepo;

    @Autowired
    private UserJpa userRepo;

    //  ADMIN APIs

    // Create a quiz contest (ADMIN)
    @PostMapping("/create")
    // @hasRole("ADMIN")
    public ActiveContest createQuizContest(@RequestBody ActiveContest contest) {
        contest.setType(ContestType.QUIZ);
        contest.setStartTime(LocalDateTime.now());
        contest.setActive(true);
        return contestRepo.save(contest);
    }

    // Get all contests (ADMIN)
    @GetMapping("/all")
    public List<ActiveContest> getAllQuizzes() {
        return contestRepo.findByType(ContestType.QUIZ);
    }

    // Delete a contest (ADMIN)
    @DeleteMapping("/delete/{id}")
    public String deleteQuiz(@PathVariable Long id) {
        contestRepo.deleteById(id);
        return "Quiz contest deleted successfully.";
    }

    // Get all submissions of a quiz contest (ADMIN)
    @GetMapping("/submissions/{contestId}")
    public List<QuizSubmission> getSubmissions(@PathVariable Long contestId) {
        return submissionRepo.findByContestId(contestId);
    }

    // ================= USER APIs =================

    // Get all active quizzes (USER)
    @GetMapping("/active")
    public List<ActiveContest> getActiveQuizzes() {
        return contestRepo.findByTypeAndIsActiveTrue(ContestType.QUIZ);
    }

    // User submits quiz (marks)
    @PostMapping("/submit")
    public String submitQuiz(@RequestBody QuizSubmission submission) {
        submission.setSubmittedAt(LocalDateTime.now());

        QuizSubmission saved = submissionRepo.save(submission);
        return "Quiz submitted with ID: " + saved.getId();
    }

    // Get user result/rank (USER)
    @GetMapping("/result/{contestId}/{userId}")
    public Map<String, Object> getUserResult(@PathVariable Long contestId, @PathVariable Long userId) {
        List<QuizSubmission> submissions = submissionRepo.findByContestIdOrderByTotalScoreDesc(contestId);
        int rank = 1;

        for (QuizSubmission s : submissions) {
            if (s.getUserId().getId().equals(userId)) {
                Map<String, Object> result = new HashMap<>();
                result.put("score", s.getTotalScore());
                result.put("rank", rank);
                return result;
            }
            rank++;
        }

        throw new RuntimeException("Submission not found.");
    }
}
