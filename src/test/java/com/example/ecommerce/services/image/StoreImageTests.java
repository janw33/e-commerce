package com.example.ecommerce.services.image;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class StoreImageTests {
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
    void shouldNotStoreInvalidImage() throws IOException {
        MockMultipartFile file = new MockMultipartFile("image", new byte[]{1, 2, 3});

        assertThrows(
                IllegalArgumentException.class,
                () -> imageUploadValidator.store(file)
        );

        try (var files = Files.list(directory)) {
            assertEquals(0, files.count());
        }
    }

    @Test
    void shouldThrowIOExceptionWhenUploadDirectoryDoesntExist() throws IOException {
        imageUploadValidator = new ImageUploadValidator(directory.resolve("missing").toString());
        MockMultipartFile file = imageFile("png", 10, 20);

        assertThrows(IOException.class, () -> imageUploadValidator.store(file));
    }

    @ParameterizedTest
    @ValueSource(strings = {"png", "jpeg"})
    void shouldStoreImageAsPngWithUniqueFileName(String format) throws IOException {
        MockMultipartFile file = imageFile(format, 10, 20);

        String firstFileName = imageUploadValidator.store(file);
        String secondFileName = imageUploadValidator.store(file);

        assertNotEquals(firstFileName, secondFileName);
        assertTrue(firstFileName.endsWith(".png"));
        assertDoesNotThrow(() -> UUID.fromString(firstFileName.substring(0, firstFileName.length() - 4)));
        assertTrue(Files.exists(directory.resolve(secondFileName)));

        try (var input = ImageIO.createImageInputStream(directory.resolve(firstFileName).toFile())) {
            var reader = ImageIO.getImageReaders(input).next();
            try {
                assertEquals("png", reader.getFormatName().toLowerCase());
                reader.setInput(input);
                BufferedImage storedImage = reader.read(0);
                assertEquals(10, storedImage.getWidth());
                assertEquals(20, storedImage.getHeight());
            } finally {
                reader.dispose();
            }
        }
    }
    @Test
    void shouldDeleteFilesAddedAfterRegisteringRollbackSynchronization() throws IOException {
        TransactionSynchronizationManager.initSynchronization();
        List<String> fileNames = new ArrayList<>();
        imageUploadValidator.deleteFilesOnRollback(fileNames);

        String firstFileName = imageUploadValidator.store(imageFile("png", 10, 20));
        String secondFileName = imageUploadValidator.store(imageFile("jpeg", 10, 20));

        fileNames.add(firstFileName);
        fileNames.add("missing.png");
        fileNames.add(secondFileName);

        Path unrelatedFile = Files.writeString(directory.resolve("unrelated.png"), "keep");

        var synchronizations = TransactionSynchronizationManager.getSynchronizations();
        assertEquals(1, synchronizations.size());
        synchronizations.getFirst().afterCompletion(TransactionSynchronization.STATUS_ROLLED_BACK);

        assertFalse(Files.exists(directory.resolve(firstFileName)));
        assertFalse(Files.exists(directory.resolve(secondFileName)));
        assertTrue(Files.exists(unrelatedFile));
    }

    @Test
    void shouldKeepFilesAfterCommit() throws IOException {
        TransactionSynchronizationManager.initSynchronization();
        String fileName = imageUploadValidator.store(imageFile("png", 10, 20));
        imageUploadValidator.deleteFilesOnRollback(List.of(fileName));

        TransactionSynchronizationManager.getSynchronizations().getFirst()
                .afterCompletion(TransactionSynchronization.STATUS_COMMITTED);

        assertTrue(Files.exists(directory.resolve(fileName)));
    }

    private MockMultipartFile imageFile(String format, int width, int height) throws IOException {
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        try (var output = new ByteArrayOutputStream()) {
            assertTrue(ImageIO.write(image, format, output));
            return new MockMultipartFile("image", "image." + format, "image/" + format, output.toByteArray());
        }
    }
}
