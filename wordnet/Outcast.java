import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdOut;

import java.util.Arrays;

public class Outcast {

    private WordNet wordNet;

    // constructor takes a WordNet object
    public Outcast(WordNet wordnet) {

        nullCheck(wordnet);
        this.wordNet = wordnet;
    }

    private static void nullCheck(Object arg) {
        if (arg == null) {
            throw new IllegalArgumentException("Null argument!");
        }
    }

    // see test client below
    public static void main(String[] args) {
        String synsetsFileName = args[0];
        String hypernymsFileName = args[1];
        System.out.format("synsets: %s, hypernyms: %s\n\n", synsetsFileName, hypernymsFileName);
        WordNet wordnet = new WordNet(synsetsFileName, hypernymsFileName);
        Outcast outcast = new Outcast(wordnet);
        for (int t = 2; t < args.length; t++) {
            In in = new In(args[t]);
            String[] nouns = in.readAllStrings();
            StdOut.println(args[t] + ": " + Arrays.toString(nouns) + " -> " + outcast.outcast(nouns));
        }
    }

    // given an array of WordNet nouns, return an outcast
    public String outcast(String[] nouns) {
        nullCheck(nouns);

        int maxDistance = Integer.MIN_VALUE;
        int outcastId = -1;


        for (int nounIndex = 0; nounIndex < nouns.length; nounIndex++) {
            int distance = 0;
            for (int otherNounIndex = 0; otherNounIndex < nouns.length; otherNounIndex++) {
                if (nounIndex != otherNounIndex) {
                    distance += wordNet.distance(nouns[nounIndex], nouns[otherNounIndex]);
                }
            }
            if (distance > maxDistance) {
                maxDistance = distance;
                outcastId = nounIndex;
            }
        }

        return nouns[outcastId];
    }

}
