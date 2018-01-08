package se.vgregion.ifeed.service.solr;

import java.text.CollationKey;
import java.text.Collator;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Claes Lundahl on 2016-01-04.
 * Should be used when sorting strings containing the swedish umlaut characters å, ä, ö. These are sorted the wrong
 * way in some jvm:s.
 */
public class SwedishCollator extends Collator {

    final static Map<Character, Character> translations = new HashMap<Character, Character>();

    static {
        translations.put('Å', 'A');
        translations.put('å', 'a');
        translations.put('Ä', 'B');
        translations.put('ä', 'b');
        translations.put('Ö', 'o');
        translations.put('ö', 'o');
    }

    private final Collator imp;

    /**
     * Creates instance with a collator to do most of the work.
     *
     * @param imp
     */
    public SwedishCollator(Collator imp) {
        super();
        assert imp != null : "An instance of " + SwedishCollator.class.getSimpleName() + " must be instantiated with" +
                " a Collator (not null).";
        this.imp = imp;
    }

    /**
     * {@link Collator#compare(Object, Object)}
     **/
    @Override
    public int compare(String source, String target) {
        if (haveUmlautChars(source) && haveUmlautChars(target)) {
            Pair pair = new Pair(source, target);
            replaceCharWhenBothHaveItOnSamePlace(pair);
            source = pair.one;
            target = pair.two;
        }
        return imp.compare(source, target);
    }

    /**
     * {@link Collator#getCollationKey(String)}
     **/
    @Override
    public CollationKey getCollationKey(String source) {
        return imp.getCollationKey(source);
    }

    /**
     * {@link Collator#hashCode()}
     **/
    @Override
    public int hashCode() {
        return imp.hashCode();
    }

    static boolean haveUmlautChars(String s) {
        for (Character c : translations.keySet()) {
            if (s.indexOf(c) != -1) {
                return true;
            }
        }
        return false;
    }

    static void replaceCharWhenBothHaveItOnSamePlace(Pair pair) {
        for (int i = 0, j = Math.min(pair.one.length(), pair.two.length()); i < j; i++) {
            Character oc = pair.one.charAt(i);
            Character tc = pair.two.charAt(i);
            if (translations.keySet().contains(oc) && translations.keySet().contains(tc)) {
                pair.one = replace(pair.one, i, translations.get(oc));
                pair.two = replace(pair.two, i, translations.get(tc));
            }
        }
    }

    static String replace(String str, int index, char replace) {
        if (str == null) {
            return str;
        } else if (index < 0 || index >= str.length()) {
            return str;
        }
        char[] chars = str.toCharArray();
        chars[index] = replace;
        return String.valueOf(chars);
    }

    static class Pair {

        public Pair(String one, String two) {
            this.one = one;
            this.two = two;
        }

        public String one;
        public String two;

    }

}
