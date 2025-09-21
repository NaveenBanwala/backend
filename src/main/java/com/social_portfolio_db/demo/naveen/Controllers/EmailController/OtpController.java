package com.social_portfolio_db.demo.naveen.Controllers.EmailController;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.social_portfolio_db.demo.naveen.Entity.OtpVerification;
import com.social_portfolio_db.demo.naveen.Jpa.OtpVerificationRepository;
import com.social_portfolio_db.demo.naveen.Payloads.OtpConfirmRequest;
// import com.social_portfolio_db.demo.naveen.OtpGenerator.OtpUtil;
import com.social_portfolio_db.demo.naveen.ServicesImp.EmailService.EmailService;

@RestController
@RequestMapping("/api")
public class OtpController {

    @Autowired
    private EmailService emailService;

    @Autowired
    private OtpVerificationRepository otpRepo;

    @PostMapping("/send")
    public ResponseEntity<?> sendOtp(@RequestParam String email) {
        String otp = String.valueOf(new Random().nextInt(900000) + 100000);
        LocalDateTime expiryTime = LocalDateTime.now().plusMinutes(5);

        OtpVerification otpRecord = otpRepo.findByEmail(email).orElse(new OtpVerification());
        otpRecord.setEmail(email);
        otpRecord.setOtp(otp);
        otpRecord.setExpiryTime(expiryTime);

        otpRepo.save(otpRecord);
        emailService.sendOtpEmail(email, otp);
        return ResponseEntity.ok("OTP sent to " + email);
    }

    @PostMapping("/verify")
    public ResponseEntity<?> verifyOtp(@RequestBody OtpConfirmRequest otpConfirm) {
        Optional<OtpVerification> optionalOtp = otpRepo.findByEmail(otpConfirm.getEmail());
        if (optionalOtp.isEmpty()) {
            return ResponseEntity.badRequest().body("OTP not sent to this email");
        }

        OtpVerification record = optionalOtp.get();
        if (record.getExpiryTime().isBefore(LocalDateTime.now())) {
            return ResponseEntity.status(HttpStatus.GONE).body("OTP expired");
        }
        System.out.println(otpConfirm.getOtp());
        if (!record.getOtp().equals(otpConfirm.getOtp())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid OTP");
        }
        return ResponseEntity.ok("VERIFIED");
    }
}

