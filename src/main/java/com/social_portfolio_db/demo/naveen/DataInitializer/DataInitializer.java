package com.social_portfolio_db.demo.naveen.DataInitializer;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.social_portfolio_db.demo.naveen.Entity.Role;
import com.social_portfolio_db.demo.naveen.Entity.Users;
import com.social_portfolio_db.demo.naveen.Enum.RoleName;
import com.social_portfolio_db.demo.naveen.Jpa.RoleRepository;
import com.social_portfolio_db.demo.naveen.Jpa.UserJpa;

import java.util.Optional;
import java.util.Set;

@Configuration
public class DataInitializer {

    @Bean
    public CommandLineRunner createAdminUser(
            UserJpa userRepo,
            RoleRepository roleRepo,
            PasswordEncoder passwordEncoder
    ) {
        return args -> {
            // ✅ Ensure ROLE_ADMIN exists
            Role adminRole = roleRepo.findByName(RoleName.ROLE_ADMIN)
                    .orElseGet(() -> roleRepo.save(new Role(null, RoleName.ROLE_ADMIN)));

            // ✅ Check if admin already exists
            Optional<Users> existingAdmin = userRepo.findByEmail("naveen@admin.com");
            if (existingAdmin.isEmpty()) {
                Users admin = new Users();
                admin.setUsername("NaveenBanwala");
                admin.setEmail("naveen@admin.com"); // You can choose any email
                admin.setPassword(passwordEncoder.encode("n123"));
                admin.setRoles(Set.of(adminRole));

                userRepo.save(admin);
            } else {
            }
        };
    }
}

