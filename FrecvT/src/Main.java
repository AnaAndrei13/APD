import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {

   HashMap<String, Integer> frequency = new HashMap<>();

 // File fileName= new File("test1.txt");
    // File fileName= new File("test2.txt");
   File fileName= new File("test3.txt");

  int nrWords=0;

        long startTime = System.nanoTime();
        try{
      Scanner scanner = new Scanner(fileName);
      while (scanner.hasNext()) {
          String word= scanner.next();
          word = word.toLowerCase().replaceAll("[^a-zA-Z]", "");

          if (!word.isEmpty()) {
              frequency.put(word, frequency.getOrDefault(word, 0) + 1);
              nrWords++;
          }
      }
  }catch(FileNotFoundException e){
      System.out.println("File not found");
  }

        long endTime = System.nanoTime();
        long duration = endTime - startTime;
        double durationInSeconds = duration / 1_000_000_000.0;

        System.out.println("Total words: " + nrWords);
        System.out.println("Execution time: " + durationInSeconds + " seconds");
        System.out.println("Words frequency");

        for (Map.Entry<String, Integer> entry : frequency.entrySet()) {
            System.out.println(entry.getKey() + "  " + entry.getValue());
        }
    }
    }
