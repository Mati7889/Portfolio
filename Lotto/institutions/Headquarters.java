package institutions;

import player.Player;

import java.util.*;

public class Headquarters {
    private long funds; // Headquarters funds in grosz
    private long jackpot; // jackpot amount in grosz
    private final List<Lottery> lotteries = new ArrayList<>(); // list of conducted draws
    private final Map<Integer, CollectionOffice> collectionOffices = new HashMap<>(); // map of branches by number
    private static int lastTicketNumber; // last ticket number

    private Headquarters() {
        jackpot = 2_000_000_00; // initial jackpot
        lastTicketNumber = 0;
    }

    // Static holder for a singleton instance
    private static class HeadquartersSingleton {
        private static final Headquarters INSTANCE = new Headquarters();
    }

    // Public access method for the singleton
    public static Headquarters getHeadquarters() {
        return HeadquartersSingleton.INSTANCE;
    }

    // Displays the Headquarters funds
    public String displayFunds() {
        return "Headquarters funds: " + funds / 100 + " zł " + funds % 100 + " gr\n";
    }

    // Recieves a subsidy from the state
    protected synchronized void receiveSubsidy(long amount) {
        StateBudget.getBudget().giveSubsidy(amount);
        funds += amount;
    }

    // Pays tax
    protected synchronized void payTax(long amount) {
        StateBudget.getBudget().collectTax(amount);
        this.funds -= amount;
    }

    // Adds income to Headquarters funds
    protected synchronized void collectIncome(long amount) {
        funds += amount;
    }

    // Conducts a draw, calculates winnings, and adds it to the draw list
    public synchronized void lottery() {
        Lottery lottery = new Lottery(lotteries.size() + 1);
        lotteries.add(lottery);
        lottery.savePrizeAmounts(CalcRewards(lottery));
    }

    // Conducts a fake draw with preset numbers (for testing)
    public synchronized void fakeLottery(int[] numbers) {
        Lottery lottery = new Lottery(lotteries.size() + 1, numbers);
        lotteries.add(lottery);
        lottery.savePrizeAmounts(CalcRewards(lottery));
    }

    // Allows user to set the account balance
    public synchronized void setBalance(long amount) {
        this.funds = amount;
    }

    // Increments the last ticket counter
    protected synchronized void incrementTicketCounter() {
        lastTicketNumber++;
    }

    public int getLastTicketNumber() {
        return lastTicketNumber;
    }

    // Adds a lottery office to the central system
    protected synchronized void addCollectionOffice(CollectionOffice collectionOffice) {
        collectionOffices.put(collectionOffice.giveNumber(), collectionOffice);
    }

    // Calculates the prize pools according to rules; does not reserve funds yet
    public long[] CalcRewards(Lottery lottery) {
        List<List<Integer>> tickets = lottery.giveWinningTickets();

        // Sum of all bets participating in the draw
        long pot = 240L * lottery.numberOfBets();
        pot = (long) (pot * 0.51);

        // Array for prize pools (index = prize tier - 1)
        long[] rewards = new long[4];
        long Ipot = (long) (pot * 0.44);

        // Calculate prize amounts according to prizing rules
        rewards[1] = (long) (pot * 0.08);
        rewards[3] = tickets.get(3).size() * 2400L;
        long tempIII = pot - Ipot - rewards[1] - rewards[3];

        // Guaranteed for 3rd tier
        rewards[2] = Math.max(tempIII, tickets.get(2).size() * 3600L);

        // Jackpot mechanism
        if (lotteries.get(0) == lottery) {
            rewards[0] = jackpot;
        } else if (tickets.get(0).isEmpty()) {
            jackpot += Ipot;
            rewards[0] = jackpot;
        } else {
            // Guaranteed prize handling
            long sumI = Ipot + jackpot;
            rewards[0] = Math.max(sumI, 2_000_000_00L);
            jackpot = 2_000_000_00L;
        }

        return rewards;
    }

    // Allows setting bet price
    public static long getBetPrice() {
        return 3_00;
    }

    public int getLotteriesCount() {
        return lotteries.size();
    }

    protected List<List<Integer>> giveWinningTickets(int lottery) {
        return lotteries.get(lottery - 1).giveWinningTickets();
    }

    protected void withdrawReward(long amount, Player player) {
        if (funds < amount) {
            receiveSubsidy(amount - funds);
        }
        funds -= amount;
        player.addFunds(amount);
    }

    public Set<Integer> getWinningNumbers(int lottery) {
        return Collections.unmodifiableSet(lotteries.get(lottery - 1).getWinningNumbers());
    }

    // Public information about the first prize pool
    public String getFirstPrizePool(int lottery) {
        long amount = lotteries.get(lottery - 1).getPrizePools()[0];
        return "Real first pot prize pool: " + amount / 100 + " zł " + amount % 100 + " gr\n";
    }

    public long getFunds() {
        return funds;
    }

    // Returns the amount per winning ticket for each tier
    public long[] prizeAmounts(int lottery) {
        long[] pot = lotteries.get(lottery - 1).getPrizePools();
        long[] amounts = Arrays.copyOf(pot, pot.length);
        List<List<Integer>> winning = giveWinningTickets(lottery);

        for (int i = 0; i < winning.size(); i++) {
            if (!winning.get(i).isEmpty()) {
                amounts[i] = amounts[i] / winning.get(i).size();
            }
        }

        return amounts;
    }

    public Lottery lottery(int numer) {
        return lotteries.get(numer - 1);
    }

    public List<Integer> getOfficeNumber() {
        return List.copyOf(collectionOffices.keySet());
    }

    public CollectionOffice getOffice(int number) {
        return collectionOffices.get(number);
    }

    // Prints bets, pools, and amounts including guaranteed amounts
    public String displayResults(int lottery) {
        StringBuilder sb = new StringBuilder(lotteries.get(lottery - 1).toString());
        sb.append("------------------\n");
        sb.append("Total winning amounts: \n");
        long[] amounts = prizeAmounts(lottery);
        List<List<Integer>> numberOfBets = giveWinningTickets(lottery);
        String[] degreeNames = {"First Prize", "Second Prize", "Third Prize", "Fourth Prize"};

        for (int i = 0; i < amounts.length; i++) {
            long prizeAmount = amounts[i];
            if (!numberOfBets.get(i).isEmpty()) {
                sb.append(String.format("%-12s : %5d zł %02d gr\n",
                        degreeNames[i],
                        prizeAmount / 100,
                        prizeAmount % 100));
            }
        }
        sb.append("------------------\n");
        sb.append("Number of winning bets: \n");

        for (int i = 0; i < numberOfBets.size(); i++) {
            sb.append(String.format("%-12s : %5d\n", degreeNames[i], numberOfBets.get(i).size()));
        }
        sb.append("------------------\n");
        long[] prizePools = lotteries.get(lottery - 1).getPrizePools();
        sb.append("Prize pools: \n");
        for (int i = 0; i < prizePools.length; i++) {
            sb.append(String.format("%-12s : %5d zł %02d gr\n",
                    degreeNames[i],
                    prizePools[i] / 100,
                    prizePools[i] % 100));
        }
        return sb.toString();
    }
}
