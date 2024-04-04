import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class FPGrowth {

    public static void main(String[] args) {
        if (args.length != 1) {
            System.err.println("Usage: java FPGrowthCountItemsets <input_filename.csv>");
            System.exit(1);
        }

        String inputFilename = args[0];

        try {
            Map<String, Integer> itemFrequency = countItemsets(inputFilename);
            System.out.println("Item frequencies:");
            for (Map.Entry<String, Integer> entry : itemFrequency.entrySet()) {
                System.out.println(entry.getKey() + ": " + entry.getValue());
            }
        } catch (IOException e) {
            System.err.println("Error reading file: " + e.getMessage());
        }
    }

    public static Map<String, Integer> countItemsets(String filename) throws IOException {
        Map<String, Integer> itemFrequency = new HashMap<>();

        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] items = line.split(",");
                for (String item : items) {
                    // Remove leading/trailing spaces
                    item = item.trim();
                    if (!item.isEmpty()) { // Skip empty items
                        // Increment frequency count for each item
                        itemFrequency.put(item, itemFrequency.getOrDefault(item, 0) + 1);
                    }
                }
            }
        }

        return itemFrequency;
    }
}