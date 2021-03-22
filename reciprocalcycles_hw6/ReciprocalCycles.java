import java.util.LinkedList;
import java.math.MathContext;
import java.math.RoundingMode;

public class ReciprocalCycles {

    public static void main(String[] args) {
        // check denominator is valid integer
        int denominator = -1;
        String buf = "0.";
        
        try {
            denominator = Integer.parseInt(args[0]);
        } catch (Exception e) {
            System.err.printf("Error: Denominator must be an integer in [1, 2000]. Received '%s'.\n", args[0]);
        }
        if (denominator < 1 || denominator > 2000) {
            System.err.printf("Error: Denominator must be an integer in [1, 2000]. Received '%s'.\n", args[0]);
        }
        
        LinkedList<Integer> tracker = new LinkedList<>();
        int numerator = 1;
        int lastVal = numerator % denominator;
        while (tracker.indexOf(lastVal) == -1 && lastVal != 0) {
            tracker.addLast(lastVal);
            numerator = lastVal * 10;
            lastVal = numerator % denominator;
            buf += numerator / denominator;
        }
        if (lastVal == 0) {
            System.out.printf("1/%d = %s\n", denominator, buf);
        } else {
            int len = tracker.size() - tracker.indexOf(lastVal);

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

