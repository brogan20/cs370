// Names: Brogan Clements, Dominick DiMaggio, Ishaan Patel
// Pledge: I pledge my honor that I have abided by the Stevens Honor System.

public class SubstringDivisibility {

    public static void main(String[] args) {
        int[] a = new int[args[0].length()];
        for (int i = 0; i < a.length; i++) {
            a[i] = args[0].charAt(i) - '0';
        }

        // Heap's algorithm for generating all permutations
        // https://en.wikipedia.org/wiki/Heap%27s_algorithm
        int n = a.length;

        int[] c = new int[a.length];

        // Use first permutation of a
        for (int b : a) {
            System.out.print(Integer.toString(b) + " ");
        }
        System.out.println();

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
                for (int b : a) {
                    System.out.print(Integer.toString(b) + " ");
                }
                System.out.println();

                c[i] += 1;
                i = 0;
            } else {
                c[i] = 0;
                i += 1;
            }
        }
    }

}