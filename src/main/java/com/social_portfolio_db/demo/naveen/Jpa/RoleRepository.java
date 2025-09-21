package com.social_portfolio_db.demo.naveen.Jpa;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.social_portfolio_db.demo.naveen.Entity.Role;
import com.social_portfolio_db.demo.naveen.Enum.RoleName;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByName(RoleName roleUser);
}

