package ch.pterrettaz.goocheat;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class GooCheat {

    private Map<Integer, Integer> lettersWeight = new HashMap<Integer, Integer>();
    private Map<String, String> accentsTable = new HashMap<String, String>();
    private Node dictionary = new Node();
    private Set<String> words;
    private int maxWords = 5;

    public GooCheat() throws Exception {
        BufferedReader in = new BufferedReader(new InputStreamReader(getClass().getResourceAsStream("accents.txt")));
        String word = null;
        while ((word = in.readLine()) != null) {
            String[] parts = word.split(" ");
            accentsTable.put(parts[0], parts[1]);
        }
        in = new BufferedReader(new InputStreamReader(getClass().getResourceAsStream("fr.dic")));
        while ((word = in.readLine()) != null) {
            dictionary.add(stripAccents(word));
        }
        in = new BufferedReader(new InputStreamReader(getClass().getResourceAsStream("fr.weight")));
        while ((word = in.readLine()) != null) {
            String[] parts = word.split(" ");
            lettersWeight.put((int) parts[0].charAt(0), Integer.parseInt(parts[1]));
        }
    }

    private String stripAccents(String s) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < s.length(); i++) {
            String c = s.charAt(i) + "";
            String character = accentsTable.get(c);
            if (character == null)
                character = c;
            builder.append(character);
        }
        return builder.toString();
    }

    public int weight(String word) {
        int weight = 0;
        for (int i = 0; i < word.length(); i++) {
            Integer w = lettersWeight.get((int) word.charAt(i));
            weight += w;
        }
        return weight;
    }

    private void addWord(String word) {
        if (words.size() == maxWords) {
            String minWord = null;
            Integer min = null;
            for (String w : words) {
                int weight = weight(w);
                if (min == null || min > weight) {
                    minWord = w;
                    min = weight;
                }
            }
            if (min < weight(word)) {
                words.remove(minWord);
                words.add(word);
            }
        } else {
            words.add(word);
        }
    }

    public synchronized List<String> getValidWordsPermutation(String in, int maxWord) {
        this.maxWords = maxWord;
        in = stripAccents(in);
        words = new HashSet<String>();
        StringBuilder out = new StringBuilder();
        boolean[] used = new boolean[in.length()];
        permuteWords(in, out, used, 0);
        List<String> list = new LinkedList<String>(words);
        Collections.sort(list, new Comparator<String>() {
            public int compare(String o1, String o2) {
                int cmp = new Integer(weight(o1)).compareTo(weight(o2));
                cmp *= -1;
                if (cmp == 0)
                    cmp = o1.compareTo(o2);
                return cmp;
            }
        });
        return list;
    }

    private void permuteWords(String in, StringBuilder out, boolean[] used, int depth) {
        if (!dictionary.hasPrefix(out.toString())) {
            return;
        }
        if (dictionary.exists(out.toString())) {
            addWord(out.toString());
        }

        if (depth == in.length()) {
            return;
        }

        for (int i = 0; i < in.length(); i++) {
            if (used[i])
                continue;
            used[i] = true;
            out.append(in.charAt(i));
            permuteWords(in, out, used, depth + 1);
            out.deleteCharAt(out.length() - 1);
            used[i] = false;
        }
    }

}
