package com.social_portfolio_db.demo.naveen.Jpa;

import com.social_portfolio_db.demo.naveen.Entity.FriendRequest;
import com.social_portfolio_db.demo.naveen.Entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface FriendRequestRepository extends JpaRepository<FriendRequest, Long> {
    List<FriendRequest> findByFromUser(Users fromUser);
    List<FriendRequest> findByToUser(Users toUser);
    Optional<FriendRequest> findByFromUserAndToUser(Users fromUser, Users toUser);
    List<FriendRequest> findByToUserAndStatus(Users toUser, String status);
    List<FriendRequest> findByFromUserAndStatus(Users fromUser, String status);
    boolean existsByFromUserAndToUserAndStatus(Users fromUser, Users toUser, String status);
} 