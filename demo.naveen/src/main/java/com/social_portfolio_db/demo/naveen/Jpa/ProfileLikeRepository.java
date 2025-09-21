package com.social_portfolio_db.demo.naveen.Jpa;

import com.social_portfolio_db.demo.naveen.Entity.ProfileLike;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProfileLikeRepository extends JpaRepository<ProfileLike, Long> {
    
    // Check if a like already exists between two users
    boolean existsByLikedByIdAndLikedUserId(Long likedBy, Long likedUser);

    // Count how many likes a user received
    long countByLikedUserId(Long likedUser);
}
