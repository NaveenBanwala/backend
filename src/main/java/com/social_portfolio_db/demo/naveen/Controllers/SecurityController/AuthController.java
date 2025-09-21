package com.social_portfolio_db.demo.naveen.Controllers.SecurityController;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;
import com.social_portfolio_db.demo.naveen.Dtos.AuthRequest;
import com.social_portfolio_db.demo.naveen.Dtos.GoogleRegisterRequest;
import com.social_portfolio_db.demo.naveen.Entity.Role;
import com.social_portfolio_db.demo.naveen.Entity.Users;
import com.social_portfolio_db.demo.naveen.Enum.RoleName;
import com.social_portfolio_db.demo.naveen.Jpa.UserJpa;
import com.social_portfolio_db.demo.naveen.Jpa.RoleRepository;
import com.social_portfolio_db.demo.naveen.Payloads.JwtAuthRequest;
import com.social_portfolio_db.demo.naveen.Payloads.JwtAuthResponse;
import com.social_portfolio_db.demo.naveen.Payloads.SignupRequest;
import com.social_portfolio_db.demo.naveen.Security.JwtService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;


import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;


import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthenticationManager authenticationManager;
    private final UserDetailsService userDetailsService;
    private final JwtService jwtService;
    private final UserJpa userRepo;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepo;

//     @PostMapping("/login")
// public ResponseEntity<?> login(@RequestBody JwtAuthRequest request) {
//     try {
//         Optional<Users> optionalUser = userRepo.findByEmail(request.getEmail());
//         if (optionalUser.isEmpty()) {
//             return ResponseEntity.status(401).body("Invalid credentials");
//         }

//         Users user = optionalUser.get();

//         // ❗ Reject if user has no password (i.e., registered with Google only)
//         if (user.getPassword() == null || user.getPassword().isBlank()) {
//             return ResponseEntity.status(401)
//                 .body("Please sign in with Google or set a password.");
//         }

//         // ✅ Authenticate
//         authenticationManager.authenticate(
//                 new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
//         );

//         // ✅ Generate JWT token
//         UserDetails userDetails = userDetailsService.loadUserByUsername(request.getEmail());
//         String token = jwtService.generateToken(userDetails);

//         JwtAuthResponse response = new JwtAuthResponse();
//         response.setToken(token);
//         response.setUsername(userDetails.getUsername());

//         return ResponseEntity.ok(response);

//     } catch (AuthenticationException e) {
//         return ResponseEntity.status(401).body("Invalid email or password");
//     } catch (Exception e) {
//         e.printStackTrace();
//         return ResponseEntity.status(500).body("Server error");
//     }
// }

