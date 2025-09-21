package com.social_portfolio_db.demo.naveen.Controllers;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import lombok.RequiredArgsConstructor;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.CrossOrigin;

import com.social_portfolio_db.demo.naveen.Dtos.ProjectDTO;
import com.social_portfolio_db.demo.naveen.Dtos.ProjectUploadRequest;
import com.social_portfolio_db.demo.naveen.Entity.Projects;
import com.social_portfolio_db.demo.naveen.Services.ProjectService;
import com.social_portfolio_db.demo.naveen.Jpa.ProjectsRepository;
import com.social_portfolio_db.demo.naveen.ServicesImp.LikeService;


@RestController
@RequestMapping("/api/projects")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173")
public class ProjectsController {

    private final ProjectService projectsService;
    private final ProjectsRepository projectsRepository;
    private final LikeService likeService;

    @PostMapping("/upload")
    public ResponseEntity<String> uploadProject(
        @RequestParam("title") String title,
        @RequestParam("description") String description,
        @RequestParam("userId") Long userId,
        @RequestParam("images") List<MultipartFile> images
    ) {
        ProjectUploadRequest request = new ProjectUploadRequest();
        request.setTitle(title);
        request.setDescription(description);
        request.setImages(images);

        projectsService.uploadProject(request, userId);
        return ResponseEntity.ok("Project uploaded successfully");
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<ProjectDTO>> getProjectsByUser(@PathVariable Long userId) {
        return ResponseEntity.ok(projectsService.getUserProjects(userId));
    }

    @GetMapping("/{projectId}")
    public ResponseEntity<ProjectDTO> getProject(@PathVariable Long projectId) {
        return ResponseEntity.ok(projectsService.getProjectById(projectId));
    }

    @DeleteMapping("/{projectId}")
    public ResponseEntity<?> deleteProject(
        @PathVariable Long projectId,
        Authentication authentication
    ) {
        try {
            Projects project = projectsRepository.findById(projectId)
                .orElseThrow(() -> new RuntimeException("Project not found"));

            // Check if the authenticated user owns this project
            String username = authentication.getName();
            if (!project.getUser().getEmail().equals(username)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("You can only delete your own projects");
            }

            projectsRepository.delete(project);
            return ResponseEntity.ok("Project deleted successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error deleting project: " + e.getMessage());
        }
    }

    // Admin endpoint to delete all projects (for cleanup)
    @DeleteMapping("/admin/delete-all")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deleteAllProjects() {
        try {
            long count = projectsRepository.count();
            projectsRepository.deleteAll();
            return ResponseEntity.ok("Deleted " + count + " projects successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error deleting all projects: " + e.getMessage());
        }
    }

    @PutMapping("/{projectId}")
    public ResponseEntity<?> updateProject(
        @PathVariable Long projectId,
        @RequestParam("title") String title,
        @RequestParam("description") String description,
        @RequestParam(value = "images", required = false) List<MultipartFile> images,
        Authentication authentication
    ) {
        try {
            Projects project = projectsRepository.findById(projectId)
                .orElseThrow(() -> new RuntimeException("Project not found"));

            // Check if the authenticated user owns this project
            String username = authentication.getName();
            if (!project.getUser().getEmail().equals(username)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("You can only update your own projects");
            }

            project.setTitle(title);
            project.setDescription(description);

            // Handle image upload (replace old images)
            if (images != null && !images.isEmpty()) {
                if (images.size() > 2) {
                    return ResponseEntity.badRequest().body("You can upload a maximum of 2 images per project");
                }
                List<String> imagePaths = new java.util.ArrayList<>();
                String uploadDir = "uploads/projects/";
                for (MultipartFile file : images) {
                    String fileName = java.util.UUID.randomUUID() + "_" + file.getOriginalFilename();
                    java.nio.file.Path path = java.nio.file.Paths.get(uploadDir + fileName);
                    java.nio.file.Files.createDirectories(path.getParent());
                    java.nio.file.Files.copy(file.getInputStream(), path, java.nio.file.StandardCopyOption.REPLACE_EXISTING);
                    imagePaths.add("/images/projects/" + fileName);
                }
                project.setImageUrl(String.join(",", imagePaths));
            }

            projectsRepository.save(project);
            return ResponseEntity.ok("Project updated successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error updating project: " + e.getMessage());
        }
    }

    @PostMapping("/{projectId}/like")
    public ResponseEntity<?> likeProject(@PathVariable Long projectId, @AuthenticationPrincipal UserDetails userDetails) {
        try {
            Long userId = getCurrentUserId(userDetails);
            likeService.likeProject(userId, projectId);
            return ResponseEntity.ok("Project liked");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error liking project: " + e.getMessage());
        }
    }

    @PostMapping("/{projectId}/unlike")
    public ResponseEntity<?> unlikeProject(@PathVariable Long projectId, @AuthenticationPrincipal UserDetails userDetails) {
        try {
            Long userId = getCurrentUserId(userDetails);
            likeService.unlikeProject(userId, projectId);
            return ResponseEntity.ok("Project unliked");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error unliking project: " + e.getMessage());
        }
    }

    @GetMapping("/{projectId}/like-status")
    public ResponseEntity<?> getProjectLikeStatus(@PathVariable Long projectId, @AuthenticationPrincipal UserDetails userDetails) {
        try {
            Long userId = getCurrentUserId(userDetails);
            boolean liked = likeService.hasUserLikedProject(userId, projectId);
            return ResponseEntity.ok(java.util.Map.of("liked", liked));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error getting like status: " + e.getMessage());
        }
    }

    @GetMapping("/{projectId}/likes")
    public ResponseEntity<?> getProjectLikeCount(@PathVariable Long projectId) {
        try {
            long count = likeService.getProjectLikeCount(projectId);
            return ResponseEntity.ok(java.util.Map.of("likeCount", count));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error getting like count: " + e.getMessage());
        }
    }

    // Add this endpoint to return all projects
    @GetMapping
    public ResponseEntity<List<ProjectDTO>> getAllProjects() {
        List<Projects> projects = projectsRepository.findAll();
        List<ProjectDTO> dtos = projects.stream().map(project -> {
            int likeCount = project.getLikes() != null ? project.getLikes().size() : 0;
            String username = project.getUser() != null ? project.getUser().getUsername() : null;
            Long userId = project.getUser() != null ? project.getUser().getId() : null;
            return new ProjectDTO(
                project.getId(),
                project.getTitle(),
                project.getDescription(),
                project.getImageUrl(),
                likeCount,
                username,
                userId
            );
        }).toList();
        return ResponseEntity.ok(dtos);
    }

    private Long getCurrentUserId(UserDetails userDetails) {
        return likeService.getUserRepo().findByEmail(userDetails.getUsername()).orElseThrow().getId();
    }

}
