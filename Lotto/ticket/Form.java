package ticket;

import institutions.Lottery;
import exceptions.IllegalArgument;

import java.util.*;

/**
 * Represents a lottery form (blank) used to create tickets.
 * Contains multiple bets and the number of draws.
 */
public class Form {
    private final int[][] originalForm; // Original numbers from the form
    private final List<Bet> correctBets; // List of valid bets
    private final int numberOfDraws; // Number of draws for this blank

    // Constructor with given bets and number of draws
    public Form(int[][] originalForm, int numberOfDraws) {
        if (numberOfDraws < 0) {
            throw new IllegalArgument("Form: number of draws must be > 0");
        }
        this.numberOfDraws = (numberOfDraws == 0) ? 1 : numberOfDraws;
        this.originalForm = originalForm;
        this.correctBets = setValidBets(originalForm);
    }

    // Constructor with random bets
    public Form(int numberOfBets, int numberOfDraws) {
        if (numberOfBets < 1 || numberOfDraws < 0) {
            throw new IllegalArgument("Form: number of bets and draws must be > 0");
        }
        this.numberOfDraws = (numberOfDraws == 0) ? 1 : numberOfDraws;
        this.originalForm = setRandom(numberOfBets);
        this.correctBets = setValidBets(originalForm);
    }

    // Constructor when multiple draw numbers are given, picks the largest (max 10)
    public Form(int[][] numbers, int[] numberOfDraws) {
        int draws = 1;
        if (numberOfDraws != null && numberOfDraws.length > 0) {
            int max = numberOfDraws[0];
            for (int val : numberOfDraws) {
                if (val > max) max = val;
            }
            draws = Math.min(max, 10);
        }
        this.numberOfDraws = draws;
        this.originalForm = numbers;
        this.correctBets = setValidBets(numbers);
    }

    // Generate random bets
    private int[][] setRandom(int numberOfBet) {
        int[][] form = new int[numberOfBet][];
        for (int i = 0; i < numberOfBet; i++) {
            form[i] = Lottery.generateNumbers();
        }
        return form;
    }

    // Returns number of valid bets
    public int numberOfCorrectBets() {
        return correctBets.size();
    }

    // Returns number of draws
    public int howManyDraws() {
        return numberOfDraws;
    }

    // Returns a list of valid bets (read-only)
    public List<Bet> getCorrectBets() {
        return Collections.unmodifiableList(correctBets);
    }

    // Filter and set valid bets
    private ArrayList<Bet> setValidBets(int[][] numbers) {
        if (numbers == null || numbers.length == 0) return new ArrayList<>();
        ArrayList<Bet> correctForm = new ArrayList<>();

        for (int[] i : numbers) {
            if (i.length == 6) {
                Set<Integer> set = new HashSet<>();
                boolean correct = true;
                for (int j : i) {
                    if (!set.add(j) || j < 1 || j > 49) correct = false;
                }
                if (correct && set.size() == 6) correctForm.add(new Bet(i));
            }
        }
        return correctForm;
    }

    // Returns original bet numbers for display
    public int[] getForm(int bet) {
        return Arrays.copyOf(originalForm[bet], originalForm[bet].length);
    }
}
