package com.social_portfolio_db.demo.naveen.ServicesImp;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.social_portfolio_db.demo.naveen.Dtos.VoteCountDTO;
import com.social_portfolio_db.demo.naveen.Entity.Users;
import com.social_portfolio_db.demo.naveen.Entity.Vote;
import com.social_portfolio_db.demo.naveen.Entity.VotingApplication;
import com.social_portfolio_db.demo.naveen.Jpa.UserJpa;
import com.social_portfolio_db.demo.naveen.Jpa.VoteRepository;
import com.social_portfolio_db.demo.naveen.Jpa.VotingApplicationRepository;

@Service
public class VoteService {

    @Autowired
    private VoteRepository voteRepo;

    @Autowired
    private UserJpa userRepo;

    @Autowired
    private VotingApplicationRepository applicationRepo;

    public String vote(Long voterId, Long applicationId) {
        if (voteRepo.existsByVoterIdAndApplicationId(voterId, applicationId)) {
            throw new IllegalStateException("You already voted for this contestant.");
        }

        Users voter = userRepo.findById(voterId)
                .orElseThrow(() -> new RuntimeException("Voter not found"));

        VotingApplication application = applicationRepo.findById(applicationId)
                .orElseThrow(() -> new RuntimeException("Contestant application not found"));

        Vote vote = new Vote();
        vote.setVoter(voter);
        vote.setApplication(application);
        vote.setVotedAt(LocalDateTime.now());

        voteRepo.save(vote);

        return "Vote submitted successfully.";
    }

    public List<VoteCountDTO> countVotes() {
        return voteRepo.countVotesForAllApplications();
    }
}
