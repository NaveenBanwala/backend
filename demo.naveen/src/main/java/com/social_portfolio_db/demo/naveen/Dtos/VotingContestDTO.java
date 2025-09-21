package com.social_portfolio_db.demo.naveen.Dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class VotingContestDTO {

    
    private Long applicationId;

    private String username;// In db voting_application store it id 

    private String profilePicUrl; // this store as image_url

}
