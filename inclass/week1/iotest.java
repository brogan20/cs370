import java.io.*; 

public class iotest {

    public static void main(String[] args) throws IOException {
        BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
        String[] line1 = in.readLine().split(" ");
        int lines = Integer.parseUnsignedInt(line1[0]);
        int divisor = Integer.parseUnsignedInt(line1[1]);

        int total = 0;
        for(; lines > 0; lines--) {
            if (Integer.parseUnsignedInt(in.readLine()) % divisor == 0) {
                total++;
            }
        }
        System.out.println(total);
    }
}