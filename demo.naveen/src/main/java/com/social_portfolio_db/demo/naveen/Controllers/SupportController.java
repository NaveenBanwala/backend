package com.social_portfolio_db.demo.naveen.Controllers;

import com.social_portfolio_db.demo.naveen.Entity.SupportMessage;
import com.social_portfolio_db.demo.naveen.Jpa.SupportMessageRepository;
import com.social_portfolio_db.demo.naveen.Jpa.UserJpa;
import com.social_portfolio_db.demo.naveen.Entity.Users;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/support")
public class SupportController {
    @Autowired private SupportMessageRepository repo;
    @Autowired private UserJpa userRepo;
    @Autowired private SimpMessagingTemplate messagingTemplate;

    @PostMapping
    public ResponseEntity<?> submitSupport(@RequestBody Map<String, String> body, @AuthenticationPrincipal UserDetails userDetails) {
        Users user = userRepo.findByEmail(userDetails.getUsername()).orElse(null);
        SupportMessage msg = new SupportMessage();
        if (user != null) {
            msg.setUserId(user.getId());
            msg.setUsername(user.getUsername());
            msg.setEmail(user.getEmail());
        } else {
            msg.setUsername(body.getOrDefault("name", ""));
            msg.setEmail(body.getOrDefault("email", ""));
        }
        msg.setMessage(body.getOrDefault("message", ""));
        msg.setCreatedAt(LocalDateTime.now());
        repo.save(msg);
        // Broadcast new message to /topic/support
        messagingTemplate.convertAndSend("/topic/support", msg);
        return ResponseEntity.ok("Submitted");
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public List<SupportMessage> getAll() {
        return repo.findAll();
    }

    @GetMapping("/my")
    public List<SupportMessage> getMySupportMessages(@AuthenticationPrincipal UserDetails userDetails) {
        Users user = userRepo.findByEmail(userDetails.getUsername()).orElse(null);
        if (user == null) return List.of();
        return repo.findByUserId(user.getId());
    }

    @PostMapping("/{id}/reply")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> reply(@PathVariable Long id, @RequestBody Map<String, String> body) {
        SupportMessage msg = repo.findById(id).orElseThrow();
        msg.setAdminReply(body.get("reply"));
        repo.save(msg);
        // Send updated message only to the user who sent the request
        messagingTemplate.convertAndSendToUser(
            msg.getEmail(), // principal (Spring will map to /user/{email}/queue/support)
            "/queue/support",
            msg
        );
        return ResponseEntity.ok("Replied");
    }
} 