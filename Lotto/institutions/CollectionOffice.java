package institutions;

import player.*;
import ticket.*;
import exceptions.IllegalArgument;

import java.util.*;

// Lottery office: sells tickets and communicates with the central system
public class CollectionOffice {
    Headquarters headquarters = Headquarters.getHeadquarters(); // Reference to the central system
    private final int number; // Office number
    private final Map<Integer, Ticket> activeTickets = new HashMap<>(); // Active tickets
    private final Map<Integer, Ticket> inactiveTickets = new HashMap<>(); // Inactive tickets

    // Constructor: registers the office in the central system
    public CollectionOffice(int number) {
        if (headquarters.getOffice(number) != null) {
            throw new IllegalArgument("Office with this number already exists: " + number + ".");
        }

        if (number < 1) {
            throw new IllegalArgument("Office: wrong office number (" + number + ").");
        }

        this.number = number;
        headquarters.addCollectionOffice(this);
    }

    // Checks a ticket for a player: validates, deactivates, pays winnings if any
    public void sprawdźKupon(Ticket ticket, Player player) {
        if (!inactiveTickets.containsKey(ticket.getNumber()) &&
                !activeTickets.containsKey(ticket.getNumber())) {
            throw new IllegalArgument("Ticket bought in another office: " + ticket.printId() + ".");
        }

        // Retrieve the real ticket from active or inactive maps
        Ticket real = activeTickets.getOrDefault(ticket.getNumber(),
                inactiveTickets.get(ticket.getNumber()));
        if (!real.equals(ticket)) {
            throw new IllegalArgument("Forged ticket!");
        }

        // Deactivate active ticket
        if (activeTickets.containsKey(ticket.getNumber())) {
            deactivateTicket(ticket);
        }

        // Calculate winnings and pay player
        long winnings = checkWinnings(ticket);
        if (winnings > 0) {
            headquarters.withdrawReward(winnings, player);
        }
    }

    // Calculates total winnings for a ticket
    private long checkWinnings(Ticket ticket) {
        long wonAmount = 0;
        long highestAmountWon = 0;
        List<Integer> draws = ticket.getDrawNumbers();

        int i = draws.get(0);
        while (i <= draws.get(draws.size() - 1) && i <= headquarters.getLotteriesCount()) {
            List<List<Integer>> winnings = headquarters.giveWinningTickets(i);
            long[] prizeAmounts = headquarters.prizeAmounts(i);

            for (int j = 0; j < winnings.size(); j++) {
                if (winnings.get(j).contains(ticket.getNumber())) {
                    int frequency = Collections.frequency(winnings.get(j), ticket.getNumber());
                    long amount = prizeAmounts[j] * frequency;
                    highestAmountWon = Math.max(amount, highestAmountWon);
                    wonAmount += amount;
                }
            }
            i++;
        }

        // Deduct 10% tax if the largest winning exceeds a threshold
        if (highestAmountWon >= 228000) {
            long tax = (long) (highestAmountWon * 0.1);
            wonAmount -= tax;
            headquarters.payTax(tax);
        }

        return wonAmount;
    }

    // Issues a ticket based on a player's blank
    public Ticket giveTicket(Form form, Player player) {
        if (!canAfford(player, form.numberOfCorrectBets(), form.howManyDraws())
                || form.numberOfCorrectBets() == 0) {
            return null;
        }

        Ticket ticket = new Ticket(this.number, headquarters.getLastTicketNumber() + 1, form);
        newTicketHandling(ticket);

        return ticket;
    }

    // Issues a ticket with a random blank for the player
    public Ticket giveTicket(int numberOfBets, int numberOfDraws, Player player) {
        Form form = new Form(numberOfBets, numberOfDraws);

        if (!canAfford(player, numberOfBets, form.howManyDraws())) {
            return null;
        }

        Ticket ticket = new Ticket(this.number, headquarters.getLastTicketNumber() + 1, form);
        newTicketHandling(ticket);

        return ticket;
    }

    // Adds a new ticket to active tickets and updates the central system
    private void newTicketHandling(Ticket ticket) {
        activeTickets.put(ticket.getNumber(), ticket);
        headquarters.incrementTicketCounter();
        headquarters.collectIncome(ticket.getPrice());
        headquarters.payTax(ticket.getTaxAmount());
    }

    // Deactivates a ticket after draw or payout
    protected void deactivateTicket(Ticket ticket) {
        activeTickets.remove(ticket.getNumber());
        inactiveTickets.put(ticket.getNumber(), ticket);
    }

    // Checks if a player has enough money to buy the ticket
    private boolean canAfford(Player player, int bets, int draws) {
        return player.getBalance() >= Headquarters.getBetPrice() * bets * draws;
    }

    // Returns only active tickets participating in the given draw
    public List<Ticket> getLotteryTickets(int drawNumber) {
        List<Ticket> kupony = new ArrayList<>();
        for (Ticket ticket : activeTickets.values()) {
            if (ticket.getDrawNumbers().contains(drawNumber)) {
                kupony.add(ticket);
            }
        }
        return kupony;
    }

    public int giveNumber() {
        return number;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        CollectionOffice collectionOffice = (CollectionOffice) o;
        return number == collectionOffice.number;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(number);
    }
}
