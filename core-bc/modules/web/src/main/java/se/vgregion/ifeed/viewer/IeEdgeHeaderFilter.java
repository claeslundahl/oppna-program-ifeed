package se.vgregion.ifeed.viewer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class IeEdgeHeaderFilter implements Filter {

    private static final Logger log = LoggerFactory.getLogger(IeEdgeHeaderFilter.class);

    @Override
    public void init(FilterConfig fConfig) throws ServletException {
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException,
            ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        httpResponse.setHeader("X-UA-Compatible", "IE=edge");
        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {
    }
}
