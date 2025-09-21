package com.social_portfolio_db.demo.naveen.Dtos;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FriendRequestDTO {
    private Long id;
    private Long fromUserId;
    private String fromUsername;
    private String fromProfilePicUrl;
    private String status;
    private String createdAt;
} 