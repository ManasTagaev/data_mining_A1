import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

class FPNode {
    /**
     * A node in the FP tree.
     */

    public String value;
    public double count;
    public FPNode parent;
    public FPNode link;
    public List<FPNode> children;

    public FPNode(String value, double count, FPNode parent) {
        /**
         * Create the node.
         */
        this.value = value;
        this.count = count;
        this.parent = parent;
        this.link = null;
        this.children = new ArrayList<>();
    }

    public boolean hasChild(String value) {
        /**
         * Check if node has a particular child node.
         */
        for (FPNode node : children) {
            if (node.value.equals(value)) {
                return true;
            }
        }
        return false;
    }

    public FPNode getChild(String value) {
        /**
         * Return a child node with a particular value.
         */
        for (FPNode node : children) {
            if (node.value.equals(value)) {
                return node;
            }
        }
        return null;
    }

    public FPNode addChild(String value) {
        /**
         * Add a node as a child node.
         */
        FPNode child = new FPNode(value, 1, this);
        children.add(child);
        return child;
    }
}


class FPTree{
    // A frequent pattern tree 

    private Map<String, Double> frequent;
    private Map<String, FPNode> headers;
    private FPNode root;

    public FPTree(List<List<String>> transactions, double threshold, String rootValue, double rootCount) {
        /**
         * Initialize the tree.
         */
        frequent = findFrequentItems(transactions, threshold);
        headers = buildHeaderTable(frequent);
        root = buildFPTree(transactions, rootValue, rootCount, frequent, headers);
    }

    private Map<String, Double> findFrequentItems(List<List<String>> transactions, double threshold) {
        /**
         * Create a dictionary of items with occurrences above the threshold.
         */
        Map<String, Double> items = new HashMap<>();

        for (List<String> transaction : transactions) {
            for (String item : transaction) {
                items.put(item, items.getOrDefault(item, 0.0) + 1);
            }
        }

        // Remove items below threshold
        Iterator<Map.Entry<String, Double>> iterator = items.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, Double> entry = iterator.next();
            if (entry.getValue() < threshold) {
                iterator.remove();
            }
        }

