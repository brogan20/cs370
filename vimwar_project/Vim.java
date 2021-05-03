import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.ArrayList;

public class Vim {

    static final int MAX_SKILL = 2000000; // 2^20 or 1111 1111 1111 1111 1111
    static final int HIGHEST_SKILL = 1 << 20;
    static final int MAX_N = 100000;
    static final int MAX_M = 20;
    static final int MODULUS = 1000000007;
    static int target;

    public static void main(String[] args) throws IOException {
        // boring input parsing stuff
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        String[] nm = br.readLine().split(" ");


        int numSoldiers = Integer.parseInt(nm[0]);
        int numSkills = Integer.parseInt(nm[1]);

        int[] soldiers = new int[numSoldiers];
        for (int i = 0; i < numSoldiers; i++) {
            soldiers[i] = Integer.parseInt(br.readLine(), 2);
        }
        target = Integer.parseInt(br.readLine(), 2);
        
        int[] f = new int[MAX_SKILL];
        
        for (int i = numSoldiers - 1; i >= 0; i--) {
            if (soldiers[i] <= target) {
                f[soldiers[i]]++;
            }
        }
        
        
        for (int i = 0; i < MAX_M; i++) {
            for (int j = 0; j <= HIGHEST_SKILL; j++) {
                if ((j & (1 << i)) == 0) {
                    f[j] += f[j ^ (1 << i)];
                }
            }
        }

        int[] twoPowers = new int[MAX_SKILL];
        twoPowers[0] = 1;
        for (int i = 1; i < twoPowers.length; i++) {
            twoPowers[i] = (2 * twoPowers[i - 1]) % MODULUS;
        }


        // for (int i = 0; i < f.length; i++) {
        //     // if (f[i] != 0)
        //         System.out.println(i + " " + f[i]);
        // }

        int result = 0;
        for (int i = target; i >= 0; i--) {
            if (Integer.bitCount(f[i] ^ result) % 2 == 1) {
                result = (result + twoPowers[f[i]]) % MODULUS;
            } else {
                result = (result - twoPowers[f[i]] + MODULUS) % MODULUS;
            }
        }

        System.out.println(result);
    }
}