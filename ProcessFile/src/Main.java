import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

public class Main {
    private static final int NUM_THREADS = 4;
    private static final int BUFFER_SIZE = 8192; // 8KB buffer size

    public static void main(String[] args) {
        File fileName = new File("test5.txt");
        System.out.println("Using " + NUM_THREADS + " threads for processing");
        
        long fileSize = fileName.length();
        long partSize = Math.max(fileSize / NUM_THREADS, 1024 * 1024);

        ConcurrentHashMap<String, Integer> globalFrequency = new ConcurrentHashMap<>(1000, 0.75f, NUM_THREADS);
        
        // Create thread pool with fixed number of threads
        ExecutorService executor = Executors.newFixedThreadPool(NUM_THREADS);

        long startTime = System.nanoTime();
        
        // Create and submit tasks
        CountDownLatch latch = new CountDownLatch(NUM_THREADS);
        AtomicInteger activeThreads = new AtomicInteger(0);
        
        for (int i = 0; i < NUM_THREADS; i++) {
            long start = i * partSize;
            long end = (i == NUM_THREADS - 1) ? fileSize : (start + partSize);
            
            executor.submit(() -> {
                try {
                    activeThreads.incrementAndGet();
                    new processFileSection(fileName, start, end, globalFrequency).run();
                } finally {
                    activeThreads.decrementAndGet();
                    latch.countDown();
                }
            });
        }

        // Wait for all tasks to complete
        try {
            latch.await();
            executor.shutdown();
            if (!executor.awaitTermination(60, TimeUnit.SECONDS)) {
                executor.shutdownNow();
            }
        } catch (InterruptedException e) {
            executor.shutdownNow();
            Thread.currentThread().interrupt();
        }

        long endTime = System.nanoTime();
        double durationInSeconds = (endTime - startTime) / 1_000_000_000.0;

        System.out.println("Execution time: " + durationInSeconds + " seconds");
        System.out.println("Total unique words: " + globalFrequency.size());
        

        System.out.println("\nTop 10 most frequent words:");
        globalFrequency.entrySet().stream()
            .sorted((e1, e2) -> e2.getValue().compareTo(e1.getValue()))
            .limit(10)
            .forEach(entry -> System.out.println(entry.getKey() + ": " + entry.getValue()));
    }
}
