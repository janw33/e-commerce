package com.example.ecommerce.controllers.image;

import com.example.ecommerce.dtos.image.ImageResponse;
import com.example.ecommerce.services.image.ImageService;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/v1/image/{productId}")
public class ImageController {
    private final ImageService imageService;

    public ImageController(ImageService imageService) {
        this.imageService = imageService;
    }

    @GetMapping
    public List<ImageResponse> getImages(
            Authentication authentication,
            @PathVariable Long productId
    ) {
        return imageService.getImages(authentication, productId);
    }
}
