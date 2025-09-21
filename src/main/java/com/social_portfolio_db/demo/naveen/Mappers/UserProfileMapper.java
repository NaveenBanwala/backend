package com.social_portfolio_db.demo.naveen.Mappers;

import com.social_portfolio_db.demo.naveen.Dtos.UserProfileDTO;
import com.social_portfolio_db.demo.naveen.Dtos.ProjectDTO;
import com.social_portfolio_db.demo.naveen.Entity.Users;
import com.social_portfolio_db.demo.naveen.Entity.Skills;
import com.social_portfolio_db.demo.naveen.Jpa.UserJpa;

import java.util.stream.Collectors;
import java.util.Collections;

public class UserProfileMapper {
    public static UserProfileDTO toDto(Users user, UserJpa userRepo) {
        UserProfileDTO dto = new UserProfileDTO();
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setEmail(user.getEmail());
        dto.setBio(user.getBio());
        dto.setLocation(user.getLocation());
        // Always set a valid profilePicUrl
        if (user.getProfilePicUrl() == null || user.getProfilePicUrl().trim().isEmpty()) {
            dto.setProfilePicUrl("/images/profiles/default-profile.png");
        } else {
            dto.setProfilePicUrl(user.getProfilePicUrl());
        }
        dto.setResumeUrl(user.getResumeUrl());

        // Handle skills with null safety
        if (user.getSkills() != null && !user.getSkills().isEmpty()) {
            dto.setSkills(
                user.getSkills().stream()
                    .map(skill -> new UserProfileDTO.SkillDTO(skill.getSkillName(), skill.getLevel()))
                    .collect(Collectors.toList())
            );
        } else {
            dto.setSkills(Collections.emptyList());
        }

        // Handle projects with null safety
        if (user.getProjects() != null && !user.getProjects().isEmpty()) {
            dto.setProjects(
                user.getProjects().stream()
                    .map(project -> {
                        ProjectDTO p = new ProjectDTO();
                        p.setId(project.getId());
                        p.setTitle(project.getTitle());
                        p.setDescription(project.getDescription());
                        p.setImageUrl(project.getImageUrl());
                        p.setLikeCount(project.getLikes() != null ? project.getLikes().size() : 0);
                        return p;
                    }).collect(Collectors.toList())
            );
        } else {
            dto.setProjects(Collections.emptyList());
        }

        // Handle roles with null safety
        if (user.getRoles() != null && !user.getRoles().isEmpty()) {
            dto.setRoles(
                user.getRoles().stream()
                    .map(role -> role.getName().toString())
                    .filter(roleName -> roleName != null && !roleName.trim().isEmpty())
                    .collect(Collectors.toList())
            );
        } else {
            dto.setRoles(Collections.emptyList());
        }

        // Set followers and following counts (from friend_requests table)
        int followersCount = 0;
        int followingCount = 0;
        try {
            followersCount = userRepo.findFollowersOfUser(user.getId()).size();
            followingCount = userRepo.findFollowings(user.getId()).size();
        } catch (Exception e) {
            followersCount = 0;
            followingCount = 0;
        }
        dto.setFollowersCount(followersCount);
        dto.setFollowingCount(followingCount);

        return dto;
    }

}


