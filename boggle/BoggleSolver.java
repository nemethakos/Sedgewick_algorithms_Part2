import edu.princeton.cs.algs4.In;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class BoggleSolver {
    private BoggleTST<Integer> tst = new BoggleTST<Integer>();

    // Initializes the data structure using the given array of strings as the dictionary.
    // (You can assume each word in the dictionary contains only the uppercase letters A through Z.)
    public BoggleSolver(String[] dictionary) {
        for (int i = 0; i < dictionary.length; i++) {
            String key = dictionary[i];
            tst.put(key, getScore(key));
        }
    }

    private Integer getScore(String s) {
        int score;
        switch (s.length()) {
            case 0:
            case 1:
            case 2:
                score = 0;
                break;
            case 3:
            case 4:
                score = 1;
                break;
            case 5:
                score = 2;
                break;
            case 6:
                score = 3;
                break;
            case 7:
                score = 5;
                break;
            default:
                score = 11;
        }
        return score;
    }

    public static void main(String[] args) {
        In in = new In(args[0]);
        List<String> dictionary = new ArrayList<>();
        while (in.hasNextLine()) {
            String line = in.readLine();
            if (line.length() >= 3) {
                dictionary.add(line);
            }
        }
        in.close();
        BoggleSolver bs = new BoggleSolver(dictionary.toArray(new String[0]));
        char[][] a = {
                { 'D', 'O', 'T', 'Y' },
                { 'T', 'Q', 'S', 'F' },
                { 'M', 'E', 'M', 'O' },
                { 'Z', 'E', 'N', 'W' }
        };
        BoggleBoard board3 = new BoggleBoard(a);

        Iterable<String> validWords = bs.getAllValidWords(board3);

        int sumScore = 0;
        for (String word : validWords) {
            int score = bs.scoreOf(word);
            sumScore += score;
            System.out.format("%s: %d\n", word, score);
        }

    }

    // Returns the set of all valid words in the given Boggle board, as an Iterable.
    public Iterable<String> getAllValidWords(BoggleBoard board) {
        Set<String> words = new HashSet<String>();
        int rows = board.rows();
        int cols = board.cols();
        StepTable st = new StepTable(board);

        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                addWords(row, col, st, words);
            }
        }

        return words;
    }

    // Returns the score of the given word if it is in the dictionary, zero otherwise.
    // (You can assume the word contains only the uppercase letters A through Z.)
    public int scoreOf(String word) {
        Integer score = tst.get(word);
        if (score == null) {
            return 0;
        }
        return score;
    }

    private void addWords(int row, int col, StepTable st, Set<String> words) {
        BoggleTST.Node<Integer> root = tst.getRoot();
        Step first = new Step(Util.getIndex(row, col, st.getCols()),
                              st.getChar(row, col),
                              st.getRows());
        char[] currentWord = new char[st.getRows() * st.getCols() * 2];
        int currentWordLength = 0;
        dfs(root, first, currentWord, currentWordLength, st, words);
    }

    private void dfs(BoggleTST.Node<Integer> node,
                     Step currentStep,
                     char[] currentWord,
                     int currentWordLength,
                     StepTable st,
                     Set<String> words) {


        // mark as visited
        currentWordLength = getWord(currentWord, currentWordLength, currentStep.getCharacter());

        st.visit(currentStep, true);

        int characterStep = 1;
        if (endsWithQU(currentWord, currentWordLength)) {
            characterStep = 2;
        }

        if (currentWordLength > characterStep) {
            node = node.getMid();
        }

        BoggleTST.Node<Integer> nextNode = tst
                .get(node, currentWord, currentWordLength, currentWordLength - characterStep);

        if (nextNode != null) {
            if (nextNode.getVal() != null && currentWordLength >= 3) {
                // found a word!
                String currentWordString = new String(currentWord, 0, currentWordLength);
                words.add(currentWordString);
            }
            if (nextNode.getMid() != null) {
                List<Step> availableNextSteps = st.getAvailableNextSteps(currentStep);
                for (Step nextStep : availableNextSteps) {
                    if (!st.isVisited(nextStep)) {
                        dfs(nextNode, nextStep, currentWord, currentWordLength, st, words);
                    }
                }
            }
        }

        st.visit(currentStep, false);
    }

    private boolean endsWithQU(char[] currentWord, int currentWordLength) {
        if (currentWordLength >= 2) {
            return currentWord[currentWordLength - 1] == 'U'
                    && currentWord[currentWordLength - 2] == 'Q';
        }
        return false;
    }

    private int getWord(char[] currentWord, int currentWordLength, char nextChar) {
        if (nextChar == 'Q') {
            currentWord[currentWordLength++] = 'Q';
            nextChar = 'U';
        }
        currentWord[currentWordLength++] = nextChar;
        return currentWordLength;
    }

}
