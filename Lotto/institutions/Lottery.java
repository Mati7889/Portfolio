package institutions;

import ticket.*;

import java.util.*;

// Handles a single lottery draw
public class Lottery {
    private final int number; // Draw number
    private long[] prizePools = new long[4]; // Prize pools
    private final Set<Integer> winningNumbers; // Winning numbers
    List<List<Integer>> winningTickets = new ArrayList<>(); // Lists of winning ticket numbers
    private int allBets; // Total number of bets checked

    // Constructor for a random draw
    protected Lottery(int number) {
        this.number = number;

        // Generate 6 random winning numbers
        int[] numbers = generateNumbers();
        this.winningNumbers = new TreeSet<>();
        for (int i : numbers) {
            this.winningNumbers.add(i);
        }

        // Initialize lists for winners (3+ correct numbers)
        for (int i = 0; i < 4; i++) {
            winningTickets.add(new ArrayList<>());
        }

        this.allBets = 0;
        findWinners(); // Check all active tickets
    }

    // Constructor with predefined winning numbers
    protected Lottery(int number, int[] numbers) {
        this.number = number;

        this.winningNumbers = new TreeSet<>();
        for (int i : numbers) {
            this.winningNumbers.add(i);
        }

        for (int i = 0; i < 4; i++) {
            winningTickets.add(new ArrayList<>());
        }

        this.allBets = 0;
        findWinners(); // Check all active tickets
    }

    // Generate 6 unique random numbers from 1 to 49
    public static int[] generateNumbers() {
        ArrayList<Integer> numbers = new ArrayList<>();
        for (int i = 1; i <= 49; i++) {
            numbers.add(i);
        }

        Collections.shuffle(numbers);
        int[] drawnNumbers = new int[6];
        for (int j = 0; j < 6; j++) {
            drawnNumbers[j] = numbers.get(j);
        }

        return drawnNumbers;
    }

    // Searches all lottery offices and their tickets for winners
    private void findWinners() {
        for (int numer : Headquarters.getHeadquarters().getOfficeNumber()) {
            CollectionOffice collectionOffice = Headquarters.getHeadquarters().getOffice(numer);
            List<Ticket> purchasedTickets = collectionOffice.getLotteryTickets(this.number);

            for (Ticket ticket : purchasedTickets) {
                checkTicket(ticket);
            }
        }
    }

    // Check a ticket and register it in the winning lists if it has 3+ matches
    private void checkTicket(Ticket ticket) {
        List<Bet> form = ticket.getCorrectBets();
        allBets += form.size();

        for (Bet bet : form) {
            int hits = correctNumbers(bet);

            if (hits > 2) { // Only 3, 4, 5, 6 hits
                winningTickets.get(6 - hits).add(ticket.getNumber());
            }
        }
    }

    // Count total bets checked
    public int numberOfBets() {
        return allBets;
    }

    // Count how many numbers in the bet match the winning numbers
    private int correctNumbers(Bet bet) {
        int hits = 0;
        for (int number : bet.getNumbers()) {
            if (winningNumbers.contains(number)) {
                hits++;
            }
        }
        return hits;
    }

    // Return the prize pools
    protected long[] getPrizePools() {
        return prizePools;
    }

    // Save prize amounts
    protected void savePrizeAmounts(long[] wonAmounts) {
        this.prizePools = wonAmounts;
    }

    // Return the lists of winning tickets
    protected List<List<Integer>> giveWinningTickets() {
        return winningTickets;
    }

    // Return the winning numbers
    public Set<Integer> getWinningNumbers() {
        return new TreeSet<>(winningNumbers);
    }

    @Override
    public String toString() {
            StringBuilder sb = new StringBuilder("Draw number ").append(number).append("\nWinning numbers:");
        for (int number : winningNumbers) {
            sb.append(String.format("%3d", number));
        }
        sb.append(" \n");
        return sb.toString();
    }
}
