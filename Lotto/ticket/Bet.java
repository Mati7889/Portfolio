package ticket;

import java.util.Arrays;

//Helper class that stores a properly filled ticket bet — always contains 6 numbers within the valid range.
public class Bet {
    private final int[] numbers;

    public Bet(int[] numbers) {
        if (numbers.length != 6) {
            throw new IllegalArgumentException("Invalid number of bet numbers: " + numbers.length + ". Expected 6.");
        }
        this.numbers = numbers;
    }

    public int[] getNumbers() {
        return Arrays.copyOf(numbers, numbers.length);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        for (int j : numbers) {
            sb.append(String.format(" %2d", j));
        }
        sb.append("\n");
        return sb.toString();
    }
}
