package com.social_portfolio_db.demo.naveen.Dtos;


import lombok.Data;
import lombok.Builder;

@Data
@Builder
public class ProjectDTO {

    private Long id;
    private String title;
    private String description;
    private String imageUrl;
    private int likeCount;
    private String username;
    private Long userId;
   

    public ProjectDTO() {
    }
    public ProjectDTO(Long id, String title, String description, String imageUrl, int likeCount, String username, Long userId) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.imageUrl = imageUrl;
        this.likeCount = likeCount;
        this.username = username;
        this.userId = userId;
    }
}
    
