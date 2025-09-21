package com.social_portfolio_db.demo.naveen.Jpa;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.antlr.v4.runtime.atn.SemanticContext.AND;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.social_portfolio_db.demo.naveen.Entity.FriendRequest;
import com.social_portfolio_db.demo.naveen.Entity.Users;
// import com.social_portfolio_db.demo.naveen.Entity.FriendRequest;
import org.springframework.stereotype.Repository;

@Repository
public interface UserJpa extends JpaRepository<Users, Long> {
    
    // Optional<Users> findByUsername(String username);

    Optional<Users> findByEmail(String email);

    @Query("SELECT u FROM Users u JOIN u.skills s WHERE s.skillName = :skill AND LOWER(u.username) LIKE LOWER(CONCAT('%', :username, '%'))")
    List<Users> findBySkillNameAndUsernameContainingIgnoreCase(@Param("skill") String skill, @Param("username") String username);

    @Query("SELECT u FROM Users u JOIN u.skills s WHERE s.skillName = :skill")
    List<Users> findBySkillName(@Param("skill") String skill);

    List<Users> findByUsernameContainingIgnoreCase(String username);


    @Query("SELECT DISTINCT u FROM Users u LEFT JOIN u.skills s " +
        "WHERE (:name IS NULL OR LOWER(u.username) LIKE LOWER(CONCAT('%', :name, '%'))) " +
        "AND (:skill IS NULL OR LOWER(s.skillName) LIKE LOWER(CONCAT('%', :skill, '%'))) " +
        "AND (:location IS NULL OR LOWER(u.location) LIKE LOWER(CONCAT('%', :location, '%')))")
        List<Users> searchUsers(@Param("name") String name,
                        @Param("skill") String skill,
                        @Param("location") String location);

    @Query(value = "SELECT u.* FROM users u LEFT JOIN user_followers f ON u.id = f.user_id GROUP BY u.id ORDER BY COUNT(f.follower_id) DESC LIMIT 10", nativeQuery = true)
    List<Users> findTop10UsersByFollowers();

    @Query("SELECT u FROM Users u LEFT JOIN FETCH u.skills WHERE u.id = :id")
    Optional<Users> findByIdWithSkills(@Param("id") Long id);

    // 1️⃣ Get all users this user is following (from friend_requests table with ACCEPTED status)
    @Query("SELECT DISTINCT fr.toUser FROM FriendRequest fr WHERE fr.fromUser.id = :userId AND fr.status = 'ACCEPTED'")
    Set<Users> findFollowings(@Param("userId") Long userId);

    // 2️⃣ Get all followers of a user (from friend_requests table with ACCEPTED status)
    @Query("SELECT DISTINCT fr.fromUser FROM FriendRequest fr WHERE fr.toUser.id = :userId AND fr.status = 'ACCEPTED'")
    Set<Users> findFollowersOfUser(@Param("userId") Long userId);

// @Query("""
//     SELECT DISTINCT u FROM Users u
//     JOIN u.skills s
//     WHERE u.id <> :userId
//     AND s IN (
//         SELECT s2 FROM Users u2
//         JOIN u2.skills s2
//         WHERE u2.id = :userId
//     )
// """)
// List<Users> findUsersWithMutualSkills(@Param("userId") Long userId);

@Query("SELECT fr FROM FriendRequest fr WHERE (fr.fromUser.id = :userId OR fr.toUser.id = :userId) AND fr.status = 'ACCEPTED'")
    List<FriendRequest> findAcceptedFriendships(@Param("userId") Long userId);

boolean existsByEmail(String email);






}
