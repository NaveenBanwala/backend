package com.social_portfolio_db.demo.naveen.Controllers;

import com.social_portfolio_db.demo.naveen.Entity.*;
import com.social_portfolio_db.demo.naveen.Jpa.UserJpa;
import com.social_portfolio_db.demo.naveen.Jpa.VotingApplicationRepository;
import com.social_portfolio_db.demo.naveen.Dtos.VoteDTO;
import com.social_portfolio_db.demo.naveen.Dtos.VotingContestDTO;
import com.social_portfolio_db.demo.naveen.Services.VotingContestService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;

import java.util.*;

@RestController
@RequestMapping("/api")
public class VotingContestController {

    @Autowired private VotingContestService votingContestService;
    @Autowired private VotingApplicationRepository votingApplicationRepo;
    @Autowired private UserJpa userRepo;

    @GetMapping("/voting-contestants")
    public List<VotingContestDTO> getVotingContest() {
        return votingContestService.findAllContestents();
    }

    @GetMapping("/statusOfApply")
    public ResponseEntity<String> getAppliedStatus(@RequestParam Long userId) {
        Optional<Users> userOptional = userRepo.findById(userId);
        if (userOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        }

        List<String> statuses = votingApplicationRepo.findApplicationStatusesByUserIdOrderByIdDesc(userId);
        if (statuses.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Application status not found");
        }

        return ResponseEntity.ok(statuses.get(0));
    }

    @PostMapping("/voting-contest/apply")
    public VotingApplication apply(
        @RequestParam Long userId,
        @RequestParam String email,
        @RequestParam String imageUrl
    ) {
        return votingContestService.applyForContest(userId, email, imageUrl);
    }

    @GetMapping("/applications")
    public List<VotingApplication> getApplicationsForAdmin() {
        return votingContestService.getApplicationsForAdmin();
    }

    @PostMapping("/vote")
    public VoteDTO vote(@RequestParam Long voterId, @RequestParam Long applicationId) {
        return votingContestService.voteForApplication(voterId, applicationId);
    }

    @PostMapping("/create-contest")
    public ResponseEntity<?> createContest(@RequestBody Map<String, String> body) {
        try {
            String title = body.getOrDefault("title", "");
            String description = body.getOrDefault("description", "");
            String startTimeStr = body.getOrDefault("startTime", "");
            String endTimeStr = body.getOrDefault("endTime", "");

            if (title.isEmpty() || startTimeStr.isEmpty() || endTimeStr.isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("error", "Title, startTime, and endTime are required."));
            }

            ActiveContest contest = new ActiveContest();
            contest.setTitle(title);
            contest.setDescription(description);
            contest.setStartTime(java.time.LocalDateTime.parse(startTimeStr));
            contest.setEndTime(java.time.LocalDateTime.parse(endTimeStr));
            contest.setActive(true);
            votingContestService.saveContest(contest);

            return ResponseEntity.ok(Map.of("message", "Contest created successfully!"));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(Map.of("error", e.getMessage()));
        }
    }

    @PutMapping("/update-contest")
    public ResponseEntity<?> updateContest(@RequestBody ActiveContest contest) {
        try {
            ActiveContest updated = votingContestService.updateContest(contest);
            return ResponseEntity.ok(Map.of("message", "Contest updated successfully!", "contest", updated));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/voting-contest/get-all-contests")
    public Map<String, Object> findAllContests() {
        return new HashMap<>(votingContestService.getAllContest());
    }

    @DeleteMapping("/voting-contest/remove-contest/{id}")
    public ResponseEntity<?> removeContest(@PathVariable Long id) {
        return votingContestService.removeContestById(id);
    }

    @GetMapping("/top")
    public Map<String, Object> getTopContestants() {
        List<VotingApplication> contestants = votingContestService.getTopContestants(3);
        String votingEndedAt = votingContestService.getVotingEndTime();

        List<Map<String, Object>> safeContestants = contestants.stream().map(app -> {
            Map<String, Object> map = new HashMap<>();
            map.put("id", app.getId());
            if (app.getUser() != null) {
                Map<String, Object> userMap = new HashMap<>();
                userMap.put("id", app.getUser().getId());
                userMap.put("username", app.getUser().getUsername());
                userMap.put("profilePicUrl", app.getUser().getProfilePicUrl());
                map.put("user", userMap);
            }
            map.put("imageUrl", app.getImageUrl());
            map.put("votes", app.getVotes());
            map.put("status", app.getStatus());
            return map;
        }).toList();

        return Map.of("topContestants", safeContestants, "votingEndedAt", votingEndedAt);
    }

    @GetMapping("/voting-contest/status")
    public ResponseEntity<?> getStatus() {
        ActiveContest contest = votingContestService.getActiveContest();
        if (contest == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of("error", "No active voting contest found. Please check back later."));
        }

        String votingStartTime = contest.getStartTime() != null ? contest.getStartTime().toString() : null;
        int totalApplications = votingContestService.getApplicationsForAdmin().size();

        return ResponseEntity.ok(Map.of(
            "isVotingOpen", votingContestService.isVotingOpen(),
            "votingEndedAt", votingContestService.getVotingEndTime(),
            "votingStartTime", votingStartTime,
            "totalApplications", totalApplications
        ));
    }
}
