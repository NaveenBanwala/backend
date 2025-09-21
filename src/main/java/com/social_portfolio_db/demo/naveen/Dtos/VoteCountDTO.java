package com.social_portfolio_db.demo.naveen.Dtos;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class VoteCountDTO {
    private Long applicationId;
    private String username;
    private Long voteCount;

    public VoteCountDTO(Long applicationId, String username, Long voteCount) {
        this.applicationId = applicationId;
        this.username = username;
        this.voteCount = voteCount;
    }

    // Getters and setters (or use Lombok @Data if you prefer)
}
