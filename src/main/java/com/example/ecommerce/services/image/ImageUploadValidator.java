package com.example.ecommerce.services.image;

import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import javax.imageio.stream.MemoryCacheImageInputStream;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.UUID;

public class ImageUploadValidator {
    private static final long MAX_FILE_BYTES = 5L * 1024 * 1024;
    private static final long MAX_PIXELS = 12_000_000;

    public BufferedImage readValidatedImage(MultipartFile file) throws IOException {
        if (file.isEmpty() || file.getSize() > MAX_FILE_BYTES) {
            throw new IllegalArgumentException("Invalid image file size");
        }

        try (var stream = file.getInputStream();
             var input = new MemoryCacheImageInputStream(stream)) {
            var readers = ImageIO.getImageReaders(input);
            if (!readers.hasNext()) {
                throw new IllegalArgumentException("Unsupported image format");
            }

            var reader = readers.next();
            try {
                String format = reader.getFormatName();
                if (!format.equalsIgnoreCase("JPEG") && !format.equalsIgnoreCase("PNG")) {
                    throw new IllegalArgumentException("Only JPEG and PNG are supported");
                }

                reader.setInput(input, true, true);
                long pixels = (long) reader.getWidth(0) * reader.getHeight(0);
                if (pixels > MAX_PIXELS) {
                    throw new IllegalArgumentException("Image has too many pixels");
                }

                return reader.read(0);
            } finally {
                reader.dispose();
            }
        }
    }

    String savePng(BufferedImage image, Path directory) throws IOException {
        String fileName = UUID.randomUUID() + ".png";
        Path path = directory.resolve(fileName);

        try (var output = Files.newOutputStream(
                path, StandardOpenOption.CREATE_NEW, StandardOpenOption.WRITE)) {
            if (!ImageIO.write(image, "png", output)) {
                throw new IOException("No PNG writer available");
            }
        }

        return fileName;
    }

    public String store(MultipartFile file, Path directory) throws IOException {
        BufferedImage image = readValidatedImage(file);
        return savePng(image, directory);
    }
}