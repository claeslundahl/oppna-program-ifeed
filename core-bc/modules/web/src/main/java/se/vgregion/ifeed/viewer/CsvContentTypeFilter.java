package se.vgregion.ifeed.viewer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.vgregion.ifeed.types.IFeed;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLDecoder;

public class CsvContentTypeFilter implements Filter {

    private static final Logger log = LoggerFactory.getLogger(CsvContentTypeFilter.class);

    @Override
    public void init(FilterConfig fConfig) throws ServletException {
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException,
            ServletException {
        String instance = "";
        try {
            HttpServletRequest httpRequest = (HttpServletRequest) request;
            HttpServletResponse httpResponse = (HttpServletResponse) response;

            instance = httpRequest.getParameter("instance");
            instance = URLDecoder.decode(instance, "UTF-8");
            IFeed feed = IFeed.fromJson(instance);

            httpResponse.setHeader("Content-Disposition", "inline; filename=" + feed.getName() + ".csv");
            response.setContentType("text/csv;charset=UTF-8");

            chain.doFilter(request, response);
        } catch (Exception e) {
            StringBuilder sb = new StringBuilder();
            sb.append("json-code that dit not turn into a feed:\n");
            sb.append(instance);
            throw new RuntimeException(sb.toString(), e);
        }
    }

    @Override
    public void destroy() {
    }
}
