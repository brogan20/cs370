import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;

public class Vim {

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
        Integer[] soldierSet = (Integer[])map.keySet().toArray();

        for (int i = 0; i < soldierSet.length; i++) {
            if (soldierSet[i] > target) {
                map.remove(soldierSet[i]);
            }
        }

        // amount of soliders x size of each solider, yikes
        int [][] dp = new int[1 << 20][21];
        

        
        // System.out.println(numSoldiers);
        // System.out.println(numSkills);

        // for (int s : soldiers) System.out.println(s);
        // System.out.println(target);
        
        //most significant bits on the left side
        
        // cut soliders with extra skills for later

        //duplicates can double the amount of possible outcomes 

        
    }
}