@PostMapping("/login")
public ResponseEntity<?> login(@Valid @RequestBody AuthRequest authRequest) {
    String email = authRequest.getEmail();
    String password = authRequest.getPassword();
    System.out.println(password);

    if (email == null || password == null || email.isBlank() || password.isBlank()) {
        return ResponseEntity.badRequest().body("Email and password must not be empty.");
    }

    Optional<Users> userOpt = userRepo.findByEmail(email);
    System.out.println(userOpt.isEmpty());

    // Optional<Users> userOpt = userRepo.findByEmail("naveen@admin.com");
if (userOpt.isPresent()) {
    Users user = userOpt.get();
    System.out.println("Password from DB: " + user.getPassword());
}

    if (userOpt.isEmpty()) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
    }

    Users user = userOpt.get();

    if (user.getPassword() == null || user.getPassword().isBlank()) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Password not set. Please complete registration.");
    }

    try {
        authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(email, password)
        );

        UserDetails userDetails = userDetailsService.loadUserByUsername(email);
        System.out.println(userDetails);
        String token = jwtService.generateToken(userDetails);

        System.out.println(token);

        return ResponseEntity.ok(Map.of("token", token));
    } catch (BadCredentialsException e) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
    }
}




    // @PostMapping("/login")
    // public ResponseEntity<JwtAuthResponse> login(@RequestBody JwtAuthRequest request) {
    //     try {
    //         // Check if user exists first
    //         if (!userRepo.findByEmail(request.getEmail()).isPresent()) {
    //             return ResponseEntity.status(401).body(null);
    //         }

    //         // 1. Authenticate user
    //         authenticationManager.authenticate(
    //                 new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
    //         );

    //         // 2. Generate token
    //         UserDetails userDetails = userDetailsService.loadUserByUsername(request.getEmail());
    //         String token = jwtService.generateToken(userDetails);

    //         // 3. Return token and username
    //         JwtAuthResponse response = new JwtAuthResponse();
    //         response.setToken(token);
    //         response.setUsername(userDetails.getUsername());

    //         return ResponseEntity.ok(response);
    //     } catch (AuthenticationException e) {
    //         return ResponseEntity.status(401).body(null);
    //     } catch (Exception e) {
    //         return ResponseEntity.status(500).body(null);
    //     }
    // }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody JwtAuthRequest request) {
        if (userRepo.findByEmail(request.getEmail()).isPresent()) {
            return ResponseEntity.badRequest().body("Email already exists");
        }

        Role defaultRole = roleRepo.findByName(RoleName.ROLE_USER)
                .orElseThrow(() -> new RuntimeException("Default role not found"));

        Users user = new Users();
        user.setUsername(request.getUsername() != null ? request.getUsername() : request.getEmail());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRoles(Set.of(defaultRole));

        userRepo.save(user);

        UserDetails userDetails = userDetailsService.loadUserByUsername(user.getEmail());
        String token = jwtService.generateToken(userDetails);

        JwtAuthResponse response = new JwtAuthResponse();
        response.setToken(token);
        response.setUsername(userDetails.getUsername());

        return ResponseEntity.ok(response);
    }

    @PostMapping("/change-password")
    public ResponseEntity<?> changePassword(@RequestBody Map<String, String> payload, @AuthenticationPrincipal UserDetails userDetails) {
        String oldPassword = payload.get("oldPassword");
        String newPassword = payload.get("newPassword");
        String email = userDetails.getUsername();

        if (oldPassword == null || newPassword == null) {
            return ResponseEntity.badRequest().body("Missing required fields");
        }

        Users user = userRepo.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("User not found"));

        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            return ResponseEntity.status(400).body("Old password is incorrect");
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepo.save(user);

        return ResponseEntity.ok("Password changed successfully");
    }

    @GetMapping("/users")
    public ResponseEntity<?> getAllUsers() {
        try {
            var users = userRepo.findAll();
            return ResponseEntity.ok(users.stream()
                .map(user -> Map.of(
                    "id", user.getId(),
                    "email", user.getEmail(),
                    "username", user.getUsername(),
                    "hasPassword", user.getPassword() != null && !user.getPassword().isEmpty()
                ))
                .collect(java.util.stream.Collectors.toList()));
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error fetching users: " + e.getMessage());
        }
    }

    @GetMapping("/check-user/{email}")
    public ResponseEntity<?> checkUserExists(@PathVariable String email) {
        try {
            var user = userRepo.findByEmail(email);
            if (user.isPresent()) {
                return ResponseEntity.ok(Map.of(
                    "exists", true,
                    "email", user.get().getEmail(),
                    "username", user.get().getUsername(),
                    "hasPassword", user.get().getPassword() != null && !user.get().getPassword().isEmpty()
                ));
            } else {
                return ResponseEntity.ok(Map.of("exists", false));
            }
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error checking user: " + e.getMessage());
        }
    }

//     @PostMapping("/google")
// public ResponseEntity<?> loginWithGoogle(@RequestBody Map<String, String> payload) {
//     try {
//         String token = payload.get("token");

//         FirebaseToken decodedToken = FirebaseAuth.getInstance().verifyIdToken(token);
//         String email = decodedToken.getEmail();
//         String name = decodedToken.getName();

//         // Fallback: if name is not found in token, extract from email
//         if (name == null || name.isBlank()) {
//             name = email.split("@")[0];
//         }

