package com.social_portfolio_db.demo.naveen.ServicesImp;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.nio.file.Path;
// import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.social_portfolio_db.demo.naveen.Dtos.UserProfileDTO;
import com.social_portfolio_db.demo.naveen.Entity.Skills;
import com.social_portfolio_db.demo.naveen.Entity.Users;
import com.social_portfolio_db.demo.naveen.Entity.Projects;
import com.social_portfolio_db.demo.naveen.Jpa.SkillRepository;
import com.social_portfolio_db.demo.naveen.Jpa.UserJpa;
import com.social_portfolio_db.demo.naveen.Jpa.ProjectsRepository;
import com.social_portfolio_db.demo.naveen.Mappers.UserProfileMapper;
import com.social_portfolio_db.demo.naveen.Services.UserService;
import com.social_portfolio_db.demo.naveen.Dtos.ProjectDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UserDetails;
import com.social_portfolio_db.demo.naveen.Entity.FriendRequest;
import com.social_portfolio_db.demo.naveen.Entity.Notification;
import com.social_portfolio_db.demo.naveen.Jpa.FriendRequestRepository;
import com.social_portfolio_db.demo.naveen.Jpa.NotificationRepository;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Optional;

@Service
public class UserServiceImp implements UserService {

    private final UserJpa userRepo;
    private final SkillRepository skillRepo;
    @Autowired
    private ProjectsRepository projectsRepo;

    @Autowired
    private FriendRequestRepository friendRequestRepo;
    @Autowired
    private NotificationRepository notificationRepo;

    public UserServiceImp(UserJpa userRepo, SkillRepository skillRepo) {
        this.userRepo = userRepo;
        this.skillRepo = skillRepo;
    }

    @Override
    public UserProfileDTO getUserProfile(Long userId) {
        Users user = userRepo.findByIdWithSkills(userId)
            .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));
        // Force initialization of projects to avoid LazyInitializationException
        user.getProjects().size();
        return UserProfileMapper.toDto(user, userRepo);
    }

    @Override
    public List<UserProfileDTO> searchUsersBySkillAndName(String skill, String username) {
        List<Users> users;

        if (skill != null && username != null) {
            users = userRepo.findBySkillNameAndUsernameContainingIgnoreCase(skill, username);
        } else if (skill != null) {
            users = userRepo.findBySkillName(skill);
        } else if (username != null) {
            users = userRepo.findByUsernameContainingIgnoreCase(username);
        } else {
            users = userRepo.findAll(); // fallback
        }

        return users.stream()
                    .map(u -> UserProfileMapper.toDto(u, userRepo))
                    .toList();
    }

    public void updateProfile(Long id, UserProfileDTO dto) {
        Users user = userRepo.findById(id).orElseThrow(() -> new RuntimeException("User not found"));

        if (dto.getUsername() != null && !dto.getUsername().isEmpty()) {
            user.setUsername(dto.getUsername());
        }
        user.setBio(dto.getBio());
        user.setLocation(dto.getLocation());
        if (dto.getProfilePicUrl() != null && !dto.getProfilePicUrl().isEmpty()) {
            user.setProfilePicUrl(dto.getProfilePicUrl());
        }
        user.setResumeUrl(dto.getResumeUrl());

        // Only update skills if provided
        if (dto.getSkills() != null) {
            Set<Skills> newSkills = new java.util.HashSet<>();
            for (UserProfileDTO.SkillDTO skillDto : dto.getSkills()) {
                Skills skill = skillRepo.findBySkillNameAndUser(skillDto.getName(), user)
                    .orElseGet(() -> {
                        Skills s = Skills.builder().skillName(skillDto.getName()).user(user).build();
                        return skillRepo.save(s);
                    });
                skill.setLevel(skillDto.getLevel());
                newSkills.add(skill);
            }
            user.setSkills(newSkills);
        }

        // Handle projects without duplication
        if (dto.getProjects() != null) {
            List<Projects> existingProjects = projectsRepo.findByUserId(user.getId());
            for (ProjectDTO projectDTO : dto.getProjects()) {
                if (projectDTO.getTitle() != null && !projectDTO.getTitle().isEmpty()) {
                    boolean alreadyExists = existingProjects.stream()
                        .anyMatch(p -> p.getTitle().equalsIgnoreCase(projectDTO.getTitle()));
                    if (!alreadyExists) {
                        Projects project = new Projects();
                        project.setTitle(projectDTO.getTitle());
                        project.setDescription(projectDTO.getDescription());
                        project.setImageUrl(projectDTO.getImageUrl());
                        project.setUser(user); // Associate project with user
                        projectsRepo.save(project);
                    }
                }
            }
        }

        userRepo.save(user);
    }

    public void uploadProfileImage(Long id, MultipartFile file) {
        try {
            String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
            Path uploadPath = Paths.get("uploads/profiles/");
            Path filePath = uploadPath.resolve(fileName);
            
            // Create directories if they don't exist
            Files.createDirectories(uploadPath);
            
            // Save the file
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
            
            // Update user profile with the image URL
            Users user = userRepo.findById(id).orElseThrow(() -> new RuntimeException("User not found"));
            user.setProfilePicUrl("/images/profiles/" + fileName);
            userRepo.save(user);
        } catch (Exception e) {
            throw new RuntimeException("Error uploading image", e);
        }
    }

