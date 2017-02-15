package se.vgregion.ifeed.viewer;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;
import java.io.*;
import java.lang.ref.WeakReference;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.WeakHashMap;

public class CacheFilter implements Filter {

    private static class ByteArrayServletStream extends ServletOutputStream {

        ByteArrayOutputStream baos;

        ByteArrayServletStream(ByteArrayOutputStream baos) {
            this.baos = baos;
        }

        public void write(int param) throws IOException {
            baos.write(param);
        }
    }

    private static class ByteArrayPrintWriter {

        private ByteArrayOutputStream baos = new ByteArrayOutputStream();

        private PrintWriter pw = new PrintWriter(baos);

        private ServletOutputStream sos = new ByteArrayServletStream(baos);

        public PrintWriter getWriter() {
            return pw;
        }

        public ServletOutputStream getStream() {
            return sos;
        }

        byte[] toByteArray() {
            return baos.toByteArray();
        }
    }

    private class BufferedServletInputStream extends ServletInputStream {

        ByteArrayInputStream bais;

        public BufferedServletInputStream(ByteArrayInputStream bais) {
            this.bais = bais;
        }

        public int available() {
            return bais.available();
        }

        public int read() {
            return bais.read();
        }

        public int read(byte[] buf, int off, int len) {
            return bais.read(buf, off, len);
        }

    }

    private class BufferedRequestWrapper extends HttpServletRequestWrapper {

        ByteArrayInputStream bais;

        ByteArrayOutputStream baos;

        BufferedServletInputStream bsis;

        byte[] buffer;

        public BufferedRequestWrapper(HttpServletRequest req) throws IOException {
            super(req);
            InputStream is = req.getInputStream();
            baos = new ByteArrayOutputStream();
            byte buf[] = new byte[1024];
            int letti;
            while ((letti = is.read(buf)) > 0) {
                baos.write(buf, 0, letti);
            }
            buffer = baos.toByteArray();
        }

        public ServletInputStream getInputStream() {
            try {
                bais = new ByteArrayInputStream(buffer);
                bsis = new BufferedServletInputStream(bais);
            } catch (Exception ex) {
                ex.printStackTrace();
            }

            return bsis;
        }

        public byte[] getBuffer() {
            return buffer;
        }

    }

    private static class TickingVolatileCache {

        static final WeakHashMap<String, TickingVolatileCache> memory = new WeakHashMap<>();

        static TickingVolatileCache getItemInMemory(String key) {
            if (!memory.containsKey(key)) {
                return null;
            }
            TickingVolatileCache item = memory.get(key);
            if (item.hasExpired()) {
                memory.remove(key);
                return null;
            }
            item.countdown--;
            return item;
        }

        static void store(String key, byte[] bytes) {
            TickingVolatileCache item = new TickingVolatileCache(new String(bytes));
            memory.put(key, item);
        }

        final WeakReference<String> responseHold;

        int countdown = 10;

        private TickingVolatileCache(String response) {
            this.responseHold = new WeakReference<String>(response);
        }

        public boolean hasExpired() {
            return (countdown <= 0 || responseHold.get() == null);
        }

        public byte[] getResponse() {
            return responseHold.get().getBytes();
        }


        public static void remove(String itemWithThatRequest) {
            memory.remove(itemWithThatRequest);
        }

    }


    private boolean dumpRequest;
    private boolean dumpResponse;

    public void init(FilterConfig filterConfig) throws ServletException {
        dumpRequest = Boolean.valueOf(filterConfig.getInitParameter("dumpRequest"));
        dumpResponse = Boolean.valueOf(filterConfig.getInitParameter("dumpResponse"));
        // dumpResponse = dumpRequest = true;
    }


    public String toRequestKey(Map<String, String[]> map) {
        Map result = new HashMap();
        for (Map.Entry<String, String[]> entry : map.entrySet()) {
            result.put(entry.getKey(), (entry.getValue() != null) ? Arrays.asList(entry.getValue()) : "null");
        }
        return result.toString();
    }

    // System.out.println("Time for call was " + (System.currentTimeMillis() - timeBefore) + "ms. Instance " + instance + " from page " + fromPage);
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse,
                         FilterChain filterChain) throws IOException, ServletException {
        long timeBefore = System.currentTimeMillis();
        doFilterImpl(servletRequest,servletResponse,filterChain);
        String instance = servletRequest.getParameter("instance");
        String fromPage = servletRequest.getParameter("fromPage");
        System.out.println("CacheFilter - Time for call was " + (System.currentTimeMillis() - timeBefore) + "ms. Instance " + instance + " from page " + fromPage);
    }

    public void doFilterImpl(ServletRequest servletRequest, ServletResponse servletResponse,
                             FilterChain filterChain) throws IOException, ServletException {

        final HttpServletRequest httpRequest = (HttpServletRequest) servletRequest;
        BufferedRequestWrapper bufferedRequest = new BufferedRequestWrapper(httpRequest);

        // String requestKey = new String(bufferedRequest.getBuffer());
        // String requestKey = new HashMap(httpRequest.getParameterMap()).toString();
        String requestKey = toRequestKey(servletRequest.getParameterMap());
        TickingVolatileCache rememberedResponse = TickingVolatileCache.getItemInMemory(requestKey);

        if (servletRequest.getParameterMap().containsKey("skip-cache")) {
            filterChain.doFilter(servletRequest, servletResponse);
            TickingVolatileCache.remove(requestKey);
            return;
        }

        if (rememberedResponse != null) {
            ServletOutputStream os = servletResponse.getOutputStream();
            os.write(rememberedResponse.getResponse());
            os.close();
            return;
        }

        if (dumpRequest) {
            System.out.println("REQUEST -> " + requestKey);
        }

        final HttpServletResponse response = (HttpServletResponse) servletResponse;

        final ByteArrayPrintWriter pw = new ByteArrayPrintWriter();
        HttpServletResponse wrappedResp = new HttpServletResponseWrapper(response) {
            public PrintWriter getWriter() {
                return pw.getWriter();
            }

            public ServletOutputStream getOutputStream() {
                return pw.getStream();
            }

        };

        filterChain.doFilter(bufferedRequest, wrappedResp);

        byte[] bytes = pw.toByteArray();
        response.getOutputStream().write(bytes);
        if (dumpResponse) System.out.println("RESPONSE -> " + new String(bytes));
        TickingVolatileCache.store(requestKey, bytes);
    }

    public void destroy() {
    }

}