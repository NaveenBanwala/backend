package com.social_portfolio_db.demo.naveen.Payloads;


import java.util.Set;
import lombok.*;

@Data
@Getter
@Setter
public class SignupRequest {
    private String username;
    private String email;
    private String password;

    // New field for roles
    private Set<String> roles; // e.g., ["ROLE_USER"] or ["ROLE_ADMIN"]
}
