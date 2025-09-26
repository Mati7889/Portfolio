package game;

public class Feedback {

    // Feedback for the computer returns the number of hits and wrong positions
    // Based on this, the computer reduces the number of possible codes
    public static OutcomeFeedback checkGuess(Code secret, Code guess) {
        int white = 0;
        int black = 0;
        int length = MasterMind.codeLenght;
        int[] secretArr = secret.giveCode();
        int[] guessArr = guess.giveCode();
        boolean[] usedSecret = new boolean[length];
        boolean[] guesses = new boolean[length];


        // Black -> hit, correct placement
        for (int i = 0; i < length; i++) {
            if (secretArr[i] == guessArr[i]) {
                black++;
                usedSecret[i] = true;
                guesses[i] = true;
            }
        }

        // White -> incorrect placement
        for (int i = 0; i < length; i++) {
            if (!guesses[i]) {
                for (int j = 0; j < length; j++) {
                    if (!usedSecret[j] && guessArr[i] == secretArr[j]) {
                        white++;
                        guesses[i] = true;
                        usedSecret[j] = true;
                        break;
                    }
                }
            }
        }
        return new OutcomeFeedback(black, white);
    }
}
