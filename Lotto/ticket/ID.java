package ticket;

import exceptions.IllegalArgument;
import java.util.Objects;

// Represents a unique ticket identifier made from the ticket number, the collection number, and a check digit.
public class ID {
    // Random marker associated with the ticket
    private final int index;
    // Ticket number (must be > 0)
    private final int ticketNumber;
    // Collection point number (must be > 0)
    private final int numberOfOffice;
    // Check digit computed from ticket number, collection number, and marker
    private final int checkDigit;

    // Constructor initializes fields and computes a check digit
    protected ID(int ticketNumber, int numberOfOffice, int index) {
        if (ticketNumber < 1) {
            throw new IllegalArgument("ID: ticket number must be > 0");
        }
        if (numberOfOffice < 1) {
            throw new IllegalArgument("ID: collection number must be > 0");
        }
        if (index < 0) {
            throw new IllegalArgument("ID: index must be >= 0");
        }

        this.index = index;
        this.ticketNumber = ticketNumber;
        this.numberOfOffice = numberOfOffice;
        this.checkDigit = generateCheckDigit(); // Compute check digit
    }

    // Computes the sum of digits of a given number
    private int digitSum(int number) {
        int sum = 0;
        while (number > 0) {
            sum += number % 10;
            number /= 10;
        }
        return sum;
    }

    // Generates the check digit as a sum of digits of ticket, collection, and marker modulo 100
    private int generateCheckDigit() {
        int number = digitSum(ticketNumber) + digitSum(numberOfOffice) + digitSum(index);
        return number % 100;
    }

    // Returns the string representation of the identifier in the format: ticket-collection-marker-check
    @Override
    public String toString() {
        return String.format("%d-%d-%09d-%02d", ticketNumber, numberOfOffice, index, checkDigit);
    }

    // Equality check based on all fields
    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        ID that = (ID) o;
        return index == that.index &&
                ticketNumber == that.ticketNumber &&
                numberOfOffice == that.numberOfOffice &&
                checkDigit == that.checkDigit;
    }

    // Hash code based on all fields
    @Override
    public int hashCode() {
        return Objects.hash(index, ticketNumber, numberOfOffice, checkDigit);
    }
}
