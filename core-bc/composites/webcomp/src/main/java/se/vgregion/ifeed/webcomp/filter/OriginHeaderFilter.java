package se.vgregion.ifeed.webcomp.filter;

import javax.servlet.*;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collection;

/**
 * Created by clalu4 on 2015-05-18.
 */
public class OriginHeaderFilter implements Filter {

    final String accessControlAllowOriginKey = "Access-Control-Allow-Origin";

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        chain.doFilter(request, response);

        addIfNotThere(httpResponse, accessControlAllowOriginKey, "*");
        addIfNotThere(httpResponse, "Access-Control-Allow-Methods", "GET,PUT,POST,DELETE");
    }

    private void addIfNotThere(HttpServletResponse httpResponse, String key, String value) {
        //Collection<String> presentValue = httpResponse.getHeaders(key);
        /*if (presentValue == null || presentValue.isEmpty()) {
            httpResponse.setHeader(key, value);
        } else {
            httpResponse.addHeader(key, value);
        }*/

        httpResponse.addHeader(key,value);
    }

    @Override
    public void destroy() {

    }
}
