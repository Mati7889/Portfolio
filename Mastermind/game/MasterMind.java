package game;

import players.*;

import java.util.Scanner;

public class MasterMind {
    // Game settings
    public  static int codeLenght = 4;
    public  static int colorNumber = 6;

    // Game mechanism
    public static void start() {
        Scanner scanner = new Scanner(System.in);

        while (true) {

            System.out.println("Select mode:");
            System.out.println("1. You set the code, the computer guesses.");
            System.out.println("2. The computer sets the code, you guess it.");
            try {
                int tryb = scanner.nextInt();
                scanner.nextLine();

                if (tryb == 1) {
                    System.out.println("Selected mode: computer guesses");
                    computerGuesses(scanner);
                    break;
                } else if (tryb == 2) {
                    System.out.println("Selected mode: you guess");
                    playerGuesses(scanner);
                    break;
                } else {
                    System.out.println("Incorrect input! Try again.");
                }
            } catch (java.util.InputMismatchException e) {
                System.out.println("Must be 1 or 2!");
                scanner.nextLine(); // Incorrect input - skip line
            }
        }
    }

    // Computer guesses the codeT
    private static void computerGuesses(Scanner scanner) {
        System.out.println("Set the code with " + codeLenght + " numbers:");
        Code secret = Code.stringToCode(scanner.nextLine());
        computerPlayer computer = new computerPlayer();

        int tries = 1;
        boolean guessed = false;

        while (!guessed) {
            Code shoot = computer.guess();

            System.out.println("Computer guesses: " + shoot);
            OutcomeFeedback outcome = Feedback.checkGuess(secret, shoot); //Computer gets feedback

            //You can enable visual feedback for this mode by changing the outcome variable to the one below

            //OutcomeFeedback outcome = PlayerFeedback.giveFeedback(secret, shoot);

            if (outcome.give()[0] == codeLenght) {
                System.out.println("Computer guessed the code in: " + tries + " tries");
                guessed = true;
            } else {
                computer.updatePossibleCodes(shoot, outcome); // Updates possible code list for the next try
                tries++;
            }
        }
    }

    // Computer sets the code you guess
    private static void playerGuesses(Scanner scanner) {
        System.out.println("The code is: " + codeLenght + " numbers.");
        Code secret = Code.randomCode();

        int tries = 1;
        boolean guessed = false;
        while (!guessed) {
            System.out.print("Try number " + tries + ":\n");
            String code = scanner.nextLine();
            Code playersCode = Code.stringToCode(code);
            OutcomeFeedback outcome = PlayerFeedback.giveFeedback(secret, playersCode);

            if (outcome.give()[0] == codeLenght) {
                System.out.println("You won in " + tries + " tries");
                guessed = true;
            } else {
                tries++;
            }

        }
    }
}
