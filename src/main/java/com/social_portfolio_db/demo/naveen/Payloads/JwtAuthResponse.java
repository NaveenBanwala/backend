package com.social_portfolio_db.demo.naveen.Payloads;

import lombok.Data;

@Data
public class JwtAuthResponse {
    private String token;
    private String username;
}