public List<UserProfileDTO> searchUsersByParams(String name, String skill, String location) {
    List<Users> users = userRepo.searchUsers(name, skill, location);
    return users.stream().map(u -> UserProfileMapper.toDto(u, userRepo)).toList();
}

public List<Users> getFollowers(Long userId){
    Set<Users> followersSet = userRepo.findFollowersOfUser(userId);
    if (followersSet == null) {
        throw new RuntimeException("User not found with id: " + userId);
    }
    return new java.util.ArrayList<>(followersSet);
}

public List<Users> getFollowing(Long userId) {
    Set<Users> followingSet = userRepo.findFollowings(userId);
    if (followingSet == null) {
        throw new RuntimeException("User not found with id: " + userId);
    }
    return new java.util.ArrayList<>(followingSet);

}

public ResponseEntity<?> sendFriendRequest(Long id, UserDetails userDetails) {
    try {
        Users fromUser = userRepo.findByEmail(userDetails.getUsername()).orElseThrow(() -> new RuntimeException("Current user not found"));
        Users toUser = userRepo.findById(id).orElseThrow(() -> new RuntimeException("Target user not found"));
        if (Objects.equals(fromUser.getId(), toUser.getId())) {
            return ResponseEntity.badRequest().body("You cannot send a friend request to yourself");
        }
        if (friendRequestRepo.existsByFromUserAndToUserAndStatus(fromUser, toUser, "PENDING")) {
            return ResponseEntity.badRequest().body("Friend request already sent");
        }
        FriendRequest req = FriendRequest.builder().fromUser(fromUser).toUser(toUser).status("PENDING")
            .createdAt(LocalDateTime.now())
            .build();
        friendRequestRepo.save(req);
        // Add notification for friend request
        Notification notification = Notification.builder()
            .user(toUser)
            .message(fromUser.getUsername() + " sent you a friend request.")
            .type("FRIEND_REQUEST")
            .createdAt(LocalDateTime.now())
            .read(false)
            .build();
        notificationRepo.save(notification);
        return ResponseEntity.ok("Friend request sent");
    } catch (Exception e) {
        e.printStackTrace();
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body("Error sending friend request: " + e.getMessage());
    }
}

public ResponseEntity<?> acceptFriendRequest(Long id, UserDetails userDetails) {
    try {
        Users toUser = userRepo.findByEmail(userDetails.getUsername()).orElseThrow(() -> new RuntimeException("Current user not found"));
        Users fromUser = userRepo.findById(id).orElseThrow(() -> new RuntimeException("Request sender not found"));
        FriendRequest req = friendRequestRepo.findByFromUserAndToUser(fromUser, toUser).orElseThrow(() -> new RuntimeException("No request found"));
        req.setStatus("ACCEPTED");
        friendRequestRepo.save(req);
        return ResponseEntity.ok("Friend request accepted");
    } catch (Exception e) {
        e.printStackTrace();
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body("Error accepting friend request: " + e.getMessage());
    }
}

public ResponseEntity<?> declineFriendRequest(Long id, UserDetails userDetails) {
    try {
        Users toUser = userRepo.findByEmail(userDetails.getUsername()).orElseThrow(() -> new RuntimeException("Current user not found"));
        Users fromUser = userRepo.findById(id).orElseThrow(() -> new RuntimeException("Request sender not found"));
        FriendRequest req = friendRequestRepo.findByFromUserAndToUser(fromUser, toUser).orElseThrow(() -> new RuntimeException("No request found"));
        req.setStatus("DECLINED");
        friendRequestRepo.save(req);
        return ResponseEntity.ok("Friend request declined");
    } catch (Exception e) {
        e.printStackTrace();
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body("Error declining friend request: " + e.getMessage());
    }
}

public ResponseEntity<?> getFriendRequestStatus(Long id, UserDetails userDetails) {
    try {
        Users currentUser = userRepo.findByEmail(userDetails.getUsername()).orElseThrow(() -> new RuntimeException("Current user not found"));
        Users otherUser = userRepo.findById(id).orElseThrow(() -> new RuntimeException("Other user not found"));
        return ResponseEntity.ok(friendRequestRepo.findByFromUserAndToUser(currentUser, otherUser).orElse(null));
    } catch (Exception e) {
        e.printStackTrace();
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body("Error checking friend request status: " + e.getMessage());
    }
}

