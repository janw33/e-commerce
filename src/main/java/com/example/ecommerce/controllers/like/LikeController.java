package com.example.ecommerce.controllers.like;

import com.example.ecommerce.services.like.LikeService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/v1/like")
public class LikeController {
    private final LikeService likeService;

    public LikeController(LikeService likeService) {
        this.likeService = likeService;
    }

    @PutMapping (path = "/{productId}")
    public ResponseEntity<Void> likeProduct (
            @PathVariable Long productId,
            Authentication authentication
    ) {
        likeService.likeProduct(productId, authentication);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping(path = "/{productId}")
    public ResponseEntity<Void> unlikeProduct (
            @PathVariable Long productId,
            Authentication authentication
    ) {
        likeService.unlikeProduct(productId, authentication);
        return ResponseEntity.noContent().build();
    }
}
