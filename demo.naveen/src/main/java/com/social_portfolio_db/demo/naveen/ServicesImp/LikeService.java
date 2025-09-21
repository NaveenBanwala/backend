package com.social_portfolio_db.demo.naveen.ServicesImp;

import org.springframework.stereotype.Service;

import com.social_portfolio_db.demo.naveen.Entity.ProfileLike;
import com.social_portfolio_db.demo.naveen.Entity.Projects;
import com.social_portfolio_db.demo.naveen.Entity.ProjectsLike;
import com.social_portfolio_db.demo.naveen.Entity.Users;
import com.social_portfolio_db.demo.naveen.Entity.Post;
import com.social_portfolio_db.demo.naveen.Entity.Notification;
import com.social_portfolio_db.demo.naveen.Jpa.ProfileLikeRepository;
import com.social_portfolio_db.demo.naveen.Jpa.ProjectsLikeRepository;
import com.social_portfolio_db.demo.naveen.Jpa.ProjectsRepository;
import com.social_portfolio_db.demo.naveen.Jpa.UserJpa;
import com.social_portfolio_db.demo.naveen.Jpa.PostRepository;
import com.social_portfolio_db.demo.naveen.Jpa.NotificationRepository;

import lombok.RequiredArgsConstructor;
import java.util.Objects;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class LikeService {

    private final ProjectsLikeRepository projectsLikeRepo;
    private final ProfileLikeRepository profileLikeRepo;
    private final UserJpa userRepo;
    private final ProjectsRepository projectRepo;
    private final PostRepository postRepo;
    private final NotificationRepository notificationRepo;

    public void likeProject(Long userId, Long projectId) {
        if (!projectsLikeRepo.existsByUserIdAndProjectId(userId, projectId)) {
            Projects project = projectRepo.findById(projectId)
                .orElseThrow(() -> new RuntimeException("Project not found"));

            Users user = userRepo.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

            ProjectsLike like = new ProjectsLike();
            like.setProject(project);
            like.setUser(user);
            projectsLikeRepo.save(like);
        }
    }

    public void likeProfile(Long likedById, Long likedUserId) {
        if (!profileLikeRepo.existsByLikedByIdAndLikedUserId(likedById, likedUserId)) {
            Users likedBy = userRepo.findById(likedById).orElseThrow();
            Users likedUser = userRepo.findById(likedUserId).orElseThrow();

            ProfileLike like = new ProfileLike();
            like.setLikedBy(likedBy);
            like.setLikedUser(likedUser);
            profileLikeRepo.save(like);
        }
    }

    public long getProjectLikeCount(Long projectId) {
        return projectsLikeRepo.countByProjectId(projectId);
    }

    public long getProfileLikeCount(Long userId) {
        return profileLikeRepo.countByLikedUserId(userId);
    }

    public void likePost(Long userId, Long postId) {
        if (!hasUserLikedPost(userId, postId)) {
            Post post = postRepo.findById(postId).orElseThrow(() -> new RuntimeException("Post not found"));
            Users user = userRepo.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
            post.getLikedBy().add(user);
            user.getLikedPosts().add(post);
            postRepo.save(post);
            userRepo.save(user);
            // Notification
            if (!Objects.equals(user.getId(), post.getUser().getId())) {
                Notification notif = Notification.builder()
                    .user(post.getUser())
                    .message(user.getUsername() + " liked your post.")
                    .type("LIKE")
                    .createdAt(LocalDateTime.now())
                    .read(false)
                    .build();
                notificationRepo.save(notif);
            }
        }
    }

    public void unlikePost(Long userId, Long postId) {
        Post post = postRepo.findById(postId).orElseThrow(() -> new RuntimeException("Post not found"));
        Users user = userRepo.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
        if (post.getLikedBy().contains(user)) {
            post.getLikedBy().remove(user);
            user.getLikedPosts().remove(post);
            postRepo.save(post);
            userRepo.save(user);
        }
    }

    public long getPostLikeCount(Long postId) {
        return postRepo.findById(postId).map(p -> (long) p.getLikedBy().size()).orElse(0L);
    }

    public boolean hasUserLikedPost(Long userId, Long postId) {
        return postRepo.existsByLikedBy_IdAndId(userId, postId);
    }

    public void unlikeProject(Long userId, Long projectId) {
        Projects project = projectRepo.findById(projectId).orElseThrow(() -> new RuntimeException("Project not found"));
        Users user = userRepo.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
        projectsLikeRepo.findAll().stream()
            .filter(like -> Long.valueOf(like.getUser().getId()).equals(userId) && Long.valueOf(like.getProject().getId()).equals(projectId))
            .findFirst()
            .ifPresent(projectsLikeRepo::delete);
    }

    public boolean hasUserLikedProject(Long userId, Long projectId) {
        return projectsLikeRepo.existsByUserIdAndProjectId(userId, projectId);
    }

    public UserJpa getUserRepo() {
        return userRepo;
    }

    public void unlikeProfile(Long likedById, Long likedUserId) {
        profileLikeRepo.findAll().stream()
            .filter(like -> like.getLikedBy().getId() == likedById && like.getLikedUser().getId() == likedUserId)
            .findFirst()
            .ifPresent(profileLikeRepo::delete);
    }

    public boolean hasUserLikedProfile(Long likedById, Long likedUserId) {
        return profileLikeRepo.existsByLikedByIdAndLikedUserId(likedById, likedUserId);
    }
}
