package se.vgregion.ifeed.viewer;

import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.vgregion.ifeed.webcomp.filter.GenericResponseWrapper;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class JsonpCallbackFilter implements Filter {

    private static final Logger log = LoggerFactory.getLogger(JsonpCallbackFilter.class);

    @Override
    public void init(FilterConfig fConfig) throws ServletException {
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException,
            ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;

        httpRequest.setCharacterEncoding("UTF-8");

        HttpServletResponse httpResponse = (HttpServletResponse) response;

        httpResponse.addHeader("Access-Control-Allow-Origin", "*");
        httpResponse.addHeader("Access-Control-Allow-Methods", "GET,PUT,POST,DELETE");

        @SuppressWarnings("unchecked")
        Map<String, String[]> parms = httpRequest.getParameterMap();
        GenericResponseWrapper wrapper = new GenericResponseWrapper(httpResponse);
        OutputStream out = httpResponse.getOutputStream();
        chain.doFilter(request, wrapper);

        byte[] result = null;
        if (IFeedViewerController.jsonResult.get() != null) {
            result = IFeedViewerController.jsonResult.get().getBytes();
            IFeedViewerController.jsonResult.set(null);
        } else {
            result = addVarnishHeadersToResponse(wrapper.getData(), httpResponse);
        }

        if (parms.containsKey("callback")) {
            out.write(new String(parms.get("callback")[0] + "(").getBytes());
            out.write(result);
            out.write(new String(");").getBytes());
            wrapper.setContentType("text/javascript;charset=UTF-8");
        } else {
            out.write(result);
        }
        out.close();
    }

    private byte[] addVarnishHeadersToResponse(byte[] json, HttpServletResponse httpResponse) throws IOException {
        try {
            // System.out.println("Json is " + new java.lang.String(json));
            ArrayList<HashMap<String, Object>> result =
                    new ObjectMapper().readValue(json, ArrayList.class);

            for (HashMap<String, Object> map : result) {
                String id = (String) map.get("id");
                String[] idFrags = id.split(Pattern.quote(":"));
                String uuid = idFrags[idFrags.length - 1];
                //httpResponse.addHeader("xkey", "ifeed/" + uuid);
            }
        } catch (Exception e) {

        }

        return json;
    }


    @Override
    public void destroy() {
    }
}
