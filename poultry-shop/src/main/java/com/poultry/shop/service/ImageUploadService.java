package com.poultry.shop.service;

import com.cloudinary.Cloudinary;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@Service
public class ImageUploadService {

    private final Cloudinary cloudinary;

    public ImageUploadService(Cloudinary cloudinary) {
        this.cloudinary = cloudinary;
    }

    public String uploadImage(MultipartFile file) {
        try {
            Map uploadResult = cloudinary.uploader().upload(file.getBytes(), Map.of());
            return uploadResult.get("secure_url").toString();   // 🔥 permanent image URL
        } catch (Exception e) {
            throw new RuntimeException("Image upload failed", e);
        }
    }
}
