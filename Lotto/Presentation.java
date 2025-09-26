import institutions.*;
import player.*;
import player.Random;

import java.util.*;

/**
 * The simulation program allows changing arguments when creating players,
 * the number of draws, the state of the central office,
 * printing the budget contribution and winnings of those who earned above a certain amount.
 * Prints the winning 6 numbers, prize pool (overwrites to 1 if below 2,000,000),
 * individual winnings, and the number of winning tickets for each rank.
 */

public class Presentation {
    // Prints the players who have the most money after the draws
    public static void printTopWinner(List<Player> players) {
        System.out.println("Player that won the most money after the lottery draws:");
        long max = 0;
        Player winner = null;

        for (Player player : players) {
            if (player.getBalance() > max) {
                winner = player;
                max = player.getBalance();
            }
        }
        assert winner != null;
        System.out.println(winner.getPlayerInfo());
    }

    public static void main(String[] args) {
        java.util.Random random = new java.util.Random();
        Headquarters headquarters = Headquarters.getHeadquarters();
        // Set to 0 to see the real revenue
        headquarters.setBalance(0);

        // Creating 10 lottery offices (in this test I assume the order, but it's possible to assign any number)
        for (int i = 1; i <= 10; i++) {
            new CollectionOffice(i);
        }

        // Creating players (200 of each type)
        List<Player> players = new ArrayList<>();
        int numberOfPlayers = 200;

        // Lists of first and last names
        String[] names = {"Jan", "Genowefa", "Piotr", "Marcin", "Oskar", "Wiktor", "Hanna", "Maja", "Mateusz", "Katarzyna"};
        String[] surnames = {"Kowal", "Siano", "Wojcieszek", "Grad", "Guszyn", "Rowek", "Kołodziej", "Geraltek", "Marczyk"};

        // Minimalist player type
        for (int i = 0; i < numberOfPlayers; i++) {
            String name = names[random.nextInt(names.length)];
            String surname = surnames[random.nextInt(surnames.length)];
            int pesel = random.nextInt(1000000000);
            players.add(new Minimalist(name, surname, pesel, 100_000_00, random.nextInt(10) + 1));
        }

        // Random player type
        for (int i = 0; i < numberOfPlayers; i++) {
            String name = names[random.nextInt(names.length)];
            String surname = surnames[random.nextInt(surnames.length)];
            int pesel = random.nextInt(1000000000);
            players.add(new Random(name, surname, pesel));
        }

        // Fixed ticket player type
        for (int i = 0; i < numberOfPlayers; i++) {
            String name = names[random.nextInt(names.length)];
            String surname = surnames[random.nextInt(surnames.length)];
            int pesel = random.nextInt(1000000000);
            int[][] numbers = new int[8][];

            for (int j = 0; j < numbers.length; j++) {
                numbers[j] = Lottery.generateNumbers();
            }
            LinkedList<Integer> favouriteOffices = new LinkedList<>(List.of(4, 2, 3, 9, 5));
            players.add(new FixedForm(name, surname, pesel, 100_000_00, numbers,
                    favouriteOffices, random.nextInt(10) + 1));
        }

        // Fixed number player type
        for (int i = 0; i < numberOfPlayers; i++) {
            String name = names[random.nextInt(names.length)];
            String surname = surnames[random.nextInt(surnames.length)];
            int pesel = random.nextInt(1000000000);
            int[] numbers = Lottery.generateNumbers();

            LinkedList<Integer> favouriteOffices = new LinkedList<>(List.of(1, 8, 6, 10, 7));
            players.add(new FixedNumber(name, surname, pesel, 100_000_00, numbers,
                    favouriteOffices));
        }


        // Simulate 20 lottery draws
        for (int l = 1; l <= 20; l++) {
            for (Player g : players) {
                g.buyTicket();
            }

            headquarters.lottery();

            for (Player g : players) {
                g.checkTickets();
            }
        }

        // Print draw results
        for (int l = 1; l <= headquarters.getLotteriesCount(); l++) {
            System.out.println(headquarters.displayResults(l));
        }

        // Print government budget contributions and central office balance
        System.out.println("Contribution to the state budget: \n" + StateBudget.getBudget().displayBudgetInfo());
        System.out.println(headquarters.displayFunds());

        // Method to display players who earned the most (also displays person's tickets)

        //printTopWinner(players);
    }
}
