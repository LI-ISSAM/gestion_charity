package com.jee.app.dto;
import com.jee.app.enums.Category;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.multipart.MultipartFile;
import java.time.LocalDateTime;

@Data
public class CharityActionDTO {

    @NotBlank(message = "Titre obligatoire")
    private String title;

    @NotBlank(message = "Description obligatoire")
    private String description;

    private String shortDescription;

    @NotNull(message = "Catégorie obligatoire")
    private Category category;

    private String location;

    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    private LocalDateTime startDate;

    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    private LocalDateTime endDate;

    private Double targetAmount;
    private String currency = "MAD";
    private MultipartFile imageFile;
}