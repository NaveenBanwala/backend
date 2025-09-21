package com.social_portfolio_db.demo.naveen.Payloads;

public class OtpConfirmRequest {
    private String email;
    private String otp;

    // Getters and setters
    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }
    public String getOtp() {
        return otp;
    }
    public void setOtp(String otp) {
        this.otp = otp;
    }
}
