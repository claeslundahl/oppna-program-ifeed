package se.vgregion.ifeed.server;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by clalu4 on 2015-06-30.
 * Is used ad factory for creating trees of Tokens. It produces a structures that, in a way, mimics the layout of the
 * code (first broken down to text-parts).
 */
public class Parser {

    private int currentIndex;

/*    public List<Token> toTokens(String[] frags) {
        currentIndex = 0;
        return toTokensImpl(frags, null);
    }

    public List<Token> toTokens(String[] frags, Token parent) {
        currentIndex = 0;
        return toTokensImpl(frags, parent);
    }*/

    /**
     * Create a tree of Token(s) to logically navigate a representation of some JavaScript-code.
     * @param frags texts, previously, broken apart on JS-keywords - "{}()" and such.
     * @return the root of a tree that mimics the blocks of the JS-code.
     */
    public Token toToken(String[] frags) {
        currentIndex = 0;
        Token root = new Token();
        root.getChildren().addAll(toTokensImpl(frags, root));
        return root;
    }

    private List<Token> toTokensImpl(String[] frags, Token parent) {
        List<Token> result = new ArrayList<Token>();
        for (; currentIndex < frags.length; ) {
            String frag = frags[currentIndex];
            Token token = new Token();
            token.setParent(parent);
            result.add(token);

            if (startHandleTextPartIfAppropriate(frags, token)) {
                currentIndex++;
                continue;
            } else if (frag.trim().equals("{")) {
                token.setStart("{");
                token.setEnd("}");
                currentIndex++;
                token.getChildren().addAll(toTokensImpl(frags, token));
            } else if (frag.trim().equals("}")) {
                currentIndex++;
                return result;
            } else {
                currentIndex++;
                token.setStart(frag);
            }

        }
        return result;
    }

    private boolean startHandleTextPartIfAppropriate(String[] frags, Token parent, String delimiter) {
        if (!frags[currentIndex].equals(delimiter)) {
            return false;
        }
        parent.setStart(delimiter);
        parent.setEnd(delimiter);
        currentIndex++;
        int count = 0;
        for (; currentIndex < frags.length; currentIndex++) {
            if (frags[currentIndex].matches("[\\r\\n]")) {
                for (int i = currentIndex - 20; i < currentIndex; i++) {
                    System.out.println("frag" + i + "=" + frags[i]);
                }
                System.out.println("Error on frag " + currentIndex + " using delimiter " + delimiter);
                System.out.println("String starts on " + (currentIndex - count));
                throw new RuntimeException(parent.toString());
            }
            // Look for the end of the line. The delimiter that - in the same time - is not escaped with '/' and
            // making sure that it, in turn, is not escaped either.
            if (frags[currentIndex].trim().equals(delimiter) &&
                    (!frags[currentIndex - 1].trim().equals("\\")
                            || frags[currentIndex - 1].trim().equals("\\") && frags[currentIndex - 2].trim().equals("\\"))) {
                System.out.println("a text " + parent);
                break;
            } else {
                Token child = new Token();
                child.setParent(parent);
                parent.getChildren().add(child);
                child.setStart(frags[currentIndex]);
                count++;
            }
        }
        return true;
    }

    private boolean startHandleTextPartIfAppropriate(String[] frags, Token parent) {
        boolean r1 = startHandleTextPartIfAppropriate(frags, parent, "\"");
        boolean r2 = startHandleTextPartIfAppropriate(frags, parent, "\'");
        return r1 || r2;
    }

}
