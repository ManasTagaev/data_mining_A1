# Apriori and FP-Growth Algorithms in Java
This repository contains Java implementations of two popular algorithms for frequent itemset mining: Apriori and FP-Growth. These algorithms are widely used in data mining and association rule learning to discover frequent patterns and associations in large datasets. These algorithms have been implemented as a part of the Assignment 1 of the CSE304 Introduction to Data Mining course at Ulsan National Institute of Science and Technology.

## Apriori Algorithm
The Apriori algorithm is an influential algorithm for mining frequent itemsets in a dataset. It employs a level-wise search strategy and the downward closure property to efficiently prune the search space. The algorithm generates candidate itemsets of length k from frequent itemsets of length k-1 and tests their support against the minimum support threshold.

### Running the Apriori Algorithm
To run the Apriori algorithm implementation, follow these steps: <br />
Compile the Java source file:
```
javac A1_G1_t1.java 
```
Run the compiled program with the following command-line arguments:
```
java A1_G1_t1 {path_to_data} {minsup}
```
{path_to_data}: Specify the path to the input dataset file. <br />
{minsup}: Specify the minimum support threshold as a decimal value (e.g., 0.5 for 50% support). <br />
Example: <br />
```
java A1_G1_t1 ./groceries.csv 0.05
```
## FP-Growth Algorithm
The FP-growth algorithm, short for Frequent Pattern growth, is a widely used method in data mining for discovering frequent patterns in datasets, particularly in transactional databases or relational tables. It operates by constructing a compact data structure called the FP-tree (Frequent Pattern tree), which represents the frequent itemsets in the dataset.

### Running the FP-Growth Algorithm
To run the Apriori algorithm implementation, follow these steps: <br />
Compile the Java source file:
```
javac A1_G1_t2.java 
```
Run the compiled program with the following command-line arguments:
```
java A1_G1_t2 {path_to_data} {minsup}
```
{path_to_data}: Specify the path to the input dataset file. <br />
{minsup}: Specify the minimum support threshold as a decimal value (e.g., 0.5 for 50% support). <br />
Example: <br />
```
java A1_G1_t2 ./groceries.csv 0.05
```
