// Names: Dominick DiMaggio, Brogan Clements, Ishaan Patel
// Pledge: I pledge my honor that I have abided by the Stevens Honor System.

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.concurrent.Semaphore;
import java.util.HashSet;
import java.util.Set;

public class ResistanceMatcher {

  private static double target;
  private static double tolerance;
  private static int maxInd;
  private static double[] bestFit;
  private static double closest = Double.MAX_VALUE;
  private static int bestI, bestK;
  private static Semaphore closestSem = new Semaphore(1);

  private static ArrayList<Integer> bestIs, bestKs;


  // Account for roundoff error
  static boolean closeEnough(double res1, double res2) {
    return Math.abs(res1-res2) < 0.000000000000001;
  }

  // Finds the best resistor to add to get the closest to the target value
  static double findBest(Double[] choices, double sum) {
    double bestDist = Math.abs(1 / (sum + 1/choices[0]) - target);
    double bestVal = choices[0];

    int l = 0, r = choices.length - 1;
    while (l <= r) {
      int m = l + (r - l) / 2;

      double res = 1 / (sum + 1/choices[m]);
      double dist = Math.abs(res - target);
      if(dist < bestDist) {
        bestDist = dist;
        bestVal = choices[m];
      }

      // Stop short at exact match
      if (res == target)
        return m;

      // Ignore left half
      if (res < target)
        l = m + 1;

      // Ignore right half
      else
        r = m - 1;
    }

    return bestVal;
  }

    // Finds the NEXT best resistor to add to get the closest to the target value
    static double findNextBest(Double[] choices, double sum, double best) {
      double bestDist = Math.abs(1 / (sum + 1/choices[0]) - target);
      double bestVal = choices[0];
  
      int l = 0, r = choices.length - 1;
      while (l <= r) {
        int m = l + (r - l) / 2;
  
        double res = 1 / (sum + 1/choices[m]);
        double dist = Math.abs(res - target);
        if(dist < bestDist && choices[m] != best) {
          bestDist = dist;
          bestVal = choices[m];
        }
  
        // Stop short at exact match
        if (res == target)
          return m;
  
        // Ignore left half
        if (res < target)
          l = m + 1;
  
        // Ignore right half
        else
          r = m - 1;
      }
  
      return bestVal;
    }

  public static void calculate(Double[] arr, double[][] resistors, double[][] resistances, double[][] sums, int start, int end, boolean nextBest) {
    for(int k = 2; k <= maxInd; k++) {
      for(int i = start; i <= end; i++) {
        double curRes = resistances[k-1][i];

        // Better than previously found
        if(inRange(curRes) && Math.abs(curRes-target) < Math.abs(closest-target)) {
          try {
            closestSem.acquire();
          } catch (InterruptedException e) {
            System.exit(1);
          }
          
          // Check again after entering CS to avoid making a false adjustment
          if(inRange(curRes) && Math.abs(curRes-target) < Math.abs(closest-target)) {
            if(closeEnough(closest, curRes)) {
              bestIs.add(bestI);
              bestKs.add(bestK);
            }
            else {
              bestIs.clear();
              bestKs.clear();
            }
  
            closest = curRes;
  
            bestI = i;
            bestK = k-1;
          }
          closestSem.release();
        }
      }

      // Greedily find best resistor at given iteration
      for(int i = start; i <= end; i++) {
        double sum = 0.0;
        double oldSum = 0.0;

        // Top to current
        oldSum = sums[k-1][i];

        double chosen = findBest(arr, oldSum);
        if(k == 2 && nextBest)
          chosen = findNextBest(arr, oldSum, chosen);

        if(resistors[k][i] != 0.0)
          chosen = resistors[k][i];

        sum = oldSum + (1 / chosen);

        sums[k][i] = sum;
        double newRes = 1 / sum;

        double self = newRes;
        
        resistors[k][i] = chosen;
        resistances[k][i] = self;
      }
    }
  }

  static final int BOUND_1 = 51;
  static final int BOUND_2 = 150;
  static final int BOUND_3 = 400;

