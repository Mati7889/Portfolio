package players;

import game.*;

import java.util.ArrayList;
import java.util.List;

public class computerPlayer {
    private List<Code> possibleCodes;
    private final int codeLength = MasterMind.codeLenght;
    private final int numberOfColors = MasterMind.colorNumber;

    public computerPlayer() {
        this.possibleCodes = generatePossibleCodes();
    }

    private List<Code> generatePossibleCodes() {
        List<Code> codes = new ArrayList<>();
        recGeneration(new int[codeLength], 0, codes);
        return codes;
    }

    private void recGeneration(int[] code, int position, List<Code> codes) {
        if (position == codeLength) {
            codes.add(new Code(code.clone()));
            return;
        }

        for (int i = 1; i <= numberOfColors; i++) {
            code[position] = i;
            recGeneration(code, position + 1, codes);
        }
    }

    public Code guess() {
        if (possibleCodes.isEmpty()) return null;
        return possibleCodes.get(0);
    }

    // Called from original guess and its feedback
    public void updatePossibleCodes(Code code, OutcomeFeedback feedback) {
        this.possibleCodes = possibleCodes.stream()
                .filter(k -> Feedback.checkGuess(code, k).equals(feedback))
                .toList();
    }


}
