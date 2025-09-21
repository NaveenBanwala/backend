package com.social_portfolio_db.demo.naveen.Controllers;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.social_portfolio_db.demo.naveen.Dtos.VoteCountDTO;
import com.social_portfolio_db.demo.naveen.ServicesImp.VoteService;

@RestController
@RequestMapping("/api")
public class VoteController {

    @Autowired
    private VoteService voteService;

    @PostMapping("/vote/{applicationId}")
    public ResponseEntity<?> castVote(@PathVariable Long applicationId,
                                    @RequestParam Long voterId) {
        try {
            String msg = voteService.vote(voterId, applicationId);
            return ResponseEntity.ok(Map.of("message", msg));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }


    @GetMapping("/vote-counts")
    public ResponseEntity<?> getVoteCount() {
        List<VoteCountDTO> count = voteService.countVotes();
        return ResponseEntity.ok(count);
    }
}

