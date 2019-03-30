package se.vgregion.ifeed.viewer;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;
import java.io.CharArrayWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class HtmlToJsonpFilter implements Filter {

    private static final Logger log = LoggerFactory.getLogger(HtmlToJsonpFilter.class);

    @Override
    public void init(FilterConfig fConfig) throws ServletException {
    }

    public void doFilter(ServletRequest request, ServletResponse response,
                         FilterChain chain) throws IOException, ServletException {

        if (request.getParameterMap().containsKey("callback")) {
            String url = ((HttpServletRequest) request).getRequestURL().toString();
            System.out.println();
            System.out.println("The call contains callback ");
            System.out.println(url);
            System.out.println();
            if (!url.endsWith(".json")) {
                PrintWriter out = response.getWriter();
                CharResponseWrapper responseWrapper = new CharResponseWrapper(
                        (HttpServletResponse) response);

                chain.doFilter(request, responseWrapper);

                String servletResponse = responseWrapper.toString();


                String callbackName = request.getParameterMap().get("callback")[0];
                String data = putResultIntoJson(servletResponse) + ";\n";
                String js = String.format("(function(){ var data = %s;\n " +
                        "     function runCallback() {\n" +
                        "      var methodName = '%s';\n" +
                        "      if (window[methodName]) {\n" +
                        "          eval(methodName+'(data);');\n" +
                        "          delete data;" +
                        "      } else {\n" +
                        "          setTimeout(\n" +
                        "              runCallback, 500\n" +
                        "          );\n" +
                        "      }\n" +
                        "      }\n" +
                        "      runCallback();" +
                        "})();", data, callbackName);


                //out.write(js.getBytes());

                servletResponse = js;

                HttpServletResponse httpResponse = (HttpServletResponse) response;
                // servletResponse = request.getParameter("callback") + "(" + putResultIntoJson(servletResponse) + ");";
                httpResponse.addHeader("Access-Control-Allow-Origin", "*");
                httpResponse.addHeader("Access-Control-Allow-Methods", "GET,PUT,POST,DELETE");
                httpResponse.setContentType("text/javascript;charset=UTF-8");

                out.write(servletResponse); // Here you can change the response
                return;
            }
        }
        chain.doFilter(request, response);
    }

    public class CharResponseWrapper extends HttpServletResponseWrapper {
        private CharArrayWriter output;

        public String toString() {
            return output.toString();
        }

        public CharResponseWrapper(HttpServletResponse response) {
            super(response);
            output = new CharArrayWriter();
        }

        public PrintWriter getWriter() {
            return new PrintWriter(output);
        }
    }


    static final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    private String putResultIntoJson(String json) throws IOException {
        return gson.toJson(new Jso(json));
    }


    @Override
    public void destroy() {
    }

    public class Jso {
        private final String content;

        public Jso(String content) {
            this.content = content;
        }

        public String getContent() {
            return content;
        }

    }

}
