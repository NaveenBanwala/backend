package com.social_portfolio_db.demo.naveen.Dtos;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;

@Data
public class ProjectUploadRequest {
    private String title;
    private String description;
    private List<MultipartFile> images;
}


