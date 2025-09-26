package game;

import java.util.Random;

public class Code {
    private final int[] code;

    public Code(int[] code) {
        this.code = code;
    }

    // String to code converter
    public static Code stringToCode(String string) {
        int[] codeArr = new int[MasterMind.codeLenght];
        for (int i = 0; i < MasterMind.codeLenght; i++) {
            codeArr[i] = Character.getNumericValue(string.charAt(i));
        }
        return new Code(codeArr);
    }

    // Random code generator
    public static Code randomCode() {
        int[] kodTab = new int[MasterMind.codeLenght];
        Random random = new Random();
        for (int i = 0; i < MasterMind.codeLenght; i++) {
            kodTab[i] = random.nextInt(MasterMind.colorNumber) + 1;
        }
        return new Code(kodTab);
    }

    protected int[] giveCode() {
        return code;
    }

    @Override
    public String toString() {
        return code[0] + ", " + code[1] + ", " + code[2] + ", " + code[3];
    }

}
