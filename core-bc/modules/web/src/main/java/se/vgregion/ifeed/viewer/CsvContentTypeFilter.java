package se.vgregion.ifeed.viewer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.vgregion.ifeed.types.IFeed;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.regex.Pattern;

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
            if (!isIntegerRegex(instance)) {
                IFeed feed = IFeed.fromJson(instance);
                httpResponse.setHeader("Content-Disposition", "inline; filename=" + feed.getName() + ".csv");
            } else {
                String name = request.getParameter("name");
                if (name != null && !name.isEmpty()) {
                    httpResponse.setHeader("Content-Disposition", "inline; filename=" + name + ".csv");
                } else{
                    httpResponse.setHeader("Content-Disposition", "inline; filename=" + instance + ".CSV");
                }
            }
            response.setContentType("text/csv;charset=UTF-8");
            response.setCharacterEncoding("UTF-8");
            chain.doFilter(request, response);
        } catch (Exception e) {
            StringBuilder sb = new StringBuilder();
            sb.append("json-code that dit not turn into a feed:\n");
            sb.append(instance);
            throw new RuntimeException(sb.toString(), e);
        }
    }

    public static Pattern patternInteger = Pattern.compile("^[0-9]+$");

    public static boolean isIntegerRegex(String str) {
        return patternInteger.matcher(str).matches();
    }

    @Override
    public void destroy() {
    }
}
