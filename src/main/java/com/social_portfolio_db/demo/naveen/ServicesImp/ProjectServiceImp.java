package com.social_portfolio_db.demo.naveen.ServicesImp;

import java.io.IOException;
import java.nio.file.*;
import java.util.*;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.social_portfolio_db.demo.naveen.Dtos.ProjectUploadRequest;
import com.social_portfolio_db.demo.naveen.Dtos.ProjectDTO;
import com.social_portfolio_db.demo.naveen.Entity.Projects;
import com.social_portfolio_db.demo.naveen.Entity.Users;
import com.social_portfolio_db.demo.naveen.Jpa.ProjectsRepository;
import com.social_portfolio_db.demo.naveen.Jpa.UserJpa;
import com.social_portfolio_db.demo.naveen.Services.ProjectService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProjectServiceImp implements ProjectService {

    private final ProjectsRepository projectsRepository;
    private final UserJpa userRepository;

    @Override
    public void uploadProject(ProjectUploadRequest request, Long userId) {
        Users user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Projects project = new Projects();
        project.setTitle(request.getTitle());
        project.setDescription(request.getDescription());
        project.setUser(user);

        // Handle local image saving
        if (request.getImages() != null && !request.getImages().isEmpty()) {
            List<String> imagePaths = new ArrayList<>();
            String uploadDir = "uploads/projects/";
            for (MultipartFile file : request.getImages()) {
                try {
                    String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
                    Path path = Paths.get(uploadDir + fileName);
                    Files.createDirectories(path.getParent());
                    Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
                    imagePaths.add("/images/projects/" + fileName);
                } catch (IOException e) {
                    throw new RuntimeException("Failed to save file: " + file.getOriginalFilename(), e);
                }
            }
            project.setImageUrl(String.join(",", imagePaths));
        }

        projectsRepository.save(project);
    }

    @Override
    public List<ProjectDTO> getUserProjects(Long userId) {
        return projectsRepository.findByUserId(userId).stream()
                .map(project -> new ProjectDTO(
                        project.getId(),
                        project.getTitle(),
                        project.getDescription(),
                        project.getImageUrl(),
                        project.getLikes() != null ? project.getLikes().size() : 0,
                        project.getUser().getUsername(), // make sure Users has getUsername()
                        project.getUser().getId()))
                .toList();
    }

    @Override
    public ProjectDTO getProjectById(Long projectId) {
        Projects project = projectsRepository.findById(projectId)
                .orElseThrow(() -> new RuntimeException("Project not found"));

        return new ProjectDTO(
                project.getId(),
                project.getTitle(),
                project.getDescription(),
                project.getImageUrl(),
                project.getLikes() != null ? project.getLikes().size() : 0,
                project.getUser().getUsername(),
                project.getUser().getId());
    }
}


