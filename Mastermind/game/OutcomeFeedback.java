package game;

import java.util.Objects;

// Helper class holding feedback for the computer player
// Can easily be compared to other feedback to deduce the best next move
public class OutcomeFeedback {
    public final int black;
    public final int white;

    public OutcomeFeedback(int black, int white) {
        this.black = black;
        this.white = white;
    }

    public int[] give() {
        return new int[]{black, white};
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof OutcomeFeedback second)) return false;
        return black == second.black && white == second.white;
    }

    @Override
    public int hashCode() {
        return Objects.hash(black, white);
    }

}
