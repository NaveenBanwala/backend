package com.social_portfolio_db.demo.naveen.Dtos;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@RequiredArgsConstructor
public class GoogleRegisterRequest {
    private String email;
    private String username;
    private String password;

    // Getters and setters
}
