package se.vgregion.ifeed.server;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by clalu4 on 2015-07-01.
 * Helps to split a text into bits. It works roughly as the String.split function but with some implicit tweaks: It
 * keeps the token that is used to delimit the splits - the string/pattern found/used to as delimiters is included
 * into the result.
 */
public class Splitter {

    private final List<String> result = new ArrayList<String>();

    static private final String WITH_DELIMITER = "((?<=%1$s)|(?=%1$s))";

    /**
     * Create instance with initial input value.
     * @param input the text to be hacked into pieces by later calls to split-method.
     */
    public Splitter(String input) {
        result.add(input);
    }

    /**
     * Getter for the, current, result of the instance. This is also an input - if the returned list is modified then
     * the latest content is also used when more operations (more splits) are applied.
     * @return
     */
    public List<String> getResult() {
        return result;
    }

    /**
     * Splits each and every string inside the result.
     * @param regex the expression to use when looking for places to break the text.
     */
    public void split(String regex) {
        List<String> items = new ArrayList<String>(this.result);
        this.result.clear();
        for (String item : items) {

            String[] r = item.split(String.format(WITH_DELIMITER, regex));
            result.addAll(Arrays.asList(r));

        }
    }

    /**
     * Splits each and every string inside the result.
     * @param c the character to use when looking for places to break the text.
     */
    public void split(char c) {
        List<String> items = new ArrayList<String>(this.result);
        this.result.clear();
        for (String item : items) {
            List<String> r = splitOnChar(item, c);
            result.addAll(r);
        }
    }

    static List<String> splitOnChar(String input, char c) {
        final String C = Character.toString(c);
        List<String> r = new ArrayList<String>();
        for (int i = 0; i < input.length(); i++) {
            if (input.charAt(i) == c) {
                r.add(input.substring(0, i));
                r.add(C);
                input = input.substring(i + 1);
                i = 0;
            }
        }
        if (!input.equals("")) {
            r.add(input);
        }

        r.add(input); // add the reminder.
        if (!r.isEmpty()) {
            String last = r.remove(r.size() - 1);
            if (last.endsWith(C)) {
                last = last.replace(C, "");
                r.add(last);
                r.add(C);
            }
        }

        while (r.remove("")) ;
        return r;
    }


    /**
     * Converts the result to a array.
     * @return the result as an array.
     */
    public String[] toArray() {
        return result.toArray(new String[result.size()]);
    }

}
