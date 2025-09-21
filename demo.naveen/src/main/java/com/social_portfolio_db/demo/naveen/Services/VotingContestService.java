package com.social_portfolio_db.demo.naveen.Services;

import com.social_portfolio_db.demo.naveen.Entity.*;
import com.social_portfolio_db.demo.naveen.Dtos.VoteDTO;
import com.social_portfolio_db.demo.naveen.Dtos.VotingContestDTO;

import java.util.List;
import java.util.Map;
import org.springframework.http.ResponseEntity;

public interface VotingContestService {

    VotingApplication applyForContest(Long userId, String email, String imageUrl);

    List<VotingApplication> getApplicationsForAdmin();

    List<VotingApplication> getApprovedApplications();

    VoteDTO voteForApplication(Long voterId, Long applicationId);

    List<VotingApplication> getTopContestants(int topN);

    ActiveContest getActiveContest();

    boolean isVotingOpen();

    String getVotingEndTime();

    void saveContest(ActiveContest contest);

    ResponseEntity<?> removeContestById(Long contestId);

    ActiveContest updateContest(ActiveContest contest);

    Map<String, Object> getAllContest();

    List<VotingContestDTO> findAllContestents();
}
