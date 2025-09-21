package com.social_portfolio_db.demo.naveen.Dtos;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@RequiredArgsConstructor
public class LoginRequest {
    private String username;
    private String password;
    // getters & setters
}

