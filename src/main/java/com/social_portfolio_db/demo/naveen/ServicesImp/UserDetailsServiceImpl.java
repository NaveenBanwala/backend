package com.social_portfolio_db.demo.naveen.ServicesImp;

import com.social_portfolio_db.demo.naveen.Entity.Users;
import com.social_portfolio_db.demo.naveen.Jpa.UserJpa;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private UserJpa userRepo;

@Override
public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
    Users user = userRepo.findByEmail(email)
        .orElseThrow(() -> new UsernameNotFoundException("User not found"));

    if (user.getPassword() == null || user.getPassword().isBlank()) {
        throw new IllegalArgumentException("Password not set. Please complete registration.");
    }

    return new org.springframework.security.core.userdetails.User(
        user.getEmail(),
        user.getPassword(),  // must not be null
        getAuthorities(user)
    );
}

private Collection<? extends GrantedAuthority> getAuthorities(Users user) {
    return user.getRoles().stream()
            .map(role -> new SimpleGrantedAuthority(role.getName().name()))
            .toList();
}

}
