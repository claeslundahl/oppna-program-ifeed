package se.vgregion.ifeed.intsvc.atom;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.vgregion.ifeed.webcomp.filter.GenericResponseWrapper;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.transform.*;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.io.*;
import java.net.URL;
import java.util.Map;

public class AtomToRssFilter implements Filter {

    private static final Logger log = LoggerFactory.getLogger(AtomToRssFilter.class);

    private Transformer transformer = createTransformer();

    @Override
    public void init(FilterConfig fConfig) throws ServletException {
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException,
            ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        @SuppressWarnings("unchecked")
        Map<String, String[]> parms = httpRequest.getParameterMap();

        if (parms.containsKey("type")) {
            OutputStream out = httpResponse.getOutputStream();
            GenericResponseWrapper wrapper = new GenericResponseWrapper(httpResponse);
            chain.doFilter(request, wrapper);
            toRss(wrapper.getData(), out);
            wrapper.setContentType("application/rss+xml;charset=UTF-8");
            out.close();
        } else {
            chain.doFilter(request, response);
        }
    }

    @Override
    public void destroy() {
    }


    void toRss(byte[] atomBytes, OutputStream out) {
        try {
            toRssImpl(atomBytes, out);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    void toRssImpl(byte[] atomBytes, OutputStream out) throws IOException, TransformerException {
        Source text = new StreamSource(new ByteArrayInputStream(atomBytes));
        transformer.transform(text, new StreamResult(out));
    }

    Transformer createTransformer() {
        try {
            return createTransformerImpl();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    Transformer createTransformerImpl() throws IOException, TransformerConfigurationException {
        TransformerFactory factory = TransformerFactory.newInstance();
        URL url = this.getClass().getResource("/atom2rss-exslt.xsl");
        Source xslt = new StreamSource(url.openStream());
        Transformer transformer = factory.newTransformer(xslt);
        return transformer;
    }

}
