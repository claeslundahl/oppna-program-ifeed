package se.vgregion.ifeed.server;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * Currently there are no support for GWT with Ie7 and all the way upp to latest.
 * IE8 is the one that is officially supported.
 * The problem for Ie7 is that GWT uses the 'finally' key-word in its generated scripts. This is not supported and
 * breaks the execution of the script on those old browsers.
 * To cheat through that problem - we rewrite the scripts before they are delivered to Ie7-clients in this
 * servlet-filter. The finally block is removed and its content, the lines to execute, is copied inside the try- and
 * catch-block instead.
 */
public class Ie7Rewrite implements Filter {

    /**
     * Cache for rewritten scripts. This is due to the excessive amount of time consumed when processing the texts.
     */
    private static Map<String, String> urlToContentCache = new HashMap<String, String>();

    /**
     * Does nothing at the moment. Just here for making the contract complete.
     */
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    /**
     * Main handler that gets the call from the client. It examines the request to find out if it is from a Ie7. If so
     * it then tries to rewrite, and cache, the served script (it only triggers on *.js-files).
     * @param request
     * @param response
     * @param chain
     * @throws IOException
     * @throws ServletException
     */
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        PrintWriter out = response.getWriter();
        CharResponseWrapper responseWrapper = new CharResponseWrapper(
                (HttpServletResponse) response);

        HttpServletRequest hr = (HttpServletRequest) request;
        String userAgent = hr.getHeader("User-Agent");
        String path = hr.getRequestURI().substring(hr.getContextPath().length());
        System.out.println("Path " + path + " userAgent = " + userAgent);

        String result = null;
        if (userAgent.indexOf("MSIE 7.0") > -1) {
            if (urlToContentCache.containsKey(path)) {
                result = urlToContentCache.get(path);
            } else {
                chain.doFilter(request, responseWrapper);
                result = new String(responseWrapper.toString());
                result = removeFinallyBlockFrom(result);
                urlToContentCache.put(path, result);
            }
        } else {
            chain.doFilter(request, responseWrapper);
            result = new String(responseWrapper.toString());
        }

        out.write(result);
    }

    /**
     * Does nothing at the moment. Just here for making the contract complete.
     */
    @Override
    public void destroy() {

    }

    /**
     * Notice that there are redundant calls in this method: "splitter.split('"');" and
     * "splitter.split(Pattern.quote("\""));" for instance. This is because the code failed on the linux test
     * environment but succeeded on the developer machine (windows), with the same input?!
     * @param text to be broken into logical meaningfully parts.
     * @return the entire input (no data loss) broken into bits.
     */
    static String[] toParts(String text) {
        Splitter splitter = new Splitter(text);
        splitter.split(";|\\}|\\{|\\(|\\)");
        splitter.split('\\');
        splitter.split(Pattern.quote("\""));
        splitter.split(Pattern.quote("'"));
        splitter.split("[\\r\\n]");
        splitter.split('"');
        splitter.split('\'');
        splitter.split('\n');
        splitter.split('\r');

        return splitter.toArray();
    }

    static String removeFinallyBlockFrom(String javaScript) {
        String[] parts = toParts(javaScript);
        Parser parser = new Parser();
        Token token = parser.toToken(parts);

        token.visit(new Token.Visitor() {
            @Override
            public void after(Token token) {
                if (token.getStart().trim().equals("finally")) {
                    Token finallyBlock = token.next("\\{");
                    Token catchOrTry = token.previous("catch|try");
                    if (catchOrTry == null) {
                        return;
                    }
                    Token catchOrTryBlock = catchOrTry.next("\\{");

                    if (catchOrTry.getStart().trim().equals("try")) {
                        // Case: There are a try{} finally{} statement here.
                        // No catch exists. Ie7 does not like finally at all so...
                        token.setStart("catch(e)");
                        Token.Tokens finallyBlockClone = Token.deepClone(finallyBlock.getChildren());
                        finallyBlockClone.alterParentOfItems(catchOrTryBlock);
                        catchOrTryBlock.getChildren().addAll(finallyBlockClone);
                    } else {
                        // Case: There are a try{} catch(e){} finally{} statement here.
                        // Ie7 still does not like finally, so ut must be erased.
                        Token tryStart = catchOrTry.previous("try");
                        if (tryStart == null) {
                            System.out.println(catchOrTry);
                        }
                        Token tryBlock = tryStart.next("\\{");
                        Token.Tokens finallyBlockClone = Token.deepClone(finallyBlock.getChildren());
                        finallyBlockClone.alterParentOfItems(catchOrTryBlock);
                        tryBlock.getChildren().addAll(finallyBlockClone);

                        finallyBlockClone = Token.deepClone(finallyBlock.getChildren());
                        finallyBlockClone.alterParentOfItems(catchOrTryBlock);
                        catchOrTryBlock.getChildren().addAll(finallyBlockClone);

                        finallyBlock.removeFromParent();
                        token.removeFromParent();
                    }

                }
            }
        });

        return token.toString();
    }

}
