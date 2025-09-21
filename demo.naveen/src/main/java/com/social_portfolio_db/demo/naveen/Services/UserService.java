package com.social_portfolio_db.demo.naveen.Services;



import java.util.List;

import com.social_portfolio_db.demo.naveen.Dtos.UserProfileDTO;

public interface UserService {

    UserProfileDTO getUserProfile(Long userId);


    List<UserProfileDTO> searchUsersBySkillAndName(String skill, String name);


}