  static double[][] solve(Set<Double> s) {
    Double[] arr = new Double[s.size()];
    System.arraycopy(s.toArray(), 0, arr, 0, s.size());
    Arrays.sort(arr);
    int size = s.size();

    int maxSize;

    if(size > BOUND_3) {
      maxSize = size;
    }
    else if(size > BOUND_2) {
      maxSize = (int)Math.pow(size, 2);
    }
    else if(size <= BOUND_2 && size > BOUND_1) {
      maxSize = (int)Math.pow(size, 2);
    }
    else if(maxInd > 4)
      maxSize = (int)Math.pow(size, 4);
    else
      maxSize = (int)Math.pow(size, maxInd);
    
    double[][] resistors = new double[maxInd+1][(maxSize+1)*2];
    double[][] resistances = new double[maxInd+1][(maxSize+1)*2];
    double[][] sums = new double[maxInd+1][(maxSize+1)*2];

    for(int i = 1; i <= size; i++) {
      double self = arr[i-1];
      resistors[1][i] = self;
      resistances[1][i] = self;
      sums[1][i] = 1 / self;
    }

    int count = 0;

    // Brute force up to depth of 4 for smaller inputs
    if(maxInd > 3 && size <= BOUND_1) {
      for(int i = 0; i < size; i++)
      for(int j = 0; j < size; j++)
      for(int k = 0; k < size; k++)
      for(int l = 0; l < size; l++) {
        resistors[1][count] = arr[i];
        resistances[1][count] = arr[i];
        sums[1][count] = 1 / arr[i];

        resistors[2][count] = arr[j];
        resistors[3][count] = arr[k];
        resistors[4][count] = arr[l];

        // Duplicates
        resistors[1][count+maxSize] = arr[i];
        resistances[1][count+maxSize] = arr[i];
        sums[1][count+maxSize] = 1 / arr[i];

        resistors[2][count+maxSize] = arr[j];
        resistors[3][count+maxSize] = arr[k];
        resistors[4][count+maxSize] = arr[l];

        count++;
      }
    }
    // Brute force depth 3
    else if(maxInd > 2 && size <= BOUND_2)
    for(int i = 0; i < size; i++) {
      for(int j = 0; j < size; j++) {
        for(int k = 0; k < size; k++) {
          if(count > maxSize) {
            k = size;
            j = size;
            i = size;
            break;
          }
          resistors[1][count] = arr[i];
          resistances[1][count] = arr[i];
          sums[1][count] = 1 / arr[i];

          resistors[2][count] = arr[j];
          resistors[3][count] = arr[k];

          // Duplicates
          resistors[1][count+maxSize] = arr[i];
          resistances[1][count+maxSize] = arr[i];
          sums[1][count+maxSize] = 1 / arr[i];

          resistors[2][count+maxSize] = arr[j];
          resistors[3][count+maxSize] = arr[k];

          count++;
        }
      }
    }
    // Depth 2
    else if(maxInd > 1 && size <= BOUND_3)
    for(int i = 1; i <= maxSize; i+=size) {
      for(int j = 0; j < maxInd && i+j < resistors[2].length; j++) {
        resistors[2][i+j] = arr[count % size];
        // Duplicates
        resistors[2][i+j+maxSize] = arr[count % size];
      }
      count++;
    }

    size = maxSize*2 + 1;
    final int sz = size;

    bestI = Integer.MAX_VALUE;
    bestK = Integer.MAX_VALUE;

    // Process second half of data on new thread
    Thread t = (new Thread() {
      public void run() {
        calculate(arr, resistors, resistances, sums, sz/2, sz, true);
      }
    });

    t.start();

    // Process first half of data on main thread
    calculate(arr, resistors, resistances, sums, 1, sz/2-1, false);

    try {
      t.join();
    } catch (InterruptedException e) {
      System.exit(1);
    }

    // Check results after each iteration (depth)
    for(int i = 1; i <= size; i++) {
      double curRes = resistances[maxInd][i];

      // If better than previous best
      if(inRange(curRes) && Math.abs(curRes-target) < Math.abs(closest-target)) {
        if(closeEnough(closest, curRes)) {
          bestIs.add(bestI);
          bestKs.add(bestK);
        }
        else {
          bestIs.clear();
          bestKs.clear();
        }

        closest = curRes;

        bestI = i;
        bestK = maxInd;
      }
    }

    return resistors;
  }

  // Debug method
  static void print(double[][] arr) {
    System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
    DecimalFormat fmt = new DecimalFormat("0.000");

    for(int i = 0; i < arr.length; i++) {
      for(int j = 0; j < arr[i].length; j++) {
        if(j != arr[i].length-1)
          System.out.print(fmt.format(arr[i][j]) + ",\t");
        else
          System.out.println(fmt.format(arr[i][j]));
      }
    }
  }

  private static boolean inRange(double res) {
    return Math.abs(res - target) / target <= tolerance;
  }

