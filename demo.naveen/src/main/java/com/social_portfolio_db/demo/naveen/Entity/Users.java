package com.social_portfolio_db.demo.naveen.Entity;

// CREATE TABLE users (
//     id BIGINT AUTO_INCREMENT PRIMARY KEY,
//     name VARCHAR(100) NOT NULL,
//     email VARCHAR(100) NOT NULL UNIQUE,
//     password VARCHAR(255) NOT NULL,
//     bio TEXT,
//     location VARCHAR(100),
//     profile_pic_url VARCHAR(255),
//     resume_url VARCHAR(255),
//     created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
// );

import jakarta.persistence.*;
import lombok.*;
// import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.social_portfolio_db.demo.naveen.Enum.RoleName;


@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString

@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Users implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @Column(name = "name", nullable = false, length = 50, unique = true)
    private String username;

    @Column(name = "email", nullable = false, length = 100, unique = true)
    private String email;

    @Column(name = "password", nullable = false, length = 255)
    private String password;

    @Column(name = "bio", length = 500)
    @ToString.Exclude
    private String bio;

    @Column(name = "location", length = 100)
    private String location;

    @Column(name = "profile_pic_url", length = 255)
    private String profilePicUrl;

    @Column(name = "resume_url", length = 255)
    private String resumeUrl;

    @Column(name = "created_at", nullable = false, updatable = false)
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();


    // @ManyToMany
    // @JoinTable(
    //     name = "user_friends",
    //     joinColumns = @JoinColumn(name = "user_id"),
    //     inverseJoinColumns = @JoinColumn(name = "friend_id")
    // )
    // private Set<Users> friends = new HashSet<>();

    // Role mapping
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
        name = "user_roles",
        joinColumns = @JoinColumn(name = "user_id"),
        inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    @Builder.Default
    private Set<Role> roles = new HashSet<>();

    // Skills mapping (assuming unidirectional OneToMany for simplicity)
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    @com.fasterxml.jackson.annotation.JsonIgnore
    private List<Skills> skills = new ArrayList<>();


    @OneToMany(mappedBy = "userId", cascade = CascadeType.ALL)
    private List<QuizSubmission> quizSubmissions = new ArrayList<>();



//     @ManyToMany(fetch = FetchType.EAGER)
// @JoinTable(
//     name = "user_skills",
//     joinColumns = @JoinColumn(name = "user_id"),
//     inverseJoinColumns = @JoinColumn(name = "skill_id")
// )
// private List<Skills> skills = new ArrayList<>();


    // Projects mapping
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    @com.fasterxml.jackson.annotation.JsonIgnore
    private List<Projects> projects = new ArrayList<>();

    // Liked projects
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    @com.fasterxml.jackson.annotation.JsonIgnore
    private List<ProjectsLike> likedProjects = new ArrayList<>();

    // Profiles that this user has liked
    @OneToMany(mappedBy = "likedBy", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    @com.fasterxml.jackson.annotation.JsonIgnore
    private List<ProfileLike> likedProfiles = new ArrayList<>();

    // Profiles that have liked this user
    @OneToMany(mappedBy = "likedUser", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    @com.fasterxml.jackson.annotation.JsonIgnore
    private List<ProfileLike> receivedLikes = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    @com.fasterxml.jackson.annotation.JsonIgnore
    private List<Post> posts = new ArrayList<>();

    // Posts that this user has liked
    @ManyToMany(mappedBy = "likedBy")
    @Builder.Default
    @com.fasterxml.jackson.annotation.JsonIgnore
    private Set<Post> likedPosts = new HashSet<>();

    @ManyToMany
    @JoinTable(
        name = "user_followers",
        joinColumns = @JoinColumn(name = "follower_id"),
        inverseJoinColumns = @JoinColumn(name = "following_id")
    )
    @Builder.Default
    private Set<Users> following = new HashSet<>();

    @ManyToMany(mappedBy = "following")
    private Set<Users> followers = new HashSet<>();


    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    @com.fasterxml.jackson.annotation.JsonIgnore
    private List<Notification> notifications = new ArrayList<>();

    public void profilePicUrl(String path) {
        this.profilePicUrl = path;
    }

    public void setSkills(Set<Skills> newSkills) {
        this.skills.clear();
        this.skills.addAll(newSkills);
    }



        @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return roles.stream()
            .map(role -> new SimpleGrantedAuthority(role.getName().name()))
            .collect(Collectors.toList());
    }

    @Override
    public String getPassword() {
        return this.password; // or actual password if used
    }

    @Override
    public String getUsername() {
        return this.email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}

