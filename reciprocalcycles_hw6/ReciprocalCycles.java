// Names: Brogan Clements, Dominick DiMaggio, Ishaan Patel
// Pledge: I pledge my honor that I have abided by the Stevens Honor System.

import java.util.LinkedList;
import java.math.MathContext;
import java.math.RoundingMode;

public class ReciprocalCycles {

    public static void main(String[] args) {
        int denominator = -1;
        String buf = "0.";

        // check that only one argument was passed in the command line
        if (args.length != 1) {
            System.out.print("Usage: java ReciprocalCycles <denominator>");
            return;
        }
        
        // check if input is an integer
        try {
            denominator = Integer.parseInt(args[0]);
        } catch (Exception e) {
            System.err.printf("Error: Denominator must be an integer in [1, 2000]. Received '%s'.\n", args[0]);
            return;
        }
        
        // check if input is within valid range
        if (denominator < 1 || denominator > 2000) {
            System.err.printf("Error: Denominator must be an integer in [1, 2000]. Received '%s'.\n", args[0]);
            return;
        }

        //hard coding this because i'm lazy
        if (denominator == 1) {
            System.out.print("1/1 = 1\n");
            return;
        }
        
        LinkedList<Integer> tracker = new LinkedList<>();
        int numerator = 1;
        int lastVal = numerator % denominator;

        // using long division to build the number
        // while loop keeps going until we encounter a mod number we already have
        // or if it just divides cleanly with no cycle
        while (tracker.indexOf(lastVal) == -1 && lastVal != 0) {
            tracker.addLast(lastVal);
            numerator = lastVal * 10;
            lastVal = numerator % denominator;
            buf += numerator / denominator;
        }
        if (lastVal == 0) { // if there is no cycle
            System.out.printf("1/%d = %s\n", denominator, buf);
        } else { //if there is a cycle
            int len = tracker.size() - tracker.indexOf(lastVal);

            // output formatting and printing
            StringBuilder output = new StringBuilder();
            output.append("1/");
            output.append(args[0]);
            output.append(" = ");
            output.append(buf.substring(0, 2 + tracker.indexOf(lastVal)));
            output.append('(');
            output.append(buf.substring(2 + tracker.indexOf(lastVal), 2 + tracker.size()));
            output.append("), cycle length ");
            output.append(Integer.toString(len));
            System.out.println(output.toString());
        }
    }   
}