public ResponseEntity<?> cancelFriendRequest(Long id, UserDetails userDetails) {
    try {
        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body("You must be logged in to cancel a friend request.");
        }
        Users currentUser = userRepo.findByEmail(userDetails.getUsername())
            .orElseThrow(() -> new RuntimeException("Current user not found"));
        Users targetUser = userRepo.findById(id)
            .orElseThrow(() -> new RuntimeException("Target user not found"));
        Optional<FriendRequest> request = friendRequestRepo.findByFromUserAndToUser(currentUser, targetUser);
        if (request.isPresent() && "PENDING".equals(request.get().getStatus())) {
            friendRequestRepo.delete(request.get());
            return ResponseEntity.ok("Friend request cancelled successfully");
        } else if (request.isPresent()) {
            return ResponseEntity.badRequest().body("Friend request is not pending (status: " + request.get().getStatus() + ")");
        } else {
            return ResponseEntity.badRequest().body("No pending friend request found to cancel");
        }
    } catch (Exception e) {
        e.printStackTrace();
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body("Error cancelling friend request: " + e.getMessage());
    }
}

public ResponseEntity<?> followUser(Long id, Long followerId) {
    try {
        Users user = userRepo.findById(id).orElseThrow(() -> new RuntimeException("User not found"));
        Users follower = userRepo.findById(followerId).orElseThrow(() -> new RuntimeException("Follower not found"));
        Optional<FriendRequest> existingRequest = friendRequestRepo.findByFromUserAndToUser(follower, user);
        if (existingRequest.isPresent()) {
            FriendRequest request = existingRequest.get();
            if ("ACCEPTED".equals(request.getStatus())) {
                return ResponseEntity.ok("Already following");
            } else if ("PENDING".equals(request.getStatus())) {
                request.setStatus("ACCEPTED");
                friendRequestRepo.save(request);
            }
        } else {
            FriendRequest request = FriendRequest.builder()
                .fromUser(follower)
                .toUser(user)
                .status("ACCEPTED")
                .createdAt(LocalDateTime.now())
                .build();
            friendRequestRepo.save(request);
        }
        if (!Objects.equals(follower.getId(), user.getId())) {
            Notification notif = Notification.builder()
                .user(user)
                .message(follower.getUsername() + " started following you.")
                .type("FOLLOW")
                .createdAt(LocalDateTime.now())
                .read(false)
                .build();
            notificationRepo.save(notif);
        }
        return ResponseEntity.ok("Followed user");
    } catch (Exception e) {
        e.printStackTrace();
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body("Error following user: " + e.getMessage());
    }
}

public ResponseEntity<?> unfollowUser(Long id, UserDetails userDetails) {
    try {
        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body("You must be logged in to unfollow a user.");
        }
        Users currentUser = userRepo.findByEmail(userDetails.getUsername())
            .orElseThrow(() -> new RuntimeException("Current user not found"));
        Users targetUser = userRepo.findById(id)
            .orElseThrow(() -> new RuntimeException("Target user not found"));
        Optional<FriendRequest> request = friendRequestRepo.findByFromUserAndToUser(currentUser, targetUser);
        if (request.isPresent() && "ACCEPTED".equals(request.get().getStatus())) {
            friendRequestRepo.delete(request.get());
            return ResponseEntity.ok("Unfollowed user successfully");
        } else if (request.isPresent()) {
            return ResponseEntity.badRequest().body("You have not followed this user yet (status: " + request.get().getStatus() + ")");
        } else {
            return ResponseEntity.badRequest().body("You are not following this user (no follow relationship found)");
        }
    } catch (Exception e) {
        e.printStackTrace();
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body("Error unfollowing user: " + e.getMessage());
    }
}

public ResponseEntity<?> removeFollower(Long id, UserDetails userDetails) {
    try {
        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body("You must be logged in to remove a follower.");
        }
        Users currentUser = userRepo.findByEmail(userDetails.getUsername())
            .orElseThrow(() -> new RuntimeException("Current user not found"));
        Users followerUser = userRepo.findById(id)
            .orElseThrow(() -> new RuntimeException("Follower user not found"));
        Optional<FriendRequest> request = friendRequestRepo.findByFromUserAndToUser(followerUser, currentUser);
        if (request.isPresent() && "ACCEPTED".equals(request.get().getStatus())) {
            friendRequestRepo.delete(request.get());
            return ResponseEntity.ok("Follower removed successfully");
        } else if (request.isPresent()) {
            return ResponseEntity.badRequest().body("This user is not following you yet (status: " + request.get().getStatus() + ")");
        } else {
            return ResponseEntity.badRequest().body("This user is not following you (no follow relationship found)");
        }
    } catch (Exception e) {
        e.printStackTrace();
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body("Error removing follower: " + e.getMessage());
    }
}

}
