package com.social_portfolio_db.demo.naveen.OtpGenerator;

import java.util.Random;

public class OtpUtil {

    public static String generateOtp() {
        Random random = new Random();
        int otp = 100000 + random.nextInt(900000); 
        return String.valueOf(otp);
    }
}

