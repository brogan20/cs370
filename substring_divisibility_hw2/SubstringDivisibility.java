
// Names: Brogan Clements, Dominick DiMaggio, Ishaan Patel
// Pledge: I pledge my honor that I have abided by the Stevens Honor System.
import java.io.BufferedWriter;
import java.io.OutputStreamWriter;
import java.util.*;

public class SubstringDivisibility {

    private static HashSet<Integer> resultSet = new HashSet<>();
    private static int sum = 0;

    private static void processPermutation(int[] a) {
        // https://stackoverflow.com/questions/41271299/how-can-i-get-the-first-two-digits-of-a-number
        // Based on this we decided to just stick with processing in lists

        // Start with d2d3d4 as we're guarenteed to have a list where 4 <= len(a) <= 10

        for (int i = 3; i < a.length; i++) {
            int currNum = a[i - 2] * 100 + a[i - 1] * 10 + a[i];
            switch (i) {
                case 3:
                    if (currNum % 2 != 0) {
                        return;
                    }
                    break;
                case 4:
                    if (currNum % 3 != 0) {
                        return;
                    }
                    break;
                case 5:
                    if (currNum % 5 != 0) {
                        return;
                    }
                    break;
                case 6:
                    if (currNum % 7 != 0) {
                        return;
                    }
                    break;
                case 7:
                    if (currNum % 11 != 0) {
                        return;
                    }
                    break;
                case 8:
                    if (currNum % 13 != 0) {
                        return;
                    }
                    break;
                case 9:
                    if (currNum % 17 != 0) {
                        return;
                    }
                    break;
            }

        }

        int fullNum = 0;
        for (int i : a) {
            fullNum = fullNum * 10 + i;
        }
        sum += fullNum;
        resultSet.add(fullNum);
    }

    public static void main(String[] args) throws Exception {
        long start = System.nanoTime();

        // Convert input string into an int array for the following algorithm
        var a = new int[args[0].length()];
        for (int i = 0; i < a.length; i++) {
            a[i] = args[0].charAt(i) - '0';
        }

        // Heap's algorithm for generating all permutations
        // https://en.wikipedia.org/wiki/Heap%27s_algorithm
        int n = a.length;

        var c = new int[a.length];

        // Use first permutation of a
        processPermutation(a);

        int i = 0;
        while (i < n) {
            if (c[i] < i) {
                if (i % 2 == 0) {
                    int tmp = a[i];
                    a[i] = a[0];
                    a[0] = tmp;
                } else {
                    int tmp = a[i];
                    a[i] = a[c[i]];
                    a[c[i]] = tmp;
                }

                // Use new combination of A
                processPermutation(a);

                c[i] += 1;
                i = 0;
            } else {
                c[i] = 0;
                i += 1;
            }
        }
        System.out.print("Sum: ");
        System.out.println(sum);
        resultSet.forEach(System.out::println);
        System.out.printf("Elapsed time: %.6f ms\n", (System.nanoTime() - start) / 1e6);

    }

}