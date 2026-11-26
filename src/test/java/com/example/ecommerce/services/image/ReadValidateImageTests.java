package com.example.ecommerce.services.image;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class ReadValidateImageTests {
    @TempDir
    private Path directory;

    private ImageUploadValidator imageUploadValidator;

    @BeforeEach
    void setUp() {
        imageUploadValidator = new ImageUploadValidator(directory.toString());
    }

    @AfterEach
    void tearDown() {
        if (TransactionSynchronizationManager.isSynchronizationActive()) {
            TransactionSynchronizationManager.clearSynchronization();
        }
    }

    @Test
    void shouldRejectEmptyFile() {
        MockMultipartFile file = new MockMultipartFile("image", new byte[0]);

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> imageUploadValidator.readValidatedImage(file)
        );

        assertEquals("Invalid image file size", exception.getMessage());
    }

    @Test
    void shouldRejectFileLargerThanFiveMegabytes() {
        MockMultipartFile file = new MockMultipartFile("image", new byte[5 * 1024 * 1024 + 1]);

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> imageUploadValidator.readValidatedImage(file)
        );

        assertEquals("Invalid image file size", exception.getMessage());
    }

    @Test
    void shouldRejectFileThatIsNotAnImageEvenWhenContentTypeIsPng() {
        MockMultipartFile file = new MockMultipartFile(
                "image", "image.png", "image/png", new byte[]{1, 2, 3}
        );

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> imageUploadValidator.readValidatedImage(file)
        );

        assertEquals("Unsupported image format", exception.getMessage());
    }

    @Test
    void shouldRejectGifImage() throws IOException {
        MockMultipartFile file = imageFile("gif", 10, 20);

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> imageUploadValidator.readValidatedImage(file)
        );

        assertEquals("Only JPEG and PNG are supported", exception.getMessage());
    }

    @Test
    void shouldRejectImageWithMoreThanTwelveMillionPixels() throws IOException {
        MockMultipartFile file = imageFile("png", 4001, 3000);

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> imageUploadValidator.readValidatedImage(file)
        );

        assertEquals("Image has too many pixels", exception.getMessage());
    }

    @Test
    void shouldAcceptImageWithExactlyTwelveMillionPixels() throws IOException {
        MockMultipartFile file = imageFile("png", 4000, 3000);

        BufferedImage result = imageUploadValidator.readValidatedImage(file);

        assertEquals(4000, result.getWidth());
        assertEquals(3000, result.getHeight());
    }

    @ParameterizedTest
    @ValueSource(strings = {"png", "jpeg"})
    void shouldReadSupportedImage(String format) throws IOException {
        MockMultipartFile file = imageFile(format, 10, 20);

        BufferedImage result = imageUploadValidator.readValidatedImage(file);

        assertNotNull(result);
        assertEquals(10, result.getWidth());
        assertEquals(20, result.getHeight());
    }

    private MockMultipartFile imageFile(String format, int width, int height) throws IOException {
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        try (var output = new ByteArrayOutputStream()) {
            assertTrue(ImageIO.write(image, format, output));
            return new MockMultipartFile("image", "image." + format, "image/" + format, output.toByteArray());
        }
    }
}
