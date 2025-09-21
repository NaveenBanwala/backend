package com.social_portfolio_db.demo.naveen.Controllers;

import org.springframework.web.bind.annotation.RestController;

import com.social_portfolio_db.demo.naveen.ServicesImp.LikeService;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.http.ResponseEntity;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/likes")
@RequiredArgsConstructor
public class LikeController {

    private final LikeService likeService;

    @PostMapping("/project/{projectId}")
    public ResponseEntity<?> likeProject(@PathVariable Long projectId, @RequestParam Long userId) {
        likeService.likeProject(userId, projectId);
        return ResponseEntity.ok("Project liked");
    }

    @PostMapping("/profile/{likedUserId}")
    public ResponseEntity<?> likeProfile(@PathVariable Long likedUserId, @RequestParam Long likedById) {
        likeService.likeProfile(likedById, likedUserId);
        return ResponseEntity.ok("Profile liked");
    }

    @GetMapping("/project/{projectId}/count")
    public ResponseEntity<Long> getProjectLikeCount(@PathVariable Long projectId) {
        return ResponseEntity.ok(likeService.getProjectLikeCount(projectId));
    }

    @GetMapping("/profile/{userId}/count")
    public ResponseEntity<Long> getProfileLikeCount(@PathVariable Long userId) {
        return ResponseEntity.ok(likeService.getProfileLikeCount(userId));
    }

    @PostMapping("/post/{postId}")
    public ResponseEntity<?> likePost(@PathVariable Long postId, @RequestParam Long userId) {
        try {
            likeService.likePost(userId, postId);
            return ResponseEntity.ok("Post liked");
        } catch (RuntimeException e) {
            if (e.getMessage().contains("not found")) {
                return ResponseEntity.status(404).body(e.getMessage());
            }
            return ResponseEntity.badRequest().body("Error liking post: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Internal server error: " + e.getMessage());
        }
    }

    @PostMapping("/post/{postId}/unlike")
    public ResponseEntity<?> unlikePost(@PathVariable Long postId, @RequestParam Long userId) {
        try {
            likeService.unlikePost(userId, postId);
            return ResponseEntity.ok("Post unliked");
        } catch (RuntimeException e) {
            if (e.getMessage().contains("not found")) {
                return ResponseEntity.status(404).body(e.getMessage());
            }
            return ResponseEntity.badRequest().body("Error unliking post: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Internal server error: " + e.getMessage());
        }
    }

    @GetMapping("/post/{postId}/count")
    public ResponseEntity<Long> getPostLikeCount(@PathVariable Long postId) {
        return ResponseEntity.ok(likeService.getPostLikeCount(postId));
    }

    @PostMapping("/profile/{likedUserId}/unlike")
    public ResponseEntity<?> unlikeProfile(@PathVariable Long likedUserId, @RequestParam Long likedById) {
        likeService.unlikeProfile(likedById, likedUserId);
        return ResponseEntity.ok("Profile unliked");
    }

    @GetMapping("/profile/{likedUserId}/status")
    public ResponseEntity<?> getProfileLikeStatus(@PathVariable Long likedUserId, @RequestParam Long likedById) {
        boolean liked = likeService.hasUserLikedProfile(likedById, likedUserId);
        return ResponseEntity.ok(java.util.Map.of("liked", liked));
    }

    @GetMapping("/post/{postId}/status")
    public ResponseEntity<?> getPostLikeStatus(@PathVariable Long postId, @RequestParam Long userId) {
        boolean liked = likeService.hasUserLikedPost(userId, postId);
        return ResponseEntity.ok(java.util.Map.of("liked", liked));
    }
}

