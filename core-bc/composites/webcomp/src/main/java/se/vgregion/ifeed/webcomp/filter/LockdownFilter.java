package se.vgregion.ifeed.webcomp.filter;

import javax.servlet.*;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

public final class LockdownFilter implements Filter {

    private FilterConfig filterConfig = null;

    private boolean active = false;

    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {

        if (active) {
            throw new RuntimeException("Website is locked! Turn off locking in configuration to resolve this.");
        }

/*        long start = System.currentTimeMillis();
        String address = request.getRemoteAddr();
        String file = ((HttpServletRequest) request).getRequestURI();*/

        chain.doFilter(request, response);

        /*System.out.println(
                "User access! " +
                        " User IP: " + address +
                        " Resource: " + file +
                        " Milliseconds used: " + (System.currentTimeMillis() - start)
        );*/

    }

    public void destroy() {

    }

    @Override
    public void init(FilterConfig filterConfig) {
        this.active = getBlockWebWithErrorFromConfiguration();
    }

    static boolean getBlockWebWithErrorFromConfiguration() {
        Properties properties = fetchProperties();
        final String key = "block.web.with.error";
        if (properties.getProperty(key) != null) {
            return properties.getProperty(key).trim().equalsIgnoreCase("true");
        }
        return false;
    }


    static Properties fetchProperties() {
        try {
            return fetchProperties(Paths.get(System.getProperty("user.home"), ".hotell", "ifeed", "config.properties"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    static Properties fetchProperties(Path path) throws IOException {
        Properties properties = new Properties();
        try (InputStream src = Files.newInputStream(path)) {

            properties.load(new InputStreamReader(src));

        }
        return properties;
    }

}