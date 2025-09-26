package institutions;

// The state budget is a singleton; it collects taxes and provides subsidies
public class StateBudget {
    private static final StateBudget budget = new StateBudget();
    private long taxesCollected; // total collected taxes in grosz
    private long totalSubsidies; // total subsidies given in grosz

    private StateBudget() {
        this.taxesCollected = 0;
        this.totalSubsidies = 0;
    }

    // Adds a subsidy to the total
    public synchronized void giveSubsidy(long amount) {
        totalSubsidies += amount;
    }

    // Adds a tax to the total collected
    public synchronized void collectTax(long amount) {
        taxesCollected += amount;
    }

    // Returns the singleton instance
    public static StateBudget getBudget() {
        return budget;
    }

    // Returns total collected taxes
    public long getTaxSum() {
        return taxesCollected;
    }

    // Returns total given subsidies
    public long getSubsidySum() {
        return totalSubsidies;
    }

    // Returns a string summarizing taxes and subsidies
    public String displayBudgetInfo() {
        return "Total tax collected: " + taxesCollected / 100 + " zł " + taxesCollected % 100 + "gr\nTotal subsidies given: "
                + totalSubsidies / 100 + " zł " + totalSubsidies % 100 + "gr\n";
    }
}
