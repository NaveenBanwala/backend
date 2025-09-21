package com.social_portfolio_db.demo.naveen.Dtos;

import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
public class UserProfileDTO {
    private Long id;
    private String username;
    private String email;
    private String bio;
    private String location;
    private String profilePicUrl;
    private String resumeUrl;

    private List<SkillDTO> skills;

    private List<ProjectDTO> projects;
    private List<String> roles;

    // Add follower/following counts for profile display
    private int followersCount;
    private int followingCount;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class SkillDTO {
        private String name;
        private String level;
    }
}


