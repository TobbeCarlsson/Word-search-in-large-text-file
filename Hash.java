import java.util.HashMap;


public class Hash {


    private final HashMap<Character, Integer> charValues = new HashMap<>() {{
        put(' ', 0);
        put('a', 1);
        put('b', 2);
        put('c', 3);
        put('d', 4);
        put('e', 5);
        put('f', 6);
        put('g', 7);
        put('h', 8);
        put('i', 9);
        put('j', 10);
        put('k', 11);
        put('l', 12);
        put('m', 13);
        put('n', 14);
        put('o', 15);
        put('p', 16);
        put('q', 17);
        put('r', 18);
        put('s', 19);
        put('t', 20);
        put('u', 21);
        put('v', 22);
        put('w', 23);
        put('x', 24);
        put('y', 25);
        put('z', 26);
        put('å', 27);
        put('ä', 28);
        put('ö', 29);
    }};

    private HashMap<Character, Integer> charValuesExp1;

    private HashMap<Character, Integer> charValuesExp2;

    public Hash() {
        preCalc();
    }

    public int getHash(String word) {
        int hashValue = 0;

        for (int i=0; i<3; i++) {
            if (i < word.length()) {
                switch (i) {
                    case 2: hashValue += charValues.get(word.charAt(i));
                            break;
                    case 1: hashValue += charValuesExp1.get(word.charAt(i));
                            break;
                    case 0: hashValue += charValuesExp2.get(word.charAt(i));
                            break;
                }
            }
        }
        hashValue = hashValue * 8;
        return hashValue;
    }

    private void preCalc() {
        charValuesExp1 = new HashMap<>();
        for (Character c : charValues.keySet()) {
            charValuesExp1.put(c, charValues.get(c) * 30);
        }

        charValuesExp2 = new HashMap<>();
        for (Character c : charValues.keySet()) {
            charValuesExp2.put(c, (int) (charValues.get(c) * Math.pow(30, 2)));
        }
    }

}
