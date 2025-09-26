package player;

import institutions.Headquarters;
import exceptions.IllegalArgument;

public class Minimalist extends Player {
    private final int favOffice;

    public Minimalist(String name, String surname, int pesel, long funds, int favOffice) {
        super(name, surname, pesel, funds);
        if (favOffice < 1 || Headquarters.getHeadquarters().getOffice(favOffice) == null) {
            throw new IllegalArgument("Minimalist: wrong office number (" + favOffice + ").");
        }

        this.favOffice = favOffice;
    }

    @Override
    public void buyTicket() {
        buyTicket(1, 1);
    }

    @Override
    protected int chooseCollectionOffice() {
        return favOffice;
    }
}
