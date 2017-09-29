package se.vgregion.ifeed.webcomp.filter;

import se.vgregion.varnish.VarnishClient;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;
import java.util.Map;

/**
 * Created by clalu4 on 2015-05-18.
 * Should be a proxy to the Varnish server, to get it to not cache an url.
 */
public class UnchaceJson extends HttpServlet {

    VarnishClient varnishClient = VarnishClient.newVarnishClient();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String uri = request.getRequestURI() +
                (request.getQueryString() != null ? "?" + request.getQueryString() : "");

        String marker = "/iFeed-portlet/uncache/";
        uri = uri.substring(uri.indexOf(marker) + marker.length());

        varnishClient.clear(uri);

        URL url = new URL(uri);
        URLConnection connection = url.openConnection();
        connection.setDoInput(true);
        connection.setDoOutput(true);

        try (InputStream in = connection.getInputStream();
             OutputStream os = response.getOutputStream()) {
            for (Map.Entry<String, List<String>> header : connection.getHeaderFields().entrySet()) {
                for (String value : header.getValue()) {
                    response.setHeader(header.getKey(), value);
                }
            }
            response.setHeader("Content-Type", "application/javascript");
            copy(in, os);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request,
                          HttpServletResponse response) throws ServletException, IOException {
        StringBuffer jb = new StringBuffer();

        URL oracle = new URL("http://www.anotherserver.com/");

        HttpURLConnection connection = null;
        connection = (HttpURLConnection) oracle.openConnection();
        connection.setRequestMethod("POST");
        connection.setDoOutput(true);
        connection.setDoInput(true);

        OutputStream wr = connection.getOutputStream();

        InputStream in = request.getInputStream();
        byte[] buffer = new byte[512];
        int read = in.read(buffer, 0, buffer.length);
        while (read >= 0) {
            wr.write(buffer, 0, read);
            read = in.read(buffer, 0, buffer.length);
        }

        wr.flush();
        wr.close();


        BufferedReader in1 = new BufferedReader(new InputStreamReader(
                connection.getInputStream()));
        String inputLine;
        while ((inputLine = in1.readLine()) != null) {
            jb.append(inputLine);
        }

        response.setContentType("text/html");
        // Get the printwriter object from response to write the required json
        // object to the output stream
        PrintWriter out = response.getWriter();
        // Assuming your json object is **jsonObject**, perform the following,
        // it will return your json object
        out.print(jb.toString());
        out.flush();
        in1.close();
    }

    /**
     * Reads all bytes from an input stream and writes them to an output stream.
     */
    private static long copy(InputStream source, OutputStream sink)
            throws IOException {
        long nread = 0L;
        byte[] buf = new byte[8192];
        int n;
        while ((n = source.read(buf)) > 0) {
            sink.write(buf, 0, n);
            nread += n;
        }
        return nread;
    }

}
