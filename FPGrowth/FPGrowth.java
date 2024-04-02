import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class FPGrowth {
    // Node class for FP-tree
    static class FPNode {
        String item;
        int count;
        FPNode parent;
        FPNode next; // Link to next node with the same item
        List<FPNode> children;

        public FPNode(String item, int count, FPNode parent) {
            this.item = item;
            this.count = count;
            this.parent = parent;
            this.children = new ArrayList<>();
            this.next = null;
        }
    }

    // FP-tree class
    static class FPTree {
        FPNode root;
        Map<String, FPNode> headerTable;

        public FPTree() {
            root = new FPNode(null, 0, null);
            headerTable = new HashMap<>();
        }

        // Method to insert a transaction into the FP-tree
        public void insertTransaction(List<String> transaction) {
            FPNode current = root;
            for (String item : transaction) {
                FPNode child = findChild(current, item);
                if (child != null) {
                    child.count++;
                } else {
                    FPNode newNode = new FPNode(item, 1, current);
                    current.children.add(newNode);
                    updateHeaderTable(newNode);
                    current = newNode;
                }
            }
        }

        // Method to find a child node with a given item
        private FPNode findChild(FPNode parent, String item) {
            for (FPNode child : parent.children) {
                if (child.item.equals(item)) {
                    return child;
                }
            }
            return null;
        }

        // Method to update the header table with a new node
        private void updateHeaderTable(FPNode node) {
            if (headerTable.containsKey(node.item)) {
                FPNode lastNode = headerTable.get(node.item);
                while (lastNode.next != null) {
                    lastNode = lastNode.next;
                }
                lastNode.next = node;
            } else {
                headerTable.put(node.item, node);
            }
        }

        // Method to recursively mine frequent itemsets
        public void mineFrequentItemsets(double minSupport) {
            for (String item : headerTable.keySet()) {
                List<String> prefixPath = new ArrayList<>();
                prefixPath.add(item);
                mineConditionalFPTree(prefixPath, headerTable.get(item), minSupport);
            }
        }

        private void mineConditionalFPTree(List<String> prefixPath, FPNode node, double minSupport) {
            Map<String, Integer> conditionalPatternBase = new HashMap<>();
            while (node != null) {
                List<String> conditionalPath = new ArrayList<>(prefixPath);
                int count = node.count;
                while (node.parent != null) {
                    conditionalPath.add(node.item);
                    node = node.parent;
                }
                Collections.reverse(conditionalPath);
                conditionalPatternBase.put(String.join(",", conditionalPath), count);
                node = node.next;
            }

            // Calculate support for each itemset and print frequent itemsets
            for (String pattern : conditionalPatternBase.keySet()) {
                double support = (double) conditionalPatternBase.get(pattern) / (double) headerTable.get(prefixPath.get(0)).count;
                if (support >= minSupport) {
                    System.out.println(pattern + " " + support);
                }
            }
        }
    }

    public static void main(String[] args) {
        // Check if correct number of arguments are provided
        if (args.length != 2) {
            System.err.println("Usage: java FPGrowth <inputfile> <minsupport>");
            System.exit(1);
        }

        String csvFile = args[0];
        double minSupport = Double.parseDouble(args[1]);

        List<List<String>> transactions = readTransactions(csvFile);

        FPTree fpTree = buildFPTree(transactions);
        fpTree.mineFrequentItemsets(minSupport);
    }

    // Method to read transactions from CSV file
    private static List<List<String>> readTransactions(String filename) {
        List<List<String>> transactions = new ArrayList<>();
        String line;

        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            while ((line = br.readLine()) != null) {
                String[] items = line.split("\t");
                transactions.add(Arrays.asList(items));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return transactions;
    }

    // Method to build FP-tree from transactions
    private static FPTree buildFPTree(List<List<String>> transactions) {
        FPTree fpTree = new FPTree();
        for (List<String> transaction : transactions) {
            fpTree.insertTransaction(transaction);
        }
        return fpTree;
    }
}
