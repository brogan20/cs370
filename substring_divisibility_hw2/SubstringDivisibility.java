
// Names: Brogan Clements, Dominick DiMaggio, Ishaan Patel
// Pledge: I pledge my honor that I have abided by the Stevens Honor System.
import java.io.BufferedWriter;
import java.io.OutputStreamWriter;

public class SubstringDivisibility {

    private static long sum = 0;
    private static int[] divisors = { 0, 0, 0, 2, 3, 5, 7, 11, 13, 17 };
    static BufferedWriter output = new BufferedWriter(new OutputStreamWriter(System.out));

    private static void processPermutation(int[] a) throws Exception {
        // https://stackoverflow.com/questions/41271299/how-can-i-get-the-first-two-digits-of-a-number
        // Based on this we decided to just stick with processing in lists

        // Start with d2d3d4 as we're guarenteed to have a list where 4 <= len(a) <= 10

        for (int i = 3; i < a.length; i++) {
            int currNum = a[i - 2] * 100 + a[i - 1] * 10 + a[i];
            if (currNum % divisors[i] != 0) {
                return;
            }

        }

        long fullNum = 0;
        for (int i : a) {
            output.write(i);
            fullNum = fullNum * 10 + i;
        }
        output.newLine();
        sum += fullNum;
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
        output.write("Sum: ");
        output.write(Long.toString(i));
        output.newLine();
        System.out.printf("Elapsed time: %.6f ms\n", (System.nanoTime() - start) / 1e6);

    }

}