package player;

import institutions.Headquarters;
import ticket.Ticket;
import exceptions.IllegalArgument;

import java.util.List;
import java.util.Random;

public class FixedForm extends Player {
    private static final Random random = new Random();
    private final int howOftenBuys;
    private final int[][] favouriteNumbers;
    private final List<Integer> favouriteOffices;
    private int officesIterator;

    // Constructor with user-defined parameters
    public FixedForm(String name, String surname, int pesel, long funds, int[][] numbers, List<Integer> favouriteOffices, int howOftenBuys) {
        super(name, surname, pesel, funds);

        if (favouriteOffices == null || favouriteOffices.isEmpty()) {
            throw new IllegalArgument("FixedForm player: no favourite offices to choose from.");
        }

        for (Integer office : favouriteOffices) {
            if (office < 1 || Headquarters.getHeadquarters().getOffice(office) == null) {
                throw new IllegalArgument("Player: office number " + office + " does not exist.");
            }
        }

        if (numbers == null || numbers.length == 0) {
            throw new IllegalArgument("FixedForm player: no numbers to choose from.");
        }

        if (howOftenBuys < 1) {
            throw new IllegalArgument("FixedForm player: how often to buy a ticket must be at least 1.");
        }

        this.officesIterator = 0;
        this.favouriteNumbers = numbers;
        this.howOftenBuys = howOftenBuys;
        this.favouriteOffices = favouriteOffices;

        buyTicket(howOftenBuys, favouriteNumbers);
    }

    // Chooses a collection based on the favorite collections array and a general iterator
    @Override
    protected int chooseCollectionOffice() {
        if (officesIterator >= favouriteOffices.size()) {
            officesIterator = 0;
        }

        return favouriteOffices.get(officesIterator++);
    }

    // Buys a ticket only if the previous one has expired (howOftenBuys() = how often the player buys)
    @Override
    public void buyTicket() {
        if (this.tickets.isEmpty()) {
            buyTicket(howOftenBuys, favouriteNumbers);
            return;
        }

        Ticket last = this.tickets.get(tickets.size() - 1);
        int previousDraw = last.getDrawNumbers().getLast();
        if (previousDraw == Headquarters.getHeadquarters().getLotteriesCount() - howOftenBuys) {
            buyTicket(howOftenBuys, favouriteNumbers);
        }
    }
}
