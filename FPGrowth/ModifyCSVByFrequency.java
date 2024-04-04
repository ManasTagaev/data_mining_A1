import java.io.*;
import java.util.*;

public class ModifyCSVByFrequency {

    public static void main(String[] args) {
        if (args.length != 1) {
            System.err.println("Usage: java ModifyCSVByFrequency <input_filename.csv>");
            System.exit(1);
        }

        String inputFilename = args[0];

        try {
            List<List<String>> modifiedCSV = modifyCSVByFrequency(inputFilename);
            System.out.println("Modified CSV: " + modifiedCSV);
        } catch (IOException e) {
            System.err.println("Error modifying CSV file: " + e.getMessage());
        }
    }

    public static List<List<String>> modifyCSVByFrequency(String filename) throws IOException {
        Map<String, Integer> itemFrequency = new HashMap<>();
        List<List<String>> modifiedLines = new ArrayList<>();

        // Read each line from the CSV file and count item frequencies
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] items = line.split(",");
                for (String item : items) {
                    itemFrequency.put(item, itemFrequency.getOrDefault(item, 0) + 1);
                }
            }
        }

        // Read each line again and sort items within each line based on frequencies
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] items = line.split(",");
                List<String> sortedLine = new ArrayList<>(Arrays.asList(items));
                sortedLine.sort((a, b) -> {
                    int freqComparison = Integer.compare(itemFrequency.get(b), itemFrequency.get(a)); // Descending order of frequency
                    if (freqComparison != 0) {
                        return freqComparison; // Sort by frequency if frequencies are different
                    } else {
                        return Arrays.asList(items).indexOf(a) - Arrays.asList(items).indexOf(b); // Maintain original order for items with the same frequency
                    }
                });
                modifiedLines.add(sortedLine);
            }
        }

        return modifiedLines;
    }
}
