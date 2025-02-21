package com.akgarg.profile.image;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;

import java.util.HashSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class LocalStorageImageServiceTest {

    private LocalStorageImageService imageService;

    @BeforeEach
    void setUp() {
        imageService = new LocalStorageImageService();
    }

    @Test
    void loadTest_GenerateImageId() {
        assertNotNull(imageService);

        final var imageIds = new HashSet<String>();
        final var idsToGenerate = 10;

        final var imageFile = new MockMultipartFile("test.jpg", "test.jpg", "image/jpeg", new byte[0]);

        for (int i = 1; i <= idsToGenerate; i++) {
            final var imageId = imageService.generateImageId(imageFile);
            assertThat(imageId).isNotBlank();
            assertFalse(imageIds.contains(imageId));
            imageIds.add(imageId);
            System.out.println(imageId);
        }
    }

    @Test
    void loadTest_GenerateImageId_MultiThreaded() throws InterruptedException {
        assertNotNull(imageService);

        final var totalThreads = 10_000;
        final var latch = new CountDownLatch(totalThreads);

        try (final var executorService = Executors.newFixedThreadPool(totalThreads)) {
            final var imageFile = new MockMultipartFile("test.jpg", "test.jpg", "image/jpeg", new byte[0]);

            final var testTask = getRunnableTask(imageFile, latch);

            for (var i = 0; i < totalThreads; i++) {
                executorService.submit(testTask);
            }

            latch.await();
        }
    }

    private Runnable getRunnableTask(
            final MockMultipartFile imageFile,
            final CountDownLatch latch
    ) {
        final var generatedImageIds = new ConcurrentHashMap<String, Object>();
        final var mapValue = new Object();

        return () -> {
            try {
                for (int i = 0; i < 1000; i++) {
                    final var imageId = imageService.generateImageId(imageFile);
                    assertFalse(imageId.isEmpty());
                    assertFalse(generatedImageIds.containsKey(imageId));
                    generatedImageIds.put(imageId, mapValue);
                }
            } finally {
                latch.countDown();
            }
        };
    }

}
