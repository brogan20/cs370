/*******************************************************************************
 * Name          : PandigitalProducts.java
 * Author        : Brian S. Borowski
 * Version       : 1.0
 * Date          : March 23, 2015
 * Last modified : February 22, 2018
 * Description   : Solution to Project Euler #32
 ******************************************************************************/
public class PandigitalProducts {

    /**
     * Uses a single integer as a bit vector of all the digits seen in the
     * parameter num. Digit 0 is stored in the least significant bit. Higher
     * digits are stored in more significant bits (to the left).
     * @param num  the integer whose digits will be extracted
     * @return     a single integer representing which digits are present in num
     */
    public static int extractDigits(int num) {
        int bitVector = 0;
        while (num > 0) {
            bitVector |= 1 << num % 10;
            num /= 10;
        }
        return bitVector;
    }

    /**
     * Returns whether or not integers a, b, and product combined are
     * pandigital.
     * @param a        an integer
     * @param b        an integer
     * @param product  an integer containing the product of a and b
     * @return         true if digits 1-9 are contained exactly once in a, b and
     *                 product; false otherwise
     */
    public static boolean isPandigital(int a, int b, int product) {
        return (extractDigits(a) |
                extractDigits(b) |
                extractDigits(product)) == 1022;
    }

    public static void main(String[] args) {
    	long start = System.nanoTime();
        final int upperLimit = 10000;

        // Implement a hash set with just an array of booleans.
        boolean[] productSet = new boolean[upperLimit];
        for (int a = 2; a <= 98; a++) {
            for (int b = 1000 / a, limit = upperLimit / a; b <= limit; b++) {
                int product = a * b;
                if (isPandigital(a, b, product)) {
                    System.out.println(a + " x " + b + " = " + product);
                    productSet[product] = true;
                }
            }
        }
        int sumOfProducts = 0;
        for (int i = 0; i < upperLimit; i++) {
            if (productSet[i]) {
                sumOfProducts += i;
            }
        }
        System.out.println("Sum of unique products: " + sumOfProducts);
        double elapsed = (System.nanoTime() - start) / 1e6;
        System.out.println("Elapsed time: " + elapsed + " ms");
    }
}
