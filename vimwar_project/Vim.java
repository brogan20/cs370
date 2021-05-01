import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.ArrayList;

//https://www.geeksforgeeks.org/perfect-sum-problem-print-subsets-given-sum/

public class Vim {

    static boolean[][] dp;

    static void printSubsetsRec(Integer arr[], int i, int sum, ArrayList<Integer> p) {
        // If we reached end and sum is non-zero. We print
        // p[] only if arr[0] is equal to sun OR dp[0][sum]
        // is true.
        if (i == 0 && sum != 0 && dp[0][sum]) {
            p.add(arr[i]);
            System.out.println(p); 
            p.clear();
            return;
        }

        // If sum becomes 0
        if (i == 0 && sum == 0) {
            System.out.println(p);
            p.clear();
            return;
        }

        // If given sum can be achieved after ignoring
        // current element.
        if (dp[i - 1][sum]) {
            // Create a new vector to store path
            ArrayList<Integer> b = new ArrayList<>();
            b.addAll(p);
            printSubsetsRec(arr, i - 1, sum, b);
        }

        // If given sum can be achieved after considering
        // current element.
        if (sum >= arr[i] && dp[i - 1][sum - arr[i]]) {
            p.add(arr[i]);
            printSubsetsRec(arr, i - 1, sum - arr[i], p);
        }
    }

    public static void main(String[] args) throws IOException {
        // boring input parsing stuff
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        String[] info = br.readLine().split(" ");
        int numSoldiers = Integer.parseInt(info[0]);
        int numSkills = Integer.parseInt(info[1]);
        int target;

        // tracks all soliders and if there were duplicates
        HashMap<Integer, Integer> map = new HashMap<>();

        for (int i = 0; i < numSoldiers; i++) {
            int k = Integer.parseInt(br.readLine(), 2);
            if (map.putIfAbsent(k, 1) != null) {
                map.put(k, map.get(k) + 1);
            }

        }
        target = Integer.parseInt(br.readLine(), 2);

        // set of unique soliders in an array form
        Integer[] soldierSet = (Integer[]) map.keySet().toArray(new Integer[1]);

        for (int i = 0; i < soldierSet.length; i++) {
            if (soldierSet[i] > target) {
                map.remove(soldierSet[i]);
            }
        }

        for (Integer i : soldierSet) {
            System.out.print(Integer.toString(i) + ", ");
        }
        System.out.println();
        System.out.println(target);

        // amount of soliders x size of each solider, yikes
        dp = new boolean[1 << 20][21];

        for (int i = 21 - 1; i >= 0; i--) {
            dp[0][i] = true;
        }

        for (int i = dp.length - 1; i >= 1; i--) {
            dp[i][0] = false;
        }

        for (int i = 1; i <= target; i++) {
            for (int j = 1; j <= soldierSet.length; j++) {
                dp[i][j] = dp[i][j - 1];
                if (i >= soldierSet[j - 1]) {
                    dp[i][j] = dp[i][j] || dp[i - soldierSet[j - 1]][j - 1];
                }
            }
        }

        ArrayList<Integer> p = new ArrayList<>();
        printSubsetsRec(soldierSet, soldierSet.length - 1, target, p);

        // System.out.println(numSoldiers);
        // System.out.println(numSkills);

        // for (int s : soldiers) System.out.println(s);
        // System.out.println(target);

        // most significant bits on the left side

        // cut soliders with extra skills for later

        // duplicates can double the amount of possible outcomes

    }
}