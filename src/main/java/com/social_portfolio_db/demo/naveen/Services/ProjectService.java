package com.social_portfolio_db.demo.naveen.Services;

import java.util.List;

import com.social_portfolio_db.demo.naveen.Dtos.ProjectDTO;
import com.social_portfolio_db.demo.naveen.Dtos.ProjectUploadRequest;


public interface ProjectService {
    void uploadProject(ProjectUploadRequest request, Long userId);
    List<ProjectDTO> getUserProjects(Long userId);
    ProjectDTO getProjectById(Long projectId);
}

