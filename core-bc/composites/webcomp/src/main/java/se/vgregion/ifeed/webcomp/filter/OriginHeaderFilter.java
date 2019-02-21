package se.vgregion.ifeed.webcomp.filter;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collection;

/**
 * Created by clalu4 on 2015-05-18.
 */
public class OriginHeaderFilter implements Filter {

    public void destroy() {
    }
    public static String VALID_METHODS = "DELETE, HEAD, GET, OPTIONS, POST, PUT";

    public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain) throws ServletException, IOException {
        HttpServletRequest httpReq = (HttpServletRequest) req;
        HttpServletResponse httpResp = (HttpServletResponse) resp;

        // No Origin header present means this is not a cross-domain request
        String origin = httpReq.getHeader("Origin");
        if (origin == null) {
            // Return standard response if OPTIONS request w/o Origin header
            if ("OPTIONS".equalsIgnoreCase(httpReq.getMethod())) {
                httpResp.setHeader("Allow", VALID_METHODS);
                httpResp.setStatus(200);
                return;
            }
        } else {
            // This is a cross-domain request, add headers allowing access
            httpResp.setHeader("Access-Control-Allow-Origin", origin);
            httpResp.setHeader("Access-Control-Allow-Methods", VALID_METHODS);

            String headers = httpReq.getHeader("Access-Control-Request-Headers");
            if (headers != null)
                httpResp.setHeader("Access-Control-Allow-Headers", headers);

            // Allow caching cross-domain permission
            httpResp.setHeader("Access-Control-Max-Age", "3600");
        }
        // Pass request down the chain, except for OPTIONS
        if (!"OPTIONS".equalsIgnoreCase(httpReq.getMethod())) {
            chain.doFilter(req, resp);
        }
    }

    public void init(FilterConfig config) throws ServletException {

    }


/*
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
        */
/*if (presentValue == null || presentValue.isEmpty()) {
            httpResponse.setHeader(key, value);
        } else {
            httpResponse.addHeader(key, value);
        }*//*


        httpResponse.addHeader(key,value);
    }

    @Override
    public void destroy() {

    }
*/
}
