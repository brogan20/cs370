import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class ResistanceMatcher {



  // TODO: Binary search
  static double findBest(Double[] choices, double sum) {
    // System.out.println("Current: " + sum + ", " + 1/sum);
    // double sum = 1 / resistance;
    double bestDist = Math.abs(1 / (sum + 1 / choices[0]) - target);
    double bestVal = choices[0];

    for (int i = 1; i < choices.length; i++) {
      double res = 1 / (sum + 1 / choices[i]);
      double dist = Math.abs(res - target);
      if (dist < bestDist) {
        // System.out.println("Best is now " + dist + ", no longer " + bestDist);
        bestDist = dist;
        // System.out.println("Best is now " + choices[i] + ", no longer " + bestVal);
        bestVal = choices[i];
      }
    }

    // System.out.println("Best choice to reach " + target + ": " + bestVal + "\n");
    return bestVal;
  }

  private static double target;
  private static double tolerance;
  private static int maxInd;
  private static double[] bestFit;
  private static double closest = Double.MAX_VALUE;
  private static int bestI, bestK;

  static void solve(Set<Double> s) {
    // System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
    Double[] invResistances = new Double[s.size()];
    System.arraycopy(s.toArray(), 0, invResistances, 0, s.size());
    Arrays.sort(invResistances, Collections.reverseOrder());
    Arrays.asList(invResistances).stream().map(d -> 1 / d);
    int size = s.size();

    double sum = 0;
    int[] used = new int[invResistances.length];
  

    for (int i = 0; i < invResistances.length; i++) {
      double tmp = sum + invResistances[i];
      double tmpError = Math.abs(tmp - target) / target;
      // iterates over used, which tracks which resistors we used alrady and the number of them
      int indUsed = -1;
      for (int j = 0; j < used.length; j++) {
        if (used[j] > 0) {
          double testErr = Math.abs(tmp - used[j] - target) / target;
          if (tmpError > testErr) {
            // need to track what's been added/removed
            // limit to one change per loop
            // 
          }
        }
      }
    } 


  }

  static void print(double[][] arr) {
    System.out.println(
        "~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
    DecimalFormat fmt = new DecimalFormat("0.000");

    for (int i = 0; i < arr.length; i++) {
      for (int j = 0; j < arr[i].length; j++) {
        if (j != arr[i].length - 1)
          System.out.print(fmt.format(arr[i][j]) + ", ");
        else
          System.out.println(fmt.format(arr[i][j]));
      }
    }
  }

  private static void calcResistances(double[] arr, double[] out) {
    double sum = 0.0;

    for (int i = 0; i < arr.length; i++) {
      double d = arr[i];
      sum += 1 / d;
      out[i] = 1 / sum;
    }
  }

  // Slow, make obsolete with nodes later
  private static void recalcRes(double[][] resistors, double[][] resistances) {
    for (int i = 1; i < resistors[0].length; i++) {
      double sum = 0.0;

      for (int j = 1; j < resistors.length; j++) {
        double d = resistors[j][i];
        sum += 1 / d;
        resistances[j][i] = 1 / sum;
      }
    }
  }

  private static boolean inRange(double res) {
    return Math.abs(res - target) / target <= tolerance;
  }

  private static double resistance(double[] arr) {
    double sum = 0.0;

    for (double d : arr) {
      if (d == 0)
        return (1 / sum);

      sum += 1 / d;
    }

    return 1 / sum;
  }

  public static void main(String[] args) throws IOException {
    if (args.length < 4) {
      System.err.println("Usage: java ResistanceMatcher <target> <tolerance %> <num resistors> <input file>");
      return;
    }

    try {
      if (Double.parseDouble(args[0]) < 1.0) {
        System.err.println("Error: Invalid target value '" + args[0] + "'.");
        return;
      }
    } catch (NumberFormatException e) {
      System.err.println("Error: Invalid target value '" + args[0] + "'.");
      return;
    }

    try {
      if (Double.parseDouble(args[1]) < 0.0) {
        System.err.println("Error: Invalid tolerance value '" + args[1] + "'.");
        return;
      }
    } catch (NumberFormatException e) {
      System.err.println("Error: Invalid tolerance value '" + args[1] + "'.");
      return;
    }

    try {
      if (Integer.parseInt(args[2]) < 1) {
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
      if (br != null)
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
        if (Double.parseDouble(line) <= 0.0) {
          br.close();
          throw new NumberFormatException("");
        }

        arr.add(Double.parseDouble(line));
        lineCount++;
      }
    } catch (NumberFormatException e) {
      System.err.println("Error: Invalid value '" + line + "' on line " + (lineCount + 1) + ".");

      return;
    }

    target = Double.parseDouble(args[0]);
    tolerance = Double.parseDouble(args[1]);
    maxInd = Integer.parseInt(args[2]);

    System.out.println("Max resistors in parallel: " + args[2]);
    System.out.println("Tolerance: " + new DecimalFormat("0.0").format(tolerance) + " %");

    // Convert percentage to decimal
    tolerance /= 100;
    bestFit = null;

    // double[][] resistors = solve(arr);
    solve(arr);
    // printCombination(arr, arr.size(), Integer.parseInt(args[2]));

    /*
     * if(closest != Double.MAX_VALUE) { System.out.print("Target resistance of " +
     * args[0] + " ohms is possible with "); //printArray(bestFit);
     * ArrayList<Double> sorted = new ArrayList<Double>(); for(int k = 1; k <=
     * bestK; k++) { //System.out.print(resistors[i][bestI] + ", ");
     * //sorted.add(resistors[k][bestI]); } Collections.sort(sorted);
     * System.out.print(sorted); //System.out.print(resistors[bestK][bestI]);
     * 
     * System.out.println(" ohm resistors."); System.out.println("Best fit: " + new
     * DecimalFormat("#.0000").format(closest) + " ohms");
     * //System.out.println("Percent error: " + Math.abs(target - closest) / target
     * * 100 + " %"); System.out.println("Percent error: " + new
     * DecimalFormat("0.00").format(Math.abs(target - closest) / target * 100) +
     * " %"); } else { System.out.print("Target resistance of " + args[0] +
     * " ohms is not possible."); }
     */

    br.close();
  }
}