package com.social_portfolio_db.demo.naveen.Controllers.Admin;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;

import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Map;
import java.util.HashMap;

import com.social_portfolio_db.demo.naveen.Jpa.PostRepository;
import com.social_portfolio_db.demo.naveen.Jpa.ProjectsRepository;
import com.social_portfolio_db.demo.naveen.Jpa.UserJpa;
import com.social_portfolio_db.demo.naveen.Entity.Users;
import com.social_portfolio_db.demo.naveen.Entity.Post;
import com.social_portfolio_db.demo.naveen.Entity.Projects;
import com.social_portfolio_db.demo.naveen.Dtos.UserProfileDTO;
import com.social_portfolio_db.demo.naveen.Mappers.UserProfileMapper;

@RestController
// @CrossOrigin(origins = "http://localhost:5173")
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final UserJpa userRepo;
    private final ProjectsRepository projectRepo;

    private final PostRepository postRepo;

    @GetMapping("/users")
    public ResponseEntity<List<Map<String, Object>>> getAllUsers() {
        List<Users> users = userRepo.findAll();
        List<Map<String, Object>> safeUsers = users.stream().map(user -> {
            Map<String, Object> map = new HashMap<>();
            map.put("id", user.getId());
            map.put("username", user.getUsername());
            map.put("email", user.getEmail());
            map.put("bio", user.getBio());
            map.put("location", user.getLocation());
            map.put("profilePicUrl", user.getProfilePicUrl());
            map.put("resumeUrl", user.getResumeUrl());
            map.put("createdAt", user.getCreatedAt());
            return map;
        }).toList();
        return ResponseEntity.ok(safeUsers);
    }

    @DeleteMapping("/users/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userRepo.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/projects")
    public ResponseEntity<List<Map<String, Object>>> getAllProjects() {
        List<Projects> projects = projectRepo.findAll();
        List<Map<String, Object>> safeProjects = projects.stream().map(project -> {
            Map<String, Object> map = new HashMap<>();
            map.put("id", project.getId());
            map.put("title", project.getTitle());
            map.put("description", project.getDescription());
            map.put("imageUrl", project.getImageUrl());
            map.put("createdAt", project.getCreatedAt());
            return map;
        }).toList();
        return ResponseEntity.ok(safeProjects);
    }

    @DeleteMapping("/projects/{id}")
    public ResponseEntity<Void> deleteProject(@PathVariable Long id) {
        projectRepo.deleteById(id);
        return ResponseEntity.noContent().build();
    }
    @DeleteMapping("posts/{postId}")
    public ResponseEntity<?> deleteAnyPost(@PathVariable Long postId) {
        Post post = postRepo.findById(postId).orElseThrow();
        postRepo.delete(post);
        return ResponseEntity.ok("Post deleted by admin");
    }

    @DeleteMapping("/users/{id}/profile-picture")
    public ResponseEntity<?> removeUserProfilePicture(@PathVariable Long id) {
        Users user = userRepo.findById(id).orElseThrow();
        user.setProfilePicUrl(null);
        userRepo.save(user);
        return ResponseEntity.ok("Profile picture removed for user: " + user.getUsername());
    }
    
    // Admin: View user profile safely
    @GetMapping("/users/{id}/profile")
    public ResponseEntity<UserProfileDTO> getUserProfileById(@PathVariable Long id) {
        Users user = userRepo.findById(id).orElseThrow();
        UserProfileDTO dto = UserProfileMapper.toDto(user, userRepo);
        return ResponseEntity.ok(dto);
    }
}
