package com.social_portfolio_db.demo.naveen.Dtos;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserFriendDTO {
    private Long id;
    private String username;
    private List<String> skills;
}
