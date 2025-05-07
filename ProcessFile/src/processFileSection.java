import java.io.*;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;
import java.nio.charset.StandardCharsets;

public class processFileSection implements Runnable {
    private final File file;
    private final long start;
    private final long end;
    private final ConcurrentHashMap<String, Integer> globalFrequency;
    private static final int BUFFER_SIZE = 8192; // 8KB buffer

    public processFileSection(File file, long start, long end, ConcurrentHashMap<String, Integer> globalFrequency) {
        this.file = file;
        this.start = start;
        this.end = end;
        this.globalFrequency = globalFrequency;
    }

    public void run() {
        try (RandomAccessFile randomAccessFile = new RandomAccessFile(file, "r")) {
            randomAccessFile.seek(start);
            
            // Skip to the start of a word if not at the beginning of the file
            if (start != 0) {
                randomAccessFile.readLine();
            }

            // Use buffered reading for better performance
            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(
                            new FileInputStream(randomAccessFile.getFD()),
                            StandardCharsets.UTF_8),
                    BUFFER_SIZE)) {

                HashMap<String, Integer> localFrequency = new HashMap<>();
                String line;
                long currentPosition = start;

                while (currentPosition < end && (line = reader.readLine()) != null) {
                    currentPosition += line.getBytes(StandardCharsets.UTF_8).length + 1; // +1 for newline
                    
                    // Process words in the line
                    String[] words = line.toLowerCase()
                            .replaceAll("[^a-zA-Z ]", "")
                            .split("\\s+");

                    for (String word : words) {
                        if (!word.isEmpty()) {
                            localFrequency.merge(word, 1, Integer::sum);
                        }
                    }
                }
                // Batch update the global frequency map
                globalFrequency.putAll(localFrequency);
            }
        } catch (Exception e) {
            System.err.println("Error processing file section from " + start + " to " + end + ": " + e.getMessage());
        }
    }
}