//         Optional<Users> optionalUser = userRepo.findByEmail(email);
//         Users user;
//         boolean passwordRequired = false;

//         if (optionalUser.isEmpty()) {
//             // Create new user (register)
//             Role userRole = roleRepo.findByName(RoleName.ROLE_USER)
//                     .orElseThrow(() -> new RuntimeException("Default role not found"));

//             user = new Users();
//             user.setEmail(email);
//             user.setUsername(name);
//             user.setRoles(Set.of(userRole));
//             user.setPassword(null); // password not yet set

//             userRepo.save(user);
//             passwordRequired = true; // tell frontend to ask for password
//         } else {
//             user = optionalUser.get();
//             if (user.getPassword() == null || user.getPassword().isBlank()) {
//                 passwordRequired = true;
//             }
//         }

//         String jwt = jwtService.generateToken(user);

//         return ResponseEntity.ok(Map.of(
//                 "token", jwt,
//                 "email", user.getEmail(),
//                 "username", user.getUsername(),
//                 "passwordRequired", passwordRequired
//         ));
//     } catch (FirebaseAuthException e) {
//     e.printStackTrace();
//     return ResponseEntity.status(500).body("Google token invalid: " + e.getMessage());
// } catch (Exception ex) {
//     ex.printStackTrace();
//     return ResponseEntity.status(500).body("Internal error: " + ex.getMessage());
// }

// }

@PostMapping("/complete-google-signup")
public ResponseEntity<?> completeGoogleSignup(@RequestBody SignupRequest signupRequest) {
    if (userRepo.existsByEmail(signupRequest.getEmail())) {
        return ResponseEntity.badRequest().body("Email already registered");
    }

    Users user = new Users();
    user.setEmail(signupRequest.getEmail());
    user.setUsername(signupRequest.getUsername());
    user.setPassword(passwordEncoder.encode(signupRequest.getPassword()));

    Role userRole = roleRepo.findByName(RoleName.ROLE_USER)
                .orElseThrow(() -> new RuntimeException("Default role not found"));
    user.setRoles(Set.of(userRole));

    userRepo.save(user);
    return ResponseEntity.ok("Google signup completed successfully");
}


@PostMapping("/google")
public ResponseEntity<?> handleGoogleSignup(@RequestBody Map<String, String> body) {
    try {
        String idToken = body.get("token");

        FirebaseToken decodedToken = FirebaseAuth.getInstance().verifyIdToken(idToken);
        String email = decodedToken.getEmail();

        Optional<Users> existing = userRepo.findByEmail(email);
        if (existing.isPresent()) {
            return ResponseEntity.ok(Map.of(
                "email", existing.get().getEmail(),
                "username", existing.get().getUsername(),
                "status", "EXISTS"
            ));
        }

        // Suggest a username from email
        String suggestedUsername = email.split("@")[0];

        return ResponseEntity.ok(Map.of(
            "email", email,
            "username", suggestedUsername,
            "status", "NEW"
        ));
    } catch (FirebaseAuthException e) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid Google token");
    }
}

@PostMapping("/google-register")
public ResponseEntity<?> registerWithGoogle(@RequestBody GoogleRegisterRequest request) {
    try {
        Optional<Users> existingUser = userRepo.findByEmail(request.getEmail());
        if (existingUser.isPresent()) {
            return ResponseEntity
                    .status(HttpStatus.CONFLICT)
                    .body("User with this email already exists");
        }

        Users user = new Users();
        user.setEmail(request.getEmail());
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));

        Role userRole = roleRepo.findByName(RoleName.ROLE_USER)
                .orElseThrow(() -> new RuntimeException("Default role not found"));
        user.setRoles(Set.of(userRole));

        userRepo.save(user);

        String token = jwtService.generateToken(user);
        return ResponseEntity.ok(Map.of("token", token));
    } catch (Exception e) {
        e.printStackTrace();
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Google registration failed");
    }
}

}