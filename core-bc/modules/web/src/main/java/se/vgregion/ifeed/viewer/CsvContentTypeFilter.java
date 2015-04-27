package se.vgregion.ifeed.viewer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.vgregion.ifeed.types.IFeed;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class CsvContentTypeFilter implements Filter {

    private static final Logger log = LoggerFactory.getLogger(CsvContentTypeFilter.class);

    @Override
    public void init(FilterConfig fConfig) throws ServletException {
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException,
            ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        String instance = httpRequest.getParameter("instance");
        IFeed feed = IFeed.fromJson(instance);

        httpResponse.setHeader("Content-Disposition", "inline; filename=" + feed.getName() + ".csv");
        response.setContentType("text/csv;charset=UTF-8");

        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {
    }
}
