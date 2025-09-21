package com.social_portfolio_db.demo.naveen.ServicesImp;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.social_portfolio_db.demo.naveen.Dtos.UserFriendDTO;
import com.social_portfolio_db.demo.naveen.Entity.FriendRequest;
import com.social_portfolio_db.demo.naveen.Entity.Skills;
import com.social_portfolio_db.demo.naveen.Entity.Users;
import com.social_portfolio_db.demo.naveen.Jpa.FriendRequestRepository;
import com.social_portfolio_db.demo.naveen.Jpa.UserJpa;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class FriendRequestService {

    private final FriendRequestRepository friendRequestRepo;
    private final UserJpa userRepo;

    public List<UserFriendDTO> suggestFriends(Long userId) {
        Users currentUser = userRepo.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Get all accepted friendships for current user
        List<FriendRequest> acceptedRequests = userRepo.findAcceptedFriendships(userId);

        Set<Users> directFriends = new HashSet<>();

        // Get all direct friends (bidirectional)
        for (FriendRequest fr : acceptedRequests) {
            if (fr.getFromUser().getId().equals(userId)) {
                directFriends.add(fr.getToUser());
            } else {
                directFriends.add(fr.getFromUser());
            }
        }

        Set<Users> suggestions = new HashSet<>();

        for (Users friend : directFriends) {
            List<FriendRequest> friendsFriends = userRepo.findAcceptedFriendships(friend.getId());

            for (FriendRequest fr : friendsFriends) {
                Users potential = fr.getFromUser().getId().equals(friend.getId())
                        ? fr.getToUser()
                        : fr.getFromUser();

                // Skip if:
                // - potential is self
                // - already direct friend
                // - already in suggestions
                if (!potential.getId().equals(userId)
                        && !directFriends.contains(potential)
                        && !suggestions.contains(potential)) {
                    suggestions.add(potential);
                }
            }
        }

        // Convert to DTOs
        return suggestions.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    private UserFriendDTO convertToDTO(Users user) {
        List<String> skillNames = user.getSkills().stream()
                .map(Skills::getSkillName)
                .collect(Collectors.toList());

        return new UserFriendDTO(user.getId(), user.getUsername(), skillNames);
    }
}
