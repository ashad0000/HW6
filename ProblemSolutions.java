/******************************************************************
 *
 *   Abdul / 272
 *
 *   This java file contains the problem solutions for the methods lastBoulder,
 *   showDuplicates, and pair methods. You should utilize the Java Collection
 *   Framework for these methods.
 *
 ********************************************************************/

import java.util.*;
import java.util.PriorityQueue;

public class ProblemSolutions {

    /**
     * Priority Queue (PQ) Game
     *
     * PQ1 Problem Statement:
     * -----------------------
     *
     * You are given an array of integers of boulders where boulders[i] is the
     * weight of the ith boulder.
     *
     * We are playing a game with the boulders. On each turn, we choose the heaviest
     * two boulders and smash them together. Suppose the heaviest two boulders have
     * weights x and y. The result of this smash is:
     *
     *    If x == y, both boulders are destroyed, and
     *    If x != y, the boulder of weight x is destroyed, and the boulder of
     *               weight y has new weight y - x.
     *
     * At the end of the game, there is at most one boulder left.
     *
     * Return the weight of the last remaining boulder. If there are no boulders
     * left, return 0.
     */
    public static int lastBoulder(int[] boulders) {

        // max-heap using a priority queue
        PriorityQueue<Integer> pq = new PriorityQueue<>(Collections.reverseOrder());

        // Add all boulders to the priority queue
        for (int b : boulders) {
            pq.offer(b);
        }

        // Repeat while more than one boulder
        while (pq.size() > 1) {
            int first = pq.poll();
            int second = pq.poll();
            if (first != second) {
                pq.offer(first - second);
            }
        }

        // Return last boulder or 0 if none
        return pq.isEmpty() ? 0 : pq.peek();
    }

    /**
     * Method showDuplicates
     *
     * This method identifies duplicate strings in an array list.
     */
    public static ArrayList<String> showDuplicates(ArrayList<String> input) {

        // Count occurrences of each string
        HashMap<String, Integer> countMap = new HashMap<>();

        for (String s : input) {
            countMap.put(s, countMap.getOrDefault(s, 0) + 1);
        }

        // Add strings that show up more than once to result list
        ArrayList<String> result = new ArrayList<>();
        for (Map.Entry<String, Integer> entry : countMap.entrySet()) {
            if (entry.getValue() > 1) {
                result.add(entry.getKey());
            }
        }

        // Sort result alphabetically
        Collections.sort(result);

        return result;
    }

    /**
     * Finds pairs in the input array that add up to k.
     */
    public static ArrayList<String> pair(int[] input, int k) {

        // Store seen elements
        HashSet<Integer> seen = new HashSet<>();
        HashSet<String> pairSet = new HashSet<>(); // to avoid duplicates

        for (int num : input) {
            int target = k - num;
            if (seen.contains(target)) {
                int a = Math.min(num, target);
                int b = Math.max(num, target);
                pairSet.add("(" + a + ", " + b + ")"); // format as string
            }
            seen.add(num);
        }

        // Convert set to list and sort it
        ArrayList<String> result = new ArrayList<>(pairSet);
        Collections.sort(result);

        return result;
    }
}
