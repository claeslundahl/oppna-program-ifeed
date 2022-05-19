package se.vgregion.ifeed.viewer;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

public class MetadataJsonMimeFilter  implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    public void doFilter(ServletRequest request, ServletResponse resp,
                         FilterChain chain) throws IOException, ServletException {
        String url = ((HttpServletRequest)request).getRequestURL().toString();
        if (url.contains("metadata.json")) {
            System.out.println("Setting the type!");
            resp.setContentType("application/json");
            chain.doFilter(request, resp);
        } else {
            System.out.println(url);
            System.out.println("Is not an json url!");
        }
    }

    @Override
    public void destroy() {

    }

}
