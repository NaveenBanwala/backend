package com.social_portfolio_db.demo.naveen.Jpa;

import com.social_portfolio_db.demo.naveen.Entity.Vote;
import com.social_portfolio_db.demo.naveen.Entity.VotingApplication;
import com.social_portfolio_db.demo.naveen.Dtos.VoteCountDTO;
import com.social_portfolio_db.demo.naveen.Dtos.VotingContestDTO;
import com.social_portfolio_db.demo.naveen.Entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface VoteRepository extends JpaRepository<Vote, Long> {
    long countByApplication(VotingApplication application);
    boolean existsByVoterAndApplication(Users voter, VotingApplication application);
    List<Vote> findByApplication(VotingApplication application);

//     @Query("SELECT new com.social_portfolio_db.demo.naveen.Dtos.VotingContestDTO(u.username, u.profilePicUrl) " +
//         "FROM VotingApplication v " +
//         "JOIN v.user u")
// List<VotingContestDTO> findAllContenders();

@Query("SELECT new com.social_portfolio_db.demo.naveen.Dtos.VotingContestDTO(v.id, u.username, v.imageUrl) " +
        "FROM VotingApplication v JOIN v.user u")
List<VotingContestDTO> findAllContenders();



// Prevent duplicate vote by same voter on same application
    boolean existsByVoterIdAndApplicationId(Long voterId, Long applicationId);

    // Count votes on a specific application (contestant)
    long countByApplicationId(Long applicationId);


    @Query("SELECT new  com.social_portfolio_db.demo.naveen.Dtos.VoteCountDTO(v.application.id, v.application.user.username, COUNT(v)) " +
    "FROM Vote v GROUP BY v.application.id, v.application.user.username")
List<VoteCountDTO> countVotesForAllApplications();




} 