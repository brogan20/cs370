import java.util.*;

public class pand {

    static Set<Integer> set = new HashSet<>();

    static void testPerms(int perm[]) {
        //bit shifting is powers of 2, so 2*i + 8*i to multiply by 
        int multiplicand = perm[0] * 10 + perm[1];
        int multiplier = perm[2] * 100 + (perm[3] << 1) + (perm[3] << 3) + perm[4];
        int product = perm[5] * 1000 + perm[6] * 100 + perm[7] * 10 + perm[8];

        if (multiplicand * multiplier == product && !set.contains(product)) {
            System.out.println(Integer.toString(multiplicand) + " * " + Integer.toString(multiplier) 
                + " = " + Integer.toString(product));
            set.add(product);
        }

        multiplicand = perm[0];
        multiplier = perm[1] * 1000 + perm[2] * 100 + perm[3] * 10 + perm[4];
        product = perm[5] * 1000 + perm[6] * 100 + perm[7] * 10 + perm[8];
        if (multiplicand * multiplier == product && !set.contains(product)) {
            System.out.println(Integer.toString(multiplicand) + " * " + Integer.toString(multiplier) 
                + " = " + Integer.toString(product));
            set.add(product);
        }
    }

    static void heapPermutation(int a[], int size, int n) {
        if (size == 1) {
            testPerms(a);
        }

        for (int i = 0; i < size; i++) {
            heapPermutation(a, size - 1, n);

            // if size is odd, swap 0th i.e (first) and
            // (size-1)th i.e (last) element
            if (size % 2 == 1) {
                int temp = a[0];
                a[0] = a[size - 1];
                a[size - 1] = temp;
            }

            // If size is even, swap ith
            // and (size-1)th i.e last element
            else {
                int temp = a[i];
                a[i] = a[size - 1];
                a[size - 1] = temp;
            }
        }
    }

    public static void main(String[] args) {
        //
        int[] a = { 1, 2, 3, 4, 5, 6, 7, 8, 9 };
        heapPermutation(a, a.length, a.length);

        
        int sum = 0;
        for (int n : set) {
            sum += n;
        }
        
        System.out.println(sum);
    }
}