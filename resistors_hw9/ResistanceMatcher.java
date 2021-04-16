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

      // Check if x is present at mid
      if (res == target)
        return m;

      // If x greater, ignore left half
      if (res < target)
        l = m + 1;

      // If x is smaller, ignore right half
      else
        r = m - 1;
    }

    //System.out.println("Best choice to reach " + target + ": " + bestVal + "\n");
    return bestVal;
  }

  /*
  static double findBest(Double[] choices, double sum, double ignore) {
    //System.out.println("Current: " + sum + ", " + 1/sum);
    //double sum = 1 / resistance;
    double bestDist = Math.abs(1 / (sum + 1/choices[1]) - target);
    double bestVal = choices[1];

    for(int i = 0; i < choices.length; i++) {
      double res = 1 / (sum + 1/choices[i]);
      double dist = Math.abs(res - target);
      if(dist < bestDist && choices[i] != ignore) {
        //System.out.println("Best is now " + dist + ", no longer " + bestDist);
        bestDist = dist;
        //System.out.println("Best is now " + choices[i] + ", no longer " + bestVal);
        bestVal = choices[i];
      }
    }

    //System.out.println("Best choice to reach " + target + ": " + bestVal + "\n");
    return bestVal;
  }
  */

  public static void calculate(Double[] arr, double[][] resistors, double[][] resistances, double[][] sums, int start, int end) {
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

      for(int i = start; i <= end; i++) {
        double sum = 0.0;
        double oldSum = 0.0;

        // Top to current
        oldSum = sums[k-1][i];

        double chosen = findBest(arr, oldSum);
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

  // TODO: Try increasing when binary search is implemented
  // TODO: Use combinations instead of permutations
  static final int BOUND_1 = 51;
  static final int BOUND_2 = 150;
  static final int BOUND_3 = 400;

  static double[][] solve(Set<Double> s) {
    //System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
    Double[] arr = new Double[s.size()];
    System.arraycopy(s.toArray(), 0, arr, 0, s.size());
    //Arrays.sort(arr, Collections.reverseOrder());
    Arrays.sort(arr);
    int size = s.size();

    int maxSize;

    // TODO: Tighter bound
    if(size > BOUND_3) {
      //System.out.println("Too fat");
      //maxSize = (int)Math.pow(size, 2);
      maxSize = size;
    }
    else if(size > BOUND_2) {
      //System.out.println("Too fat");
      //System.out.println("Too bat");
      maxSize = (int)Math.pow(size, 2);
      //maxSize = size;
    }
    else if(size <= BOUND_2 && size > BOUND_1) {
      //System.out.println("Too dat");
      //System.out.println("Too fat");
      maxSize = (int)Math.pow(size, 2);
      //maxSize = size;
    }
    else if(maxInd > 4)
      maxSize = (int)Math.pow(size, 4);
    else
      maxSize = (int)Math.pow(size, maxInd);
    
    double[][] resistors = new double[maxInd+1][maxSize+1];
    double[][] resistances = new double[maxInd+1][maxSize+1];
    double[][] sums = new double[maxInd+1][maxSize+1];

    for(int i = 1; i <= size; i++) {
      //double up = Double.MAX_VALUE;
      double self = arr[i-1];
      //double left = Double.MAX_VALUE;
      resistors[1][i] = self;
      resistances[1][i] = self;
      sums[1][i] = 1 / self;

      /*
      int tmp = i + size;
      //int tmp2 = i + size;

      //resistors[2][i] = arr[0];

      
      int count = 1;
      while(tmp <= maxSize) {
        resistors[1][tmp] = self;
        resistances[1][tmp] = self;
        sums[1][tmp] = 1 / self;

        resistors[2][tmp] = arr[count % arr.length];

        int count2 = 0;
        while(tmp2 <= maxSize) {
          resistors[3][tmp2] = arr[count2 % arr.length];
          tmp2 += size*size;

          count2++;
        }
        

        tmp += size;
        //count++;
      }
      */
      //resistors[1][i+size] = self;
      //resistances[1][i+size] = self;
      //sums[1][i+size] = 1 / self;

      //resistors[1][i] = min3(up, self, left);
      //resistances[1][i] = min3(up, self, left);

    }

    int count = 0;

    if(maxInd > 3 && size <= BOUND_1)
    for(int i = 0; i < size; i++) {
      for(int j = 0; j < size; j++) {
        for(int k = 0; k < size; k++) {
          for(int l = 0; l < size; l++) {
            resistors[1][count] = arr[i];
            resistances[1][count] = arr[i];
            sums[1][count] = 1 / arr[i];
  
            resistors[2][count] = arr[j];
            resistors[3][count] = arr[k];
            resistors[4][count] = arr[l];
  
            count++;
          }

        }
      }
    }
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

          count++;
        }
      }
    }
    else if(maxInd > 1 && size <= BOUND_3)
    for(int i = 1; i <= maxSize; i+=size) {
      for(int j = 0; j < maxInd && i+j < resistors[2].length; j++) {
        resistors[2][i+j] = arr[count % size];
      }
      count++;
    }

    size = maxSize;
    final int sz = size;

    //print(resistors);

    bestI = Integer.MAX_VALUE;
    bestK = Integer.MAX_VALUE;

    //calculate(arr, resistors, resistances, sums, 1, size);

    Thread t = (new Thread() {
      public void run() {
        calculate(arr, resistors, resistances, sums, sz/2, sz);
      }
    });

    t.start();

    calculate(arr, resistors, resistances, sums, 1, sz/2-1);

     try {
      t.join();
     } catch (InterruptedException e) {
       System.exit(1);
     }
     
    /*
    for(int k = 2; k <= maxInd; k++) {
      for(int i = 1; i <= size; i++) {
        double curRes = resistances[k-1][i];

        // Better than previously found
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
      }

      for(int i = 1; i <= size; i++) {
        double sum = 0.0;
        double oldSum = 0.0;

        // Top to current
        oldSum = sums[k-1][i];

        double chosen = findBest(arr, oldSum);
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
    */

    for(int i = 1; i <= size; i++) {
      double curRes = resistances[maxInd][i];

      // Better than previously found
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
      if(Integer.parseInt(args[2]) < 1) {
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

    // Fill in the array list with numbers
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
    //printCombination(arr, arr.size(), Integer.parseInt(args[2]));


    ArrayList<Double> bestCandidate = null;
    // If duplicates, prioritize length first, then resistor sortedness
    if(bestIs.size() > 0) {
      //System.out.println("Potential other solutions: " + bestIs.size());
      bestIs.add(bestI);
      bestKs.add(bestK);

      // Get minimum lengths
      int smallestI = Integer.MAX_VALUE;
      for(int i = 0; i < bestIs.size(); i++) {
        //System.out.println("Got: " + bestIs.get(i) + ", " + bestKs.get(i));
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
        for(int k = 1; k <= bestK; k++) {
          sorted.add(resistors[k][bestI]);
        }
        Collections.sort(sorted);
        candidates.add(sorted);
      }

      bestCandidate = candidates.get(0);

      // Get smallest
      for(int i = 1; i < candidates.size(); i++) {
        for(int j = 0; j < candidates.get(i).size(); j++) {
          if(candidates.get(i).get(j) == bestCandidate.get(j))
            continue;

          if(candidates.get(i).get(j) < bestCandidate.get(j)) {
            bestCandidate = candidates.get(i);
          }
          break;
        }
      }

      //System.out.println("Best: " + bestCandidate);
    }

    if(closest != Double.MAX_VALUE) {
      if(args[0].equals("1"))
        System.out.print("Target resistance of " + args[0] + " ohm is possible with ");
      else System.out.print("Target resistance of " + args[0] + " ohms is possible with ");
      //printArray(bestFit);
      if(bestCandidate == null) {
        ArrayList<Double> sorted = new ArrayList<Double>();
        for(int k = 1; k <= bestK; k++) {
          //System.out.print(resistors[i][bestI] + ", ");
          sorted.add(resistors[k][bestI]);
        }
        Collections.sort(sorted);
        System.out.print(sorted);
      }
      else {
        System.out.print(bestCandidate);
      }

      //System.out.print(resistors[bestK][bestI]);

      System.out.println(" ohm resistors.");
      System.out.println("Best fit: " + new DecimalFormat("#.0000").format(closest) + " ohms");
      //System.out.println("Percent error: " + Math.abs(target - closest) / target * 100 + " %");
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