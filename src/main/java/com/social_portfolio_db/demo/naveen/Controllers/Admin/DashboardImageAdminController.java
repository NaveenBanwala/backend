package com.social_portfolio_db.demo.naveen.Controllers.Admin;

import com.social_portfolio_db.demo.naveen.Entity.DashboardImage;
import com.social_portfolio_db.demo.naveen.Jpa.DashboardImageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/dashboard-images")
@PreAuthorize("hasRole('ADMIN')")
public class DashboardImageAdminController {
    @Autowired
    private DashboardImageRepository dashboardImageRepository;

    private final String UPLOAD_DIR = "uploads/dashboard/";

    @PostMapping("/upload")
    public ResponseEntity<?> uploadDashboardImages(@RequestParam("files") List<MultipartFile> files) throws IOException {
        if (files == null || files.isEmpty()) {
            return ResponseEntity.badRequest().body("No files uploaded");
        }
        if (files.size() > 4) {
            return ResponseEntity.badRequest().body("You can upload a maximum of 4 images");
        }
        File dir = new File(UPLOAD_DIR);
        if (!dir.exists()) dir.mkdirs();
        List<DashboardImage> savedImages = new java.util.ArrayList<>();
        for (MultipartFile file : files) {
            String filename = System.currentTimeMillis() + "_" + file.getOriginalFilename();
            Path filepath = Paths.get(UPLOAD_DIR, filename);
            Files.write(filepath, file.getBytes());
            String url = "/images/dashboard/" + filename;
            DashboardImage img = DashboardImage.builder()
                    .url(url)
                    .uploadedAt(LocalDateTime.now())
                    .position("center")
                    .build();
            dashboardImageRepository.save(img);
            savedImages.add(img);
        }
        // Keep only the latest 4 images in DB
        List<DashboardImage> allImages = dashboardImageRepository.findAll();
        if (allImages.size() > 4) {
            allImages.sort((a, b) -> b.getUploadedAt().compareTo(a.getUploadedAt())); // newest first
            List<DashboardImage> toDelete = allImages.subList(4, allImages.size());
            for (DashboardImage img : toDelete) {
                try {
                    Path filePath = Paths.get("uploads/dashboard/" + img.getUrl().substring(img.getUrl().lastIndexOf("/") + 1));
                    Files.deleteIfExists(filePath);
                } catch (Exception e) {
                    // ignore file delete error
                }
                dashboardImageRepository.delete(img);
            }
        }
        return ResponseEntity.ok(savedImages);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteDashboardImage(@PathVariable Long id) {
        DashboardImage img = dashboardImageRepository.findById(id).orElse(null);
        if (img == null) return ResponseEntity.notFound().build();
        // Delete file from disk
        try {
            Path filePath = Paths.get("." + img.getUrl());
            Files.deleteIfExists(filePath);
        } catch (Exception e) {
            // ignore file delete error
        }
        dashboardImageRepository.deleteById(id);
        return ResponseEntity.ok("Deleted");
    }

    @PutMapping("/{id}/position")
    public ResponseEntity<?> updateImagePosition(@PathVariable Long id, @RequestBody Map<String, String> body) {
        DashboardImage img = dashboardImageRepository.findById(id).orElse(null);
        if (img == null) return ResponseEntity.notFound().build();
        String position = body.getOrDefault("position", "center");
        img.setPosition(position);
        dashboardImageRepository.save(img);
        return ResponseEntity.ok(img);
    }

    // Get all dashboard images (up to 4)
    @GetMapping
    @PreAuthorize("permitAll()")
    public ResponseEntity<List<DashboardImage>> getDashboardImages() {
        List<DashboardImage> allImages = dashboardImageRepository.findAll();
        if (allImages.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        // Return up to 4 latest images
        allImages.sort((a, b) -> b.getUploadedAt().compareTo(a.getUploadedAt()));
        return ResponseEntity.ok(allImages.subList(0, Math.min(4, allImages.size())));
    }
} 