package com.social_portfolio_db.demo.naveen.Entity;

import com.social_portfolio_db.demo.naveen.Enum.RoleName;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "roles")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(unique = true)
    private RoleName name; // "ROLE_USER", "ROLE_ADMIN"
}

