package ticket;

import institutions.*;
import exceptions.IllegalArgument;

import java.util.*;

/**
 * Represents a lottery ticket with a blank, draw numbers, collection number and price.
 */
public class Ticket implements Comparable<Ticket> {
    private final int office; // Lottery office number
    private final int number; // Ticket number
    private final ID ID; // Unique identifier for the ticket
    private final Form form; // Associated blank with bets
    private final List<Integer> numberOfDraws; // Draw numbers for this ticket

    // Constructor for a new ticket
    public Ticket(int office, int number, Form form) {
        if (office < 1 || !Headquarters.getHeadquarters().getOfficeNumber().contains(office)) {
            throw new IllegalArgument("Ticket: number exceeds limit (" + office + ").");
        }
        if (form == null || form.numberOfCorrectBets() == 0) {
            throw new IllegalArgument("Ticket: incorrect form.");
        }

        this.office = office;
        this.number = number;
        this.form = form;
        this.numberOfDraws = setNumberOfDraws(form.howManyDraws());

        // Generate random marker for the identifier
        Random random = new Random();
        int index = random.nextInt(0, 1_000_000_000);
        this.ID = new ID(number, office, index);
    }

    // Set draw numbers based on the number of draws
    private LinkedList<Integer> setNumberOfDraws(int numberOfDraws) {
        if (numberOfDraws > 10) {
            throw new IllegalArgument("Exceeded draw limit(10)");
        }

        LinkedList<Integer> numbers = new LinkedList<>();
        int firstDraw = Headquarters.getHeadquarters().getLotteriesCount() + 1; // First available draw

        for (int i = 0; i < numberOfDraws; i++) {
            numbers.add(firstDraw + i); // Add consecutive draw numbers
        }

        return numbers;
    }

    // Returns the ticket's identifier as a string
    public String printId() {
        return this.ID.toString();
    }

    // Returns a copy of the draw numbers
    public List<Integer> getDrawNumbers() {
        return new ArrayList<>(numberOfDraws);
    }

    // Returns the total price of the ticket
    public long getPrice() {
        return Headquarters.getBetPrice() * form.numberOfCorrectBets() * form.howManyDraws();
    }

    // Returns the tax (20% of the price)
    public long getTaxAmount() {
        return getPrice() / 5;
    }

    public int getNumber() {
        return number;
    }

    // Returns a copy of valid bets on this ticket
    public List<Bet> getCorrectBets() {
        return List.copyOf(form.getCorrectBets());
    }

    public int getOffice() {
        return office;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("TICKET NUMBER");
        sb.append(ID.toString()).append("\n");

        List<Bet> correctForm = form.getCorrectBets();

        for (int i = 0; i < correctForm.size(); i++) {
            sb.append(i + 1).append(":");
            sb.append(correctForm.get(i).toString());
        }

        sb.append("NUMBER OF DRAWS: ").append(form.howManyDraws()).append("\n");
        sb.append("DRAWS NUMBERS:\n");

        // Show draw numbers
        for (int i = 0; i < form.howManyDraws(); i++) {
            sb.append(" ").append(i + 1 + Headquarters.getHeadquarters().getLotteriesCount());
        }

        sb.append("\nPRICE: ");
        sb.append(getPrice() / 100).append(" zł ");
        sb.append(getPrice() % 100).append(" gr\n");

        return sb.toString();
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(number);
    }

    @Override
    public int compareTo(Ticket other) {
        return Integer.compare(this.number, other.number);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Ticket ticket)) return false;
        return ID.equals(ticket.ID);
    }
}
