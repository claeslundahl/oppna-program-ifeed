package se.vgregion.ifeed.server;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.logging.Logger;

public class NoCacheHeaderFilter implements javax.servlet.Filter {
    private ServletContext servletContext;
    private Logger log;

    public NoCacheHeaderFilter() {
        super();
    }

    public void init(FilterConfig filterConfig) throws ServletException {
        servletContext = filterConfig.getServletContext();
        log = Logger.getLogger(NoCacheHeaderFilter.class.getName());
    }

    public void doFilter(ServletRequest req,
                         ServletResponse res,
                         FilterChain filterChain)
            throws IOException, ServletException {

        ServletRequestWrapper httpReq = new ServletRequestWrapper((HttpServletRequest) req);
        HttpServletResponse httpRes = (HttpServletResponse) res;

        // HttpSession session = httpReq.getSession();

        /*
            Cache-Control: no-cache, no-store, must-revalidate
            Pragma: no-cache
            Expires: 0
         */

        System.out.println("Runs header filter");

        httpReq.addHeader("Cache-Control", "no-cache, no-store, must-revalidate");
        httpReq.addHeader("Pragma", "no-cache");
        httpReq.addHeader("Expires", "0");

        filterChain.doFilter(httpReq, httpRes);
    }

    public void destroy() {
    }
}