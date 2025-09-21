package com.social_portfolio_db.demo.naveen.ServicesImp;

import com.social_portfolio_db.demo.naveen.Entity.*;
import com.social_portfolio_db.demo.naveen.Jpa.*;
import com.social_portfolio_db.demo.naveen.Services.VotingContestService;

import jakarta.transaction.Transactional;

import com.social_portfolio_db.demo.naveen.Dtos.VoteDTO;
import com.social_portfolio_db.demo.naveen.Dtos.VotingContestDTO;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

@Service
public class VotingContestServiceImp implements VotingContestService {

    @Autowired
    private VotingContestRepository contestRepo;

    @Autowired
    private VotingApplicationRepository applicationRepo;

    @Autowired
    private VoteRepository voteRepo;

    @Autowired
    private UserJpa userRepo;

    @Override
    public List<VotingContestDTO> findAllContestents() {
        return voteRepo.findAllContenders();
    }

    @Override
    public ActiveContest updateContest(ActiveContest contest) {
        if (contest == null || contest.getId() == null) {
            throw new IllegalArgumentException("Contest or Contest ID must not be null");
        }
        if (!contestRepo.existsById(contest.getId())) {
            throw new RuntimeException("Contest not found");
        }
        return contestRepo.save(contest);
    }

    @Override
    public VotingApplication applyForContest(Long userId, String email, String imageUrl) {
        try {
            if (userId == null || email == null || imageUrl == null || imageUrl.isEmpty()) {
                throw new IllegalArgumentException("User ID, Email, and Image URL must not be null or empty");
            }

            Users user = userRepo.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

            if (!user.getEmail().equals(email)) {
                throw new IllegalArgumentException("Email does not match user");
            }

            ActiveContest contest = getActiveContest();
            if (contest == null) {
                throw new RuntimeException("No active contest found");
            }

            List<VotingApplication> existing = applicationRepo.findByUserAndContest(user, contest);
            if (existing != null && !existing.isEmpty()) {
                throw new IllegalStateException("User has already applied for this contest");
            }

            VotingApplication application = new VotingApplication();
            application.setUser(user);
            application.setContest(contest);
            application.setImageUrl(imageUrl);
            application.setStatus("PENDING");
            application.setAppliedAt(LocalDateTime.now());

            return applicationRepo.save(application);

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to apply for contest: " + e.getMessage(), e);
        }
    }

    @Override
    public List<VotingApplication> getApplicationsForAdmin() {
        ActiveContest contest = getActiveContest();
        if (contest == null) return Collections.emptyList();
        return applicationRepo.findByContestAndStatus(contest, "PENDING");
    }

    @Override
    public List<VotingApplication> getApprovedApplications() {
        ActiveContest contest = getActiveContest();
        if (contest == null) return Collections.emptyList();
        return applicationRepo.findByContestAndStatus(contest, "APPROVED");
    }

    @Override  @Transactional // for avoid duplicate voting
    public VoteDTO voteForApplication(Long voterId, Long applicationId) {
        if (!isVotingOpen()) {
            throw new IllegalStateException("Voting is not open.");
        }

        Users voter = userRepo.findById(voterId)
            .orElseThrow(() -> new RuntimeException("Voter not found"));

        VotingApplication application = applicationRepo.findById(applicationId)
            .orElseThrow(() -> new RuntimeException("Application not found"));

        if (application.getUser().getId().equals(voterId)) {
            throw new IllegalArgumentException("You cannot vote for yourself.");
        }

        if (voteRepo.existsByVoterAndApplication(voter, application)) {
            throw new IllegalStateException("You have already voted for this application.");
        }

        Vote vote = new Vote();
        vote.setVoter(voter);
        vote.setApplication(application);
        vote.setVotedAt(LocalDateTime.now());

        Vote savedVote = voteRepo.save(vote);

        return new VoteDTO(
            savedVote.getId(),
            savedVote.getVoter().getId(),
            savedVote.getVoter().getUsername(),
            savedVote.getApplication().getId(),
            savedVote.getVotedAt()
        );
    }

    @Override
    public List<VotingApplication> getTopContestants(int topN) {
        List<VotingApplication> allApplications = applicationRepo.findAll();
        if (allApplications == null || allApplications.isEmpty()) {
            return Collections.emptyList();
        }

        for (VotingApplication app : allApplications) {
            int totalVotes = voteRepo.findByApplication(app).size();
            app.setVotes(totalVotes);
        }

        allApplications.sort((a, b) -> Integer.compare(b.getVotes(), a.getVotes()));

        return allApplications.subList(0, Math.min(topN, allApplications.size()));
    }

    @Override
    public ResponseEntity<?> removeContestById(Long id) {
        contestRepo.deleteById(id);
        return ResponseEntity.ok(Map.of("message", "Contest removed successfully"));
    }

    @Override
    public Map<String, Object> getAllContest() {
        List<ActiveContest> contests = contestRepo.findAll();
        if (contests != null && !contests.isEmpty()) {
            for (ActiveContest contest : contests) {
                if (contest.isActive()) {
                    Map<String, Object> currContest = new HashMap<>();
                    currContest.put("id", contest.getId());
                    currContest.put("title", contest.getTitle());
                    currContest.put("description", contest.getDescription());
                    currContest.put("startTime", contest.getStartTime());
                    currContest.put("endTime", contest.getEndTime());
                    return currContest;
                }
            }
        }
        return Map.of("message", "No Contest found");
    }

    @Override
    public ActiveContest getActiveContest() {
    List<ActiveContest> contests = contestRepo.findByIsActiveTrue();

        LocalDateTime now = LocalDateTime.now();

        for (ActiveContest contest : contests) {
            if (contest.isActive() &&
                contest.getStartTime() != null &&
                contest.getEndTime() != null &&
                now.isAfter(contest.getStartTime()) &&
                now.isBefore(contest.getEndTime())) {
                return contest;
            }
        }

        return contests.stream()
            .filter(ActiveContest::isActive)
            .findFirst()
            .orElse(null);
    }

    @Override
    public boolean isVotingOpen() {
        ActiveContest contest = getActiveContest();
        if (contest == null) return false;
        LocalDateTime now = LocalDateTime.now();
        return contest.isActive() &&
                contest.getStartTime() != null &&
                contest.getEndTime() != null &&
                now.isAfter(contest.getStartTime()) &&
                now.isBefore(contest.getEndTime());
    }

    @Override
    public String getVotingEndTime() {
        ActiveContest contest = getActiveContest();
        if (contest == null || contest.getEndTime() == null) return null;
        return contest.getEndTime().toString();
    }

    @Override
    public void saveContest(ActiveContest contest) {
        contestRepo.save(contest);
    }
}
