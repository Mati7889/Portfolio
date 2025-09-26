package game;

// Additional class for printing the current state of the game
public class PlayerFeedback {
    public static OutcomeFeedback giveFeedback(Code secret, Code shoot) {
        int black = 0;
        int white = 0;
        int length = MasterMind.codeLenght;
        int[] secretArr = secret.giveCode();
        int[] shootArr = shoot.giveCode();
        char[] feedback = new char[length];
        boolean[] usedSecret = new boolean[length];
        boolean[] usedShots = new boolean[length];

        for (int i = 0; i < length; i++) {
            if (secretArr[i] == shootArr[i]) {
                black ++;
                feedback[i] = 'C';
                usedShots[i] = true;
                usedSecret[i] = true;
            }
        }

        // Incorrect placement, color hit
        for (int i = 0; i < length; i++) {
            if (!usedShots[i]) { // Avoids the same color multiple hits displayed problem
                boolean hit = false;
                for (int j = 0; j < length; j++) {
                    if (!usedSecret[j] && shootArr[i] == secretArr[j]) {
                        white++;
                        feedback[i] = 'B';
                        usedSecret[j] = true;
                        hit = true;
                        break;
                    }
                }
                if (!hit) feedback[i] = 'X';
            }
        }
        System.out.println(feedback);

        return new OutcomeFeedback(black, white);
    }
}
