import java.io.*;

public class zeros {

    static class InputReader {
        private InputStream stream;
        private byte[] buf = new byte[8192];
        private int currentChar, numChars;

        public InputReader(InputStream stream) {
            this.stream = stream;
        }

        public int nextInt() throws IOException {
            int c = '0', value = 0;
            while (true) {
                if (currentChar >= numChars) {
                    currentChar = 0;
                    numChars = stream.read(buf);
                }
                if ((c = buf[currentChar++]) < 33) {
                    break;
                }
                value = (value << 3) + (value << 1) + (c & 15);
            }
            return value;
        }
    }

    public static void main(String[] args) throws Exception {
        // BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(System.out));
        InputReader ir = new InputReader(System.in);

        for (int lines = ir.nextInt(); lines > 0; lines--) {
            int n = ir.nextInt();
            int zeroes = 0;
            for (int i = 5; i <= n; i *= 5) {
                zeroes += n / i;
            }
            writer.write(Integer.toString(zeroes));
            writer.newLine();
        }
        writer.flush();
    }
}
