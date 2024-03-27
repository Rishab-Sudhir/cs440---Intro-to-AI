package src.labs.pitfall.agents;

import java.util.ArrayList;
import java.util.List;

public class CombinationGenerator {
    public static void main(String[] args) {
        CombinationGenerator generator = new CombinationGenerator();
        List<List<Integer>> combinations = generator.generateCombinations(3); // For 3 frontier coordinates
        for (List<Integer> combination : combinations) {
            System.out.println(combination);
        }
    }

    public List<List<Integer>> generateCombinations(int n) {
        List<List<Integer>> combinations = new ArrayList<>();
        generateAllBinaryStrings(n, new int[n], 0, combinations);
        return combinations;
    }

    private void generateAllBinaryStrings(int n, int[] array, int i, List<List<Integer>> combinations) {
        if (i == n) {
            List<Integer> combination = new ArrayList<>();
            for (int value : array) {
                combination.add(value);
            }
            combinations.add(combination);
            return;
        }

        // Assign 0 at ith position and try for all other permutations for remaining positions
        array[i] = 0;
        generateAllBinaryStrings(n, array, i + 1, combinations);

        // Assign 1 at ith position and try for all other permutations for remaining positions
        array[i] = 1;
        generateAllBinaryStrings(n, array, i + 1, combinations);
    }
}
