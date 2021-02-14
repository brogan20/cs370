
// Names: Brogan Clements, Dominick DiMaggio, Ishaan Patel
// Pledge: I pledge my honor that I have abided by the Stevens Honor System.
import java.io.BufferedWriter;
import java.io.OutputStreamWriter;

public class SubstringDivisibility {

    private static long sum = 0;
    private static int[] divisors = { 0, 0, 0, 2, 3, 5, 7, 11, 13, 17 };
    static BufferedWriter output = new BufferedWriter(new OutputStreamWriter(System.out));

    private static void processautation(int[] a) throws Exception {
        // https://stackoverflow.com/questions/41271299/how-can-i-get-the-first-two-digits-of-a-number
        // Based on this we decided to just stick with processing in lists

        // Start with d2d3d4 as we're guarenteed to have a list where 4 <= len(a) <= 10

        for (int i = 3; i < a.length; i++) {
            int two = (a[i-1] << 1) + (a[i-1] << 3);
            int three = (a[i-2] << 6) + (a[i-2] << 5) + (a[i-2] << 2);
            int currNum = three + two + a[i];
            if (currNum % divisors[i] != 0) {
                return;
            }
        }

        //printing out the values and also finding the sum
        long fullNum = 0;
        for (int i : a) {
            output.write(i + '0');
            fullNum = fullNum * 10 + i;
        }
        output.newLine();
        sum += fullNum;
    }

    static void reverse(int a[], int start, int end) {
        while(start < end) {
            int tmp = a[start];
            a[start] = a[end];
            a[end] = tmp;
            start++;
            end--;
        }
    }

    public static void main(String[] args) throws Exception {
        long start = System.nanoTime();

        // Convert input string into an int array for the following algorithm
        var a = new int[args[0].length()];
        for (int i = 0; i < a.length; i++) {
            a[i] = args[0].charAt(i) - '0';
        }

        // Heap's algorithm for generating all autations
        // https://en.wikipedia.org/wiki/Heap%27s_algorithm
        //int n = a.length;

        //var c = new int[a.length];

        // Use first autation of a
        processautation(a);

        boolean cont = false;
        for(int i = 0; i < a.length - 1; i++) {
            if(a[i] < a[i+1]) {
                cont = true;
                break;
            }
        }

        while(cont) {

            int i;
            for(i = a.length-2; i >= 0; i--) {
                if(a[i] < a[i+1])
                    break;
            }

            int j;
            for(j = a.length-1; j >= 0; j--) {
                if(a[i] < a[j])
                    break;
            }

            int tmp = a[i];
            a[i] = a[j];
            a[j] = tmp;

            int startInd = i+1, endInd = a.length - 1;
            while(startInd < endInd) {
                int tmp_ = a[startInd];
                a[startInd] = a[endInd];
                a[endInd] = tmp_;
                startInd++;
                endInd--;
            }

            //reverse(a, i+1, a.length-1);

            processautation(a);
            //printArr(a);

            cont = false;
            for(int k = 0; k < a.length - 1; k++) {
                if(a[k] < a[k+1]) {
                    cont = true;
                    break;
                }
            }
        }

        /*

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
                processautation(a);

                c[i] += 1;
                i = 0;
            } else {
                c[i] = 0;
                i += 1;
            }
        }
        */

        output.write("Sum: ");
        output.write(Long.toString(sum));
        output.newLine();
        output.flush();
        System.out.printf("Elapsed time: %.6f ms\n", (System.nanoTime() - start) / 1e6);

    }

}