  public static void main(String[] args) throws IOException {
    // ~~~~~~~~~~~~~~~~~~~~~~~~~ Start input checking ~~~~~~~~~~~~~~~~~~~~~~~~~
    if(args.length < 4) {
      System.err.println("Usage: java ResistanceMatcher <target> <tolerance %> <num resistors> <input file>");
      return;
    }

    try {
      if(Double.parseDouble(args[0]) <= 0.0) {
        System.err.println("Error: Invalid target value '" + args[0] + "'.");
        return;
      }
    } catch (NumberFormatException e) {
      System.err.println("Error: Invalid target value '" + args[0] + "'.");
      return;
    }

    try {
      if(Double.parseDouble(args[1]) < 0.0) {
        System.err.println("Error: Invalid tolerance value '" + args[1] + "'.");
        return;
      }
    } catch (NumberFormatException e) {
      System.err.println("Error: Invalid tolerance value '" + args[1] + "'.");
      return;
    }

    try {
      if(Integer.parseInt(args[2]) < 1 || Integer.parseInt(args[2]) > 10)  {
        System.err.println("Error: Invalid number of resistors '" + args[2] + "'.");
        return;
      }
    } catch (NumberFormatException e) {
      System.err.println("Error: Invalid number of resistors '" + args[2] + "'.");
      return;
    }

    File file = new File(args[3]);
    BufferedReader br = null;

    try {
        br = new BufferedReader(new FileReader(file));
    } catch (Exception e) {
        if(br != null)
          br.close();
        System.err.println("Error: Input file '" + args[3] + "' not found.");
        return;
    }

    String line = "";
    int lineCount = 0;
    Set<Double> arr = new HashSet<>();

    // Fill in the array list with numbers from file
    try {
      while ((line = br.readLine()) != null) {
        if(Double.parseDouble(line) <= 0.0) {
          br.close();
          throw new NumberFormatException("");
        }

        arr.add(Double.parseDouble(line));          
        lineCount++;
      }
    } catch (NumberFormatException e) {
      System.err.println("Error: Invalid value '" + line + "' on line " + (lineCount+1) + ".");
      return;
    }

    // ~~~~~~~~~~~~~~~~~~~~~~~~~ End input checking ~~~~~~~~~~~~~~~~~~~~~~~~~

    target = Double.parseDouble(args[0]);
    tolerance = Double.parseDouble(args[1]);
    maxInd = Integer.parseInt(args[2]);

    System.out.println("Max resistors in parallel: " + args[2]);
    System.out.println("Tolerance: " +  new DecimalFormat("0.0").format(tolerance) + " %");

    // Convert percentage to decimal
    tolerance /= 100;
    bestFit = null;

    bestIs = new ArrayList<Integer>();
    bestKs = new ArrayList<Integer>();

    double[][] resistors = solve(arr);

    ArrayList<Double> bestCandidate = null;
    // If duplicates present, prioritize length first, then resistor sortedness
    if(bestIs.size() > 0) {
      bestIs.add(bestI);
      bestKs.add(bestK);

      // Get minimum length
      int smallestI = Integer.MAX_VALUE;
      for(int i = 0; i < bestIs.size(); i++) {
        if(bestIs.get(i) < smallestI) {
          smallestI = bestIs.get(i);
        }
      }

      ArrayList<Integer> candIs = new ArrayList<>();

      for(int i = 0; i < bestIs.size(); i++) {
        if(bestIs.get(i) == smallestI) {
          candIs.add(bestIs.get(i));
          bestK = bestKs.get(i);
        }
      }

      // Sort values
      ArrayList<ArrayList<Double>> candidates = new ArrayList<>();
      for(int i = 0; i < bestIs.size(); i++) {
        bestI = bestIs.get(i);

        ArrayList<Double> sorted = new ArrayList<Double>();
        for(int k = 1; k <= bestK; k++)
          sorted.add(resistors[k][bestI]);

        Collections.sort(sorted);
        candidates.add(sorted);
      }

      bestCandidate = candidates.get(0);

      // Get smallest
      for(int i = 1; i < candidates.size(); i++) {
        for(int j = 0; j < candidates.get(i).size(); j++) {
          if(candidates.get(i).get(j) == bestCandidate.get(j))
            continue;

          if(candidates.get(i).get(j) < bestCandidate.get(j))
            bestCandidate = candidates.get(i);
        }
      }
    }

    if(closest != Double.MAX_VALUE) {
      if(args[0].equals("1"))
        System.out.print("Target resistance of " + args[0] + " ohm is possible with ");
      else System.out.print("Target resistance of " + args[0] + " ohms is possible with ");
      if(bestCandidate == null) {
        ArrayList<Double> sorted = new ArrayList<Double>();
        for(int k = 1; k <= bestK; k++)
          sorted.add(resistors[k][bestI]);

        Collections.sort(sorted);
        System.out.print(sorted);
      }
      else
        System.out.print(bestCandidate);

      System.out.println(" ohm resistors.");
      System.out.println("Best fit: " + new DecimalFormat("#.0000").format(closest) + " ohms");
      System.out.println("Percent error: " + new DecimalFormat("0.00").format(Math.abs(target - closest) / target * 100) + " %");
    }
    else {
      if(args[0].equals("1"))
        System.out.println("Target resistance of " + args[0] + " ohm is not possible.");
      else System.out.println("Target resistance of " + args[0] + " ohms is not possible.");
    }

    br.close();
  }
}