// Brogan Clements, Dominick DiMaggio, Ishaan Patel
// I pledge my honor that I abide by the Stevens Honor System.

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Vim {

    static final int MODULO = 1000000007;

    public static void main(String[] args) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        String[] nm = br.readLine().split(" ");


        int numSoldiers = Integer.parseInt(nm[0]);
        // int numSkills = Integer.parseInt(nm[1]); // Never needed?

        int[] soldiers = new int[numSoldiers];
        for (int i = 0; i < numSoldiers; i++) {
            soldiers[i] = Integer.parseInt(br.readLine(), 2);
        }
        int target = Integer.parseInt(br.readLine(), 2);
        
        // Big credit to: https://stackoverflow.com/questions/31401705/inclusion-exclusion-in-dynamic-programming
        // Gave us the math needed to implement everything below

        // Prepopulate f(i) with all soldiers == i
        // Also skips any soldiers > target as they cannot be used
        int[] f = new int[2000000];
        for (int i = numSoldiers - 1; i >= 0; i--) {
            if (soldiers[i] <= target) {
                f[soldiers[i]]++;
            }
        }
        
        // calculate f(i) = all numbers which are one bit different or equal to i
        // 20 is the maximum number of skills
        // 1<<20 is the biggest skill
        for (int i = 0; i < 20; i++) {
            for (int j = 0; j <= (1<<20); j++) {
                if ((j & (1 << i)) != 0) {
                    f[j] += f[j ^ (1 << i)];
                }
            }
        }


        // Precalculate a lookup table for powers of 2 to avoid many re-calculations in f(i)
        int[] twoPowers = new int[2000000];
        twoPowers[0] = 1;
        for (int i = 1; i < twoPowers.length; i++) {
            twoPowers[i] = (2 * twoPowers[i - 1]) % MODULO;
        }

        
        // for (int i = 0; i < 10; i++) {
        //     if (f[i] != 0)
        //         System.out.println(i + " " + f[i] + " " + twoPowers[i]);
        // }

        // Calculate the total sum with inclusion-exclusion principle
        // +(2^f(i where i is 0 bits away from target) - 1) - (2^f(i where i is 1 bit away) - 1)
        // + (2^f(i where i is 2 bits away) - 1) and so on 
        int result = 0;
        for (int i = 0; i <= target; i++) {
            // Only consider if i is a valid input
            if ((i | target) == target) {
                if (Integer.bitCount(i ^ target) % 2 == 0) { // Checks how many bits away i is from the target
                    result = (result + (twoPowers[f[i]] - 1)) % MODULO;
                } else {
                    result = (result - (twoPowers[f[i]] - 1) + MODULO) % MODULO;
                }
            }

        }
        System.out.println(result);
    }
}