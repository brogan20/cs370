import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;

public class ResistanceMatcher {

  private static double target;
  private static double tolerance;
  private static double[] bestFit;
  private static double closest = Double.MAX_VALUE;

  private static void printArray(double[] input) {
    System.out.print('[');

    if(input[0] < input[input.length-1]) {
      for(int i = 0; i < input.length; i++) {
        if(i != input.length - 1)
          System.out.print(input[i] + ", ");
        else
          System.out.print(input[i] + "]");
      }
    }
    else
      for(int i = input.length-1; i >= 0; i--) {
          if(i != 0)
            System.out.print(input[i] + ", ");
          else
            System.out.print(input[i] + "]");
      }
  }

    static void combinationUtil(double arr[], double data[], int start,
                                int end, int index, int r) {
        // Current combination is ready to be printed, print it
        if (index == r) {
          double res = resistance(data);
          if(Math.abs(res - target) / target <= tolerance) {
            if(Math.abs(res-target) < Math.abs(closest - target)) {
              closest = res;
              bestFit = Arrays.copyOf(data, data.length);
            }
          }
          return;
        }
 
        for (int i=start; i<=end && end-i+1 >= r-index; i++) {
            data[index] = arr[i];
            combinationUtil(arr, data, i+1, end, index+1, r);
        }
    }
 
    static void printCombination(ArrayList<Double> a, int n, int r) {
      double[] arr = new double[a.size()];
      for(int i = 0; i < a.size(); i++)
        arr[i] = a.get(i);

      double data[]=new double[r];

      combinationUtil(arr, data, 0, n-1, 0, r);
    }

  private static double resistance(double[] arr) {
    double sum = 0.0;

    for(double d : arr) {
      sum += 1 / d;
    }

    return 1 / sum;
  }
  public static void main(String[] args) throws IOException {
    if(args.length < 4) {
      System.err.println("Usage: java ResistanceMatcher <target> <tolerance %> <num resistors> <input file>");
      return;
    }

    try {
      if(Double.parseDouble(args[0]) < 1.0) {
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
    ArrayList<Double> arr = new ArrayList<>();

    // Fill in the array list with numbers
    try {
      while ((line = br.readLine()) != null) {
        if(Double.parseDouble(line) <= 0.0) {
          br.close();
          throw new NumberFormatException("");
        }

        for(int i = 0; i < Integer.parseInt(args[2]); i++)
          arr.add(Double.parseDouble(line));
        lineCount++;
      }
    } catch (NumberFormatException e) {
      System.err.println("Error: Invalid value '" + line + "' on line " + (lineCount+1) + ".");

      return;
    }

    target = Double.parseDouble(args[0]);
    tolerance = Double.parseDouble(args[1]);

    System.out.println("Max resistors in parallel: " + args[2]);
    System.out.println("Tolerance: " +  new DecimalFormat("0.0").format(tolerance) + " %");

    // Convert percentage to decimal
    tolerance /= 100;
    bestFit = null;

    printCombination(arr, arr.size(), Integer.parseInt(args[2]));

    if(bestFit != null) {
      System.out.print("Target resistance of " + args[0] + " ohms is possible with ");
      printArray(bestFit);
      System.out.println(" ohm resistors.");
      System.out.println("Best fit: " + new DecimalFormat("#.0000").format(closest) + " ohms");
      //System.out.println("Percent error: " + Math.abs(target - closest) / target * 100 + " %");
      System.out.println("Percent error: " + new DecimalFormat("0.00").format(Math.abs(target - closest) / target * 100) + " %");
    }
    else {
      System.out.print("Target resistance of " + args[0] + " ohms is not possible.");
    }

    br.close();
  }
}