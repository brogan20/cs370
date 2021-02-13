public class euler3 {
    public static void main(String[] args) {
        long num = 600851475143L; // need L or else it thinks its an int literal
        int i = 2;
        int maxFactor = (int)Math.sqrt(num); // floor returns a double for some reason
    

        while (i <= maxFactor) {
            if (num % i == 0) {
                num = num / i;
                maxFactor = (int)Math.sqrt(num);
                continue;
            }
            i++;
        }

        System.out.println(i);
        System.out.println(maxFactor);
    }
}
// ya we're one step off
//so the end condition or whatever i guess 
// close enough