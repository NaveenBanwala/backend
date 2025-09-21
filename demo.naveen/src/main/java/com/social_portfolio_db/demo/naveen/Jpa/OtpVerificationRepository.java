package com.social_portfolio_db.demo.naveen.Jpa;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.social_portfolio_db.demo.naveen.Entity.OtpVerification;

public interface OtpVerificationRepository  extends JpaRepository<OtpVerification, Long>{

        Optional<OtpVerification> findByEmail(String email);

}
