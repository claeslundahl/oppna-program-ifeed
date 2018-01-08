package se.vgregion.ifeed.service.solr;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;

/**
 * @author anders
 * 
 */
public class SolrQueryEscaper {
    private static final char ESCAPE_CHAR = '\\';
    private static final char[] SPECIAL_CHARACTERS = { '+', '-', '!', '(', ')', '{', '}', '[', ']', '^', '\'',
            '~', '?', ':', '\\', ' ' };

    private static final String[] SPECIAL_CHAR_PAIR = { "&&", "||" };
    private static final String[] SPECIAL_CHAR_PAIR_ESCAPED = { "\\&&", "\\||" };

    public static String escape(String query) {
        if (query == null) {
            query = "";
        }
        StringBuilder sb = new StringBuilder(query.length());

        for (char ch : query.toCharArray()) {
            if (isSpecialCharacter(ch)) {
                sb.append(ESCAPE_CHAR);
            }
            sb.append(ch);
        }
        String escapedQuery = sb.toString();
        return StringUtils.replaceEach(escapedQuery, SPECIAL_CHAR_PAIR, SPECIAL_CHAR_PAIR_ESCAPED);
    }

    private static boolean isSpecialCharacter(char ch) {
        return ArrayUtils.contains(SPECIAL_CHARACTERS, ch);
    }
}
