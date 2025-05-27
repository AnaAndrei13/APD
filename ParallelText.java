import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ParallelText {
    public static void main(String[] args) {
        Path filePath = Path.of("test1.txt"); 

        int[] totalWords = {0};

        long startTime = System.nanoTime();

        try (Stream<String> lines = Files.lines(filePath)) {
            
            Map<String, Integer> wordFrequency = lines
                    .parallel() 
                    .flatMap(line -> Stream.of(line.split("\\W+")))
                    .map(String::toLowerCase) 
                    .filter(word -> !word.isEmpty()) 
                    .peek(word -> totalWords[0]++) 
                    .collect(Collectors.toConcurrentMap(
                            word -> word, 
                            word -> 1, 
                            Integer::sum, 
                            ConcurrentHashMap::new 
                    ));

           
            long endTime = System.nanoTime();
            double durationInSeconds = (endTime - startTime) / 1_000_000_000.0;

           
            System.out.println("Total words: " + totalWords[0]);
            System.out.println("Execution time: " + durationInSeconds + " seconds");
            System.out.println("Word frequencies:");
            wordFrequency.forEach((word, frequency) ->
                    System.out.println(word + ": " + frequency));

        } catch (IOException e) {
            System.err.println("Error reading the file: " + e.getMessage());
        }
    }
}
