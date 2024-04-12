import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class A1_G1_t1 {
    public static void main(String[] args) {
        // Check if the required arguments are provided
        if (args.length < 2) {
            System.out.println("Usage: java AprioriAlgorithm <file_path> <minsup>");
            return;
        }

        String filePath = args[0];
        double minsup = Double.parseDouble(args[1]);

        // Initialize necessary data structures
        Map<String, Integer> itemCounts = new HashMap<>();
        List<List<String>> transactions = new ArrayList<>();
        int totalTransactions = 0;

        // Read transactions from the input file
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] items = line.split(",");
                List<String> transaction = new ArrayList<>();
                for (String item : items) {
                    if (!item.trim().isEmpty()) {
                        transaction.add(item.trim());
                        itemCounts.put(item.trim(), itemCounts.getOrDefault(item.trim(), 0) + 1);
                    }
                }
                transactions.add(transaction);
                totalTransactions++;
            }
        } catch (IOException e) {
            System.err.println("Error reading file: " + e.getMessage());
            return;
        }

        // Calculate the minimum support count
        int minSupportCount = (int) Math.ceil(minsup * totalTransactions);

        // Initialize a list to store frequent itemsets
        List<ItemsetWithSupport> frequentItemsets = new ArrayList<>();

        // Generate frequent 1-itemsets
        for (Map.Entry<String, Integer> entry : itemCounts.entrySet()) {
            if (entry.getValue() >= minSupportCount) {
                double support = (double) entry.getValue() / totalTransactions;
                frequentItemsets.add(new ItemsetWithSupport(List.of(entry.getKey()), support));
            }
        }

        // Generate frequent itemsets of size k (k >= 2)
        int k = 2;
        while (true) {
            // Generate candidate itemsets of size k
            Set<List<String>> candidates = generateCandidates(frequentItemsets, k);

            // Count the support of each candidate itemset
            Map<List<String>, Integer> candidateCountMap = new HashMap<>();
            for (List<String> transaction : transactions) {
                for (List<String> candidate : candidates) {
                    if (transaction.containsAll(candidate)) {
                        candidateCountMap.put(candidate, candidateCountMap.getOrDefault(candidate, 0) + 1);
                    }
                }
            }

            // Generate frequent itemsets of size k based on the minimum support count
            List<ItemsetWithSupport> newFrequentItemsets = new ArrayList<>();
            for (Map.Entry<List<String>, Integer> entry : candidateCountMap.entrySet()) {
                if (entry.getValue() >= minSupportCount) {
                    double support = (double) entry.getValue() / totalTransactions;
                    newFrequentItemsets.add(new ItemsetWithSupport(entry.getKey(), support));
                }
            }

            // If no new frequent itemsets are generated, break the loop
            if (newFrequentItemsets.isEmpty()) {
                break;
            }

            // Add the new frequent itemsets to the list of all frequent itemsets
            frequentItemsets.addAll(newFrequentItemsets);
            k++;
        }

        // Sort the frequent itemsets in ascending order based on their support values
        frequentItemsets.sort(Comparator.comparingDouble(ItemsetWithSupport::getSupport));

        // Print the frequent itemsets and their support values
        for (ItemsetWithSupport entry : frequentItemsets) {
            System.out.printf("%-50s %.8f%n", String.join(", ", entry.getItemset()), entry.getSupport());
        }
    }

    /**
     * Represents an itemset with its corresponding support value
     */
    private static class ItemsetWithSupport {
        private List<String> itemset;
        private double support;

        public ItemsetWithSupport(List<String> itemset, double support) {
            this.itemset = itemset;
            this.support = support;
        }

        public List<String> getItemset() {
            return itemset;
        }

        public double getSupport() {
            return support;
        }
    }

    /**
     * Generates candidate itemsets of size k from the frequent itemsets of size k-1
     * frequentItemsets is the list of frequent itemsets of size k-1
     * k is the size of the candidate itemsets to generate
     * and returns the set of candidate itemsets of size k
     */
    private static Set<List<String>> generateCandidates(List<ItemsetWithSupport> frequentItemsets, int k) {
        Set<List<String>> candidates = new HashSet<>();
        for (int i = 0; i < frequentItemsets.size(); i++) {
            for (int j = i + 1; j < frequentItemsets.size(); j++) {
                List<String> itemset1 = frequentItemsets.get(i).getItemset();
                List<String> itemset2 = frequentItemsets.get(j).getItemset();
                if (itemset1.size() == k - 1 && itemset2.size() == k - 1) {
                    boolean isJoinable = true;
                    for (int l = 0; l < k - 2; l++) {
                        if (!itemset1.get(l).equals(itemset2.get(l))) {
                            isJoinable = false;
                            break;
                        }
                    }
                    if (isJoinable) {
                        List<String> candidate = new ArrayList<>(itemset1);
                        candidate.add(itemset2.get(k - 2));
                        candidates.add(candidate);
                    }
                }
            }
        }
        return candidates;
    }
}
