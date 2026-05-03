package com.jee.app.services;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class FileUploadService {

    private final Cloudinary cloudinary;

    // ✅ Upload vers Cloudinary — retourne l'URL publique
    public String saveFile(MultipartFile file) {
        try {
            Map result = cloudinary.uploader().upload(
                    file.getBytes(),
                    ObjectUtils.asMap(
                            "folder", "charityapp",
                            "resource_type", "auto"
                    )
            );
            // Retourne l'URL sécurisée Cloudinary
            return (String) result.get("secure_url");
        } catch (IOException e) {
            throw new RuntimeException(
                    "Erreur upload Cloudinary : "
                    + e.getMessage());
        }
    }

    // ✅ Supprime de Cloudinary via l'URL
    public void deleteFile(String fileUrl) {
        if (fileUrl == null || fileUrl.isEmpty()) return;
        try {
            // Extrait le public_id depuis l'URL
            String publicId = extractPublicId(fileUrl);
            cloudinary.uploader().destroy(
                    publicId, ObjectUtils.emptyMap());
        } catch (Exception e) {
            System.err.println("Erreur suppression Cloudinary : "
                    + e.getMessage());
        }
    }

    private String extractPublicId(String url) {
        // URL format: .../charityapp/filename.ext
        String[] parts = url.split("/");
        String filename = parts[parts.length - 1];
        String folder = parts[parts.length - 2];
        // Supprime l'extension
        String nameWithoutExt = filename.substring(
                0, filename.lastIndexOf('.'));
        return folder + "/" + nameWithoutExt;
    }
}