        return items;
    }

    private Map<String, FPNode> buildHeaderTable(Map<String, Double> frequent) {
        /**
         * Build the header table.
         */
        Map<String, FPNode> headers = new HashMap<>();
        for (String key : frequent.keySet()) {
            headers.put(key, null);
        }
        return headers;
    }

    private FPNode buildFPTree(List<List<String>> transactions, String rootValue,
                                double rootCount, Map<String, Double> frequent, Map<String, FPNode> headers) {
        /**
         * Build the FP tree and return the root node.
         */
        FPNode root = new FPNode(rootValue, rootCount, null);

        for (List<String> transaction : transactions) {
            List<String> sortedItems = new ArrayList<>();
            for (String item : transaction) {
                if (frequent.containsKey(item)) {
                    sortedItems.add(item);
                }
            }
            
            sortedItems.sort((item1, item2) -> {
                int freqCompare = frequent.get(item2).compareTo(frequent.get(item1));
                if (freqCompare == 0) {
                    return item1.compareTo(item2); // Lexicographical order for tie-break
                }
                return freqCompare;
            });
            
            if (!sortedItems.isEmpty()) {
                insertTree(sortedItems, root, headers);
            }
        }

        return root;
    }
    
    private void insertTree(List<String> items, FPNode node, Map<String, FPNode> headers) {
        /**
         * Recursively grow FP tree.
         */
        String first = items.get(0);
        FPNode child = node.getChild(first);
        if (child != null) {
            child.count += 1; 
        } else {
            // Add new child.
            child = node.addChild(first);

            // Link it to header structure.
            if (headers.get(first) == null) {
                headers.put(first, child);
            } else {
                FPNode current = headers.get(first);
                while (current.link != null) {
                    current = current.link;
                }
                current.link = child;
            }
        }

        // Call function recursively.
        List<String> remainingItems = items.subList(1, items.size());
        if (!remainingItems.isEmpty()) {
            insertTree(remainingItems, child, headers);
        }
    }

    private boolean treeHasSinglePath(FPNode node) {
        /**
         * If there is a single path in the tree,
         * return true, else return false.
         */
        int numChildren = node.children.size();
        if (numChildren > 1) {
            return false;
        } else if (numChildren == 0) {
            return true;
        } else {
            return true && treeHasSinglePath(node.children.get(0));
        }
    }
    
    public Map<List<String>,Double> minePatterns(double threshold) {
        /**
         * Mine the constructed FP tree for frequent patterns.
         */
        if (treeHasSinglePath(root)) {
            return generatePatternList();
        } else {
            return zipPatterns(mineSubTrees(threshold));
        }
    }

    private Map<List<String>, Double> zipPatterns(Map<List<String>, Double> patterns) {
        /**
         * Append suffix to patterns in dictionary if
         * we are in a conditional FP tree.
         */
        String suffix = root.value;

        if (suffix != null) {
            // We are in a conditional tree.
            Map<List<String>, Double> newPatterns = new HashMap<>();
            for (List<String> key : patterns.keySet()) {
                List<String> newKey = new ArrayList<>(key);
                newKey.add(suffix);
                Collections.sort(newKey);
                newPatterns.put(newKey, (double) patterns.get(key));
            }

            return newPatterns;
        }

        return patterns;
    }

    private Map<List<String>, Double> generatePatternList() {
        /**
         * Generate a list of patterns with support counts.
         */
        Map<List<String>, Double> patterns = new HashMap<>();
        Set<String> items = frequent.keySet();

        // If we are in a conditional tree,
        // the suffix is a pattern on its own.
        List<String> suffixValue = new ArrayList<>();
        if (root.value == null) {
            suffixValue = new ArrayList<>();
        } else {
            suffixValue.add(root.value);
            patterns.put(suffixValue, root.count);
        }

        for (int i = 1; i <= items.size(); i++) {
            for (List<String> subset : combinations(items, i)) {
                List<String> pattern = new ArrayList<>(subset);
                pattern.addAll(suffixValue);
                Collections.sort(pattern);
                patterns.put(pattern, getMinFrequency(subset));
            }
        }
        return patterns;
    }

    private List<List<String>> combinations(Set<String> items, int k) {
        /**
         * Generate combinations of items.
         */
        List<List<String>> result = new ArrayList<>();
        List<String> list = new ArrayList<>(items);
        int[] indices = new int[k];
        for (int i = 0; i < k; i++) {
            indices[i] = i;
        }
        while (indices[0] <= items.size() - k) {
            List<String> combination = new ArrayList<>();
            for (int index : indices) {
                combination.add(list.get(index));
            }
            result.add(combination);
            int i = k - 1;
            while (i >= 0 && indices[i] == i + items.size() - k) {
                i--;
            }
            if (i >= 0) {
                indices[i]++;
                for (int j = i + 1; j < k; j++) {
                    indices[j] = indices[j - 1] + 1;
                }
            } else {
                break;
            }
        }
        return result;
    }
    
    private double getMinFrequency(List<String> items) {
        /**
         * Get the minimum frequency among items.
         */
        double minFrequency = Double.MAX_VALUE;
        for (String item : items) {
            minFrequency = Math.min(minFrequency, frequent.get(item));
        }
        return minFrequency;
    }

    private Map<List<String>, Double> mineSubTrees(double threshold) {
        /**
         * Generate subtrees and mine them for patterns.
         */
        Map<List<String>, Double> patterns = new HashMap<>();
        List<String> miningOrder = new ArrayList<>(frequent.keySet());
        miningOrder.sort(Comparator.comparingDouble(frequent::get));

        // Get items in tree in reverse order of occurrences.
        for (String item : miningOrder) {
            List<FPNode> suffixes = new ArrayList<>();
            List<List<String>> conditionalTreeInput = new ArrayList<>();
            FPNode node = headers.get(item);

            // Follow node links to get a list of
            // all occurrences of a certain item.
            while (node != null) {
                suffixes.add(node);
                node = node.link;
            }

            // For each occurrence of the item, 
            // trace the path back to the root node.
            for (FPNode suffix : suffixes) {
                double frequency = suffix.count;
                List<String> path = new ArrayList<>();
                FPNode parent = suffix.parent;

                while (parent.parent != null) {
                    path.add(parent.value);
                    parent = parent.parent;
                }

                for (int i = 0; i < frequency; i++) {
                    conditionalTreeInput.add(new ArrayList<>(path));
                }
            }

            // Now we have the input for a subtree,
            // so construct it and grab the patterns.
            FPTree subtree = new FPTree(conditionalTreeInput, threshold, item, frequent.get(item));
            Map<List<String>, Double> subtreePatterns = subtree.minePatterns(threshold);

            // Insert subtree patterns into main patterns dictionary.
            for (Map.Entry<List<String>, Double> entry : subtreePatterns.entrySet()) {
                List<String> pattern = entry.getKey();
                double count = entry.getValue();
                patterns.merge(pattern, count, Double::sum);
            }
        }

        return patterns;
    }
}



public class fpgrowth{
    public static void main(String[] args) {
        String csvFile = args[0];

        // Getting an input
        List<List<String>> transactions = new ArrayList<>();
        String line;

        try (BufferedReader br = new BufferedReader(new FileReader(csvFile))) {
            while ((line = br.readLine()) != null) {
                String[] items = line.split(",");
                transactions.add(Arrays.asList(items));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        double minSupport = Double.parseDouble(args[1]) * transactions.size(); 
        System.out.println(minSupport); 

        Map<List<String>,Double>  patterns = findFrequentPatterns(transactions, minSupport);
        
        for (Map.Entry<List<String>, Double> entry : patterns.entrySet()) {
            List<String> pattern = entry.getKey();
            double count = entry.getValue();
            System.out.println(pattern + " " + count / transactions.size());
        }
    }

    private static Map<List<String>,Double>  findFrequentPatterns(List<List<String>> transactions, double supportThreshold){
        FPTree tree = new FPTree(transactions, supportThreshold, null, 0); 
        return tree.minePatterns(supportThreshold); 
    }
}
