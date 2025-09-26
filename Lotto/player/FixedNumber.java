package player;
import java.util.*;

// Subclass for fixed-blank tickets with a constant number of draws and a single-bet blank
public class FixedNumber extends FixedForm {

    public FixedNumber(String name, String surname, int pesel, long funds, int[] numbers, List<Integer> favouriteOffices) {
        super(name, surname, pesel, funds, new int[][]{numbers}, favouriteOffices, 10);
    }
}

