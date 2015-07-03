package se.vgregion.ifeed.server;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;
import java.io.CharArrayWriter;
import java.io.PrintWriter;

/**
 * Used for catching the served result as text.
 */
public class CharResponseWrapper extends HttpServletResponseWrapper {

    private CharArrayWriter output;

    public String toString() {
        return output.toString();
    }

    /**
     * Creates an instance.
     * @param response the object to wrap.
     */
    public CharResponseWrapper(HttpServletResponse response) {
        super(response);
        output = new CharArrayWriter();
    }

    /**
     * Getter for the writer object.
     * @return a new constructed print writer.
     */
    public PrintWriter getWriter() {
        return new PrintWriter(output);
    }

}