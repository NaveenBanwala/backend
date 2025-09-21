package com.social_portfolio_db.demo.naveen.Entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "profile_likes")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProfileLike {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "liked_user_id") 
    private Users likedUser;

    @ManyToOne
    @JoinColumn(name = "liked_by_user_id") 
    private Users likedBy;

    @Column(name = "liked_at")
    private LocalDateTime likedAt = LocalDateTime.now();
}

