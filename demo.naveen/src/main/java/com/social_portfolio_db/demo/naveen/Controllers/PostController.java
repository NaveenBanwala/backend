package com.social_portfolio_db.demo.naveen.Controllers;

import java.util.List;
import java.util.Map;
import java.util.HashMap;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.social_portfolio_db.demo.naveen.Dtos.PostRequest;
import com.social_portfolio_db.demo.naveen.Entity.Post;
import com.social_portfolio_db.demo.naveen.Entity.Users;
import com.social_portfolio_db.demo.naveen.Jpa.PostRepository;
import com.social_portfolio_db.demo.naveen.Jpa.UserJpa;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class PostController {

    private final PostRepository postRepo;
    private final UserJpa userRepo;

    // Removed test endpoints and all debug logging

    // Create a new post
    @PostMapping
    public ResponseEntity<?> createPost(@RequestBody PostRequest request, @AuthenticationPrincipal UserDetails userDetails) {
        try {
            log.info("Creating post for user: {}", userDetails.getUsername());
            log.info("Post content: {}", request.getContent());
            
            if (request.getContent() == null || request.getContent().trim().isEmpty()) {
                return ResponseEntity.badRequest().body("Post content cannot be empty");
            }
            
            Users user = userRepo.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found: " + userDetails.getUsername()));
            
            log.info("Found user: {}", user.getUsername());
            
            Post post = new Post();
            post.setContent(request.getContent().trim());
            post.setUser(user);
            
            Post savedPost = postRepo.save(post);
            log.info("Post created successfully with ID: {}", savedPost.getId());
            
            return ResponseEntity.ok("Post created successfully!");
        } catch (Exception e) {
            log.error("Error creating post: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error creating post: " + e.getMessage());
        }
    }

    // View own posts
    @GetMapping("/me")
    public ResponseEntity<?> getMyPosts(@AuthenticationPrincipal UserDetails userDetails) {
        try {
            log.info("Fetching posts for user: {}", userDetails.getUsername());
            
            // Find user
            Users user = userRepo.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found: " + userDetails.getUsername()));
            
            log.info("Found user: {} (ID: {})", user.getUsername(), user.getId());
            
            // Fetch posts
            List<Post> posts = postRepo.findByUserIdOrderByCreatedAtDesc(user.getId());
            log.info("Found {} posts for user ID: {}", posts.size(), user.getId());
            
            // Create a safe response structure to avoid serialization issues
            List<Map<String, Object>> safePosts = posts.stream()
                .map(post -> {
                    Map<String, Object> postMap = new HashMap<>();
                    postMap.put("id", post.getId());
                    postMap.put("content", post.getContent());
                    postMap.put("createdAt", post.getCreatedAt());
                    
                    // Safely add user info
                    if (post.getUser() != null) {
                        Map<String, Object> userMap = new HashMap<>();
                        userMap.put("id", post.getUser().getId());
                        userMap.put("username", post.getUser().getUsername());
                        userMap.put("email", post.getUser().getEmail());
                        userMap.put("bio", post.getUser().getBio());
                        userMap.put("location", post.getUser().getLocation());
                        userMap.put("profilePicUrl", post.getUser().getProfilePicUrl());
                        userMap.put("resumeUrl", post.getUser().getResumeUrl());
                        userMap.put("createdAt", post.getUser().getCreatedAt());
                        
                        // Add skills as simple strings
                        if (post.getUser().getSkills() != null) {
                            List<String> skillNames = post.getUser().getSkills().stream()
                                .map(skill -> skill.getSkillName())
                                .collect(java.util.stream.Collectors.toList());
                            userMap.put("skills", skillNames);
                        }
                        
                        postMap.put("user", userMap);
                    }
                    
                    return postMap;
                })
                .collect(java.util.stream.Collectors.toList());
            
            log.info("Successfully serialized {} posts for user: {}", posts.size(), user.getUsername());
            return ResponseEntity.ok(safePosts);
            
        } catch (Exception e) {
            log.error("Error fetching posts for user {}: ", userDetails.getUsername(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of(
                    "error", "Failed to fetch posts",
                    "message", e.getMessage(),
                    "timestamp", java.time.LocalDateTime.now()
                ));
        }
    }

    // Like a post
    /*
    @PostMapping("/{postId}/like")
    public ResponseEntity<?> likePost(@PathVariable Long postId, @AuthenticationPrincipal UserDetails userDetails) {
        try {
            Users user = userRepo.findByEmail(userDetails.getUsername()).orElseThrow();
            Post post = postRepo.findById(postId).orElseThrow();
            
            // Check if already liked
            if (post.getLikedBy().contains(user)) {
                // Unlike
                post.getLikedBy().remove(user);
                postRepo.save(post);
                return ResponseEntity.ok("Post unliked");
            } else {
                // Like
                post.getLikedBy().add(user);
                postRepo.save(post);
                return ResponseEntity.ok("Post liked");
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error liking post: " + e.getMessage());
        }
    }
    */

    // Get like count for a post
    /*
    @GetMapping("/{postId}/likes")
    public ResponseEntity<?> getPostLikes(@PathVariable Long postId) {
        try {
            Post post = postRepo.findById(postId).orElseThrow();
            return ResponseEntity.ok(Map.of(
                "postId", postId,
                "likeCount", post.getLikedBy().size(),
                "likedBy", post.getLikedBy().stream()
                    .map(u -> Map.of("id", u.getId(), "username", u.getUsername()))
                    .collect(java.util.stream.Collectors.toList())
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error getting post likes: " + e.getMessage());
        }
    }
    */

    // Delete own post
    @DeleteMapping("/{postId}")
    public ResponseEntity<?> deleteOwnPost(@PathVariable Long postId, @AuthenticationPrincipal UserDetails userDetails) {
        Users user = userRepo.findByEmail(userDetails.getUsername()).orElseThrow();
        Post post = postRepo.findById(postId).orElseThrow();
        if (post.getUser().getId() != user.getId()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("You can only delete your own posts");
        }
        postRepo.delete(post);
        return ResponseEntity.ok("Post deleted");
    }

    // View posts of another user (public)
    @GetMapping("/user/{userId}")
    public ResponseEntity<?> getUserPosts(@PathVariable Long userId) {
        try {
            log.info("Fetching posts for user ID: {}", userId);
            
            // First check if user exists
            if (!userRepo.existsById(userId)) {
                log.warn("User with ID {} not found", userId);
                return ResponseEntity.ok(List.of());
            }
            
            List<Post> posts = postRepo.findByUserIdOrderByCreatedAtDesc(userId);
            log.info("Found {} posts for user ID: {}", posts.size(), userId);
            
            return ResponseEntity.ok(posts);
        } catch (Exception e) {
            log.error("Error fetching posts for user ID {}: ", userId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of(
                    "error", "Failed to fetch posts",
                    "message", e.getMessage()
                ));
        }
    }

    @GetMapping
    public ResponseEntity<?> getAllPosts() {
        List<Post> posts = postRepo.findAllByOrderByCreatedAtDesc();
        List<Map<String, Object>> safePosts = posts.stream()
            .map(post -> {
                Map<String, Object> postMap = new HashMap<>();
                postMap.put("id", post.getId());
                postMap.put("content", post.getContent());
                postMap.put("createdAt", post.getCreatedAt());
                if (post.getUser() != null) {
                    Map<String, Object> userMap = new HashMap<>();
                    userMap.put("id", post.getUser().getId());
                    userMap.put("username", post.getUser().getUsername());
                    userMap.put("email", post.getUser().getEmail());
                    userMap.put("bio", post.getUser().getBio());
                    userMap.put("location", post.getUser().getLocation());
                    userMap.put("profilePicUrl", post.getUser().getProfilePicUrl());
                    userMap.put("resumeUrl", post.getUser().getResumeUrl());
                    userMap.put("createdAt", post.getUser().getCreatedAt());
                    if (post.getUser().getSkills() != null) {
                        List<String> skillNames = post.getUser().getSkills().stream()
                            .map(skill -> skill.getSkillName())
                            .collect(java.util.stream.Collectors.toList());
                        userMap.put("skills", skillNames);
                    }
                    postMap.put("user", userMap);
                }
                return postMap;
            })
            .collect(java.util.stream.Collectors.toList());
        return ResponseEntity.ok(safePosts);
    }
    
    @GetMapping("/all")
    public ResponseEntity<?> getAllPostsGlobal() {
        List<Post> posts = postRepo.findAllByOrderByCreatedAtDesc();
        List<Map<String, Object>> safePosts = posts.stream()
            .map(post -> {
                Map<String, Object> postMap = new HashMap<>();
                postMap.put("id", post.getId());
                postMap.put("content", post.getContent());
                postMap.put("createdAt", post.getCreatedAt());
                if (post.getUser() != null) {
                    Map<String, Object> userMap = new HashMap<>();
                    userMap.put("id", post.getUser().getId());
                    userMap.put("username", post.getUser().getUsername());
                    userMap.put("email", post.getUser().getEmail());
                    userMap.put("bio", post.getUser().getBio());
                    userMap.put("location", post.getUser().getLocation());
                    userMap.put("profilePicUrl", post.getUser().getProfilePicUrl());
                    userMap.put("resumeUrl", post.getUser().getResumeUrl());
                    userMap.put("createdAt", post.getUser().getCreatedAt());
                    if (post.getUser().getSkills() != null) {
                        List<String> skillNames = post.getUser().getSkills().stream()
                            .map(skill -> skill.getSkillName())
                            .collect(java.util.stream.Collectors.toList());
                        userMap.put("skills", skillNames);
                    }
                    postMap.put("user", userMap);
                }
                return postMap;
            })
            .collect(java.util.stream.Collectors.toList());
        return ResponseEntity.ok(safePosts);
    }
    
}


