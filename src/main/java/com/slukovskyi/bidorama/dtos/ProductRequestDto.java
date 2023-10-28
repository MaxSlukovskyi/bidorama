package com.slukovskyi.bidorama.dtos;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Data
public class ProductRequestDto {
    private Long id;
    private String name;
    private Long categoryId;
    private String description;
    private List<MultipartFile> images;
    private String startTime;
    private String endTime;
    private Double startBid;
    private Double minimalStep;
    private Boolean imagesWasChanged;
}
