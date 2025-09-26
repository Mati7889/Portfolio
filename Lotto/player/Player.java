package player;

import institutions.Headquarters;
import institutions.CollectionOffice;
import ticket.*;

import java.util.*;

/**
 * Abstract class Gracz implements common methods for different player types
 * Each player keeps their tickets and funds, in addition to personal information
 */
public abstract class Player {
    private final String name;
    private final String surname;
    private final int pesel;
    private long funds;
    protected List<Ticket> tickets = new ArrayList<>();

    public Player(String name, String surname, int pesel, long funds) {
        this.name = name;
        this.surname = surname;
        this.pesel = pesel;
        this.funds = funds;
    }

    // Each player has their own way of choosing a collection depending on preferences
    protected abstract int chooseCollectionOffice();

    // Method for player types with different ticket purchase strategies
    public abstract void buyTicket();

    /*
     * Before buying a ticket, the player fills a blank
     * Public method and constructor allow independent blank filling
     */
    public Form fillForm(int[][] numbers, int numberOfDraws) {
        return new Form(numbers, numberOfDraws);
    }

    /* Method allows buying a ticket filled manually; pass in the array of chosen numbers
     * Based on this, the player fills the blank and buys the ticket by choosing a collection
     * Adds the ticket to the player's list only if the transaction succeeds
     */
    public synchronized void buyTicket(int numberOfDraws, int[][] numbers) {
        CollectionOffice collectionOffice = Headquarters.getHeadquarters().getOffice(chooseCollectionOffice());

        Form form = fillForm(numbers, numberOfDraws);
        Ticket ticket = collectionOffice.giveTicket(form, this);

        if (ticket != null) {
            tickets.add(ticket);
            funds -= ticket.getPrice();
        }
    }

    /* Overloaded method for a random ticket; only pass the number of draws and bets
     * The collection generates the ticket using the appropriate giveTicket() method
     */
    public synchronized void buyTicket(int numberOfBets, int numberOfDraws) {
        if (numberOfBets > 8) {
            throw new IllegalArgumentException("Bet limit exceeded (8)");
        }

        CollectionOffice collectionOffice = Headquarters.getHeadquarters().getOffice(chooseCollectionOffice());
        Ticket ticket = collectionOffice.giveTicket(numberOfBets, numberOfDraws, this);

        if (ticket != null) { // Only if the transaction is successful
            tickets.add(ticket);
            funds -= ticket.getPrice();
        }
    }

    // The player can check if the draws of their tickets have passed and claim them
    public void checkTickets() {
        List<Ticket> toCheck = new ArrayList<>(tickets);
        for (Ticket ticket : toCheck) {
            List<Integer> drawNumbers = ticket.getDrawNumbers();

            if (drawNumbers.getLast() == Headquarters.getHeadquarters().getLotteriesCount()) { // if draws have ended
                redeemTicket(ticket);
            }
        }
    }

    public void addFunds(long kwota) {
        this.funds += kwota;
    }

    // Winnings can be claimed before the draws finish; removes the ticket to prevent fraud
    public void redeemTicket(Ticket ticket) {
        if (tickets.contains(ticket)) {
            CollectionOffice collectionOffice = Headquarters.getHeadquarters().getOffice(ticket.getOffice());
            collectionOffice.sprawdźKupon(ticket, this);
            tickets.remove(ticket);
        }
    }

    // Less efficient method for manually claiming tickets before the draws finish
    public void redeemTicket(int numer) {
        Iterator<Ticket> iterator = tickets.iterator();
        while (iterator.hasNext()) {
            Ticket ticket = iterator.next();
            if (ticket.getNumber() == numer) {
                CollectionOffice collectionOffice = Headquarters.getHeadquarters().getOffice(ticket.getOffice());
                collectionOffice.sprawdźKupon(ticket, this);
                iterator.remove();
            }
        }
    }

    public long getBalance() {
        return funds;
    }

    // Prints personal information, ticket identifiers, and funds
    public String getPlayerInfo() {
        StringBuilder sb = new StringBuilder("Nazwisko: ");
        sb.append(surname).append("\nImię: ").append(name);
        sb.append("\nPESEL: ").append(pesel);
        sb.append("\nPosiadane środki: ").append(funds / 100).append(" zł ").append(funds % 100).append(" gr\n");

        if (!tickets.isEmpty()) {
            sb.append("Identyfikatory posiadanych kuponów: \n");

            for (Ticket ticket : tickets) {
                sb.append(ticket.printId()).append("\n");
            }
        } else {
            sb.append("Gracz nie posiada kuponów!\n");
        }

        return sb.toString();
    }
}
