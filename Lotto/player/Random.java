package player;

import institutions.Headquarters;
import exceptions.IllegalArgument;

import java.util.List;

// Player picking everything at random
public class Random extends Player {
    private static final java.util.Random random = new java.util.Random();

    public Random(String name, String surname, int pesel) {
        super(name, surname, pesel, random.nextInt(100000000));
    }

    @Override
    protected int chooseCollectionOffice() {
        return randomOffice();
    }

    private int randomOffice() {
        List<Integer> offices = Headquarters.getHeadquarters().getOfficeNumber();
        if (offices.isEmpty()) throw new IllegalArgument("No collection offices to choose from.");
        int index = random.nextInt(offices.size());
        return offices.get(index);
    }

    @Override
    public void buyTicket() {
        int random = Random.random.nextInt(100) + 1;

        for (int i = 0; i < random; i++) {
            buyTicket(Random.random.nextInt(8) + 1, Random.random.nextInt(10) + 1);
        }
    }
}
