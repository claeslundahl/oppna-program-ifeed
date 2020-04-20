package se.vgregion.ifeed.tools;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;
import java.util.Map;

public class Urlz {

    public String read(String fromThatUrl) {
        return urlConnectionImpl(fromThatUrl);
    }

    private String openStreamImpl(String fromThatUrl) {
        try (
                InputStream os = new URL(fromThatUrl).openStream();
                InputStreamReader is = new InputStreamReader(os);
                BufferedReader in = new BufferedReader(is);
        ) {
            StringBuilder sb = new StringBuilder();
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                sb.append(inputLine);
                sb.append('\n');
            }
            return sb.toString();
        } catch (IOException ioe) {
            throw new RuntimeException(ioe);
        }
    }

    private String urlConnectionImpl(String url) {
        try {
            URL urlObj = new URL(url);
            URLConnection con = urlObj.openConnection();
            con.setConnectTimeout(1000);
            con.setReadTimeout(2000);
            // con.setRequestProperty("Connection", "close");
            con.connect();
            try (BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()))) {
                StringBuilder sb = new StringBuilder();
                String inputLine;
                while ((inputLine = in.readLine()) != null) {
                    sb.append(inputLine);
                    sb.append('\n');
                }
                return sb.toString();
            }
        } catch (IOException ioe) {
            throw new RuntimeException(ioe);
        }
    }

    public static String toCurlRequest(HttpURLConnection connection, byte[] body) {
        StringBuilder builder = new StringBuilder("curl -v ");

        // Method
        builder.append("-X ").append(connection.getRequestMethod()).append(" \\\n  ");

        // Headers
        for (Map.Entry<String, List<String>> entry : connection.getRequestProperties().entrySet()) {
            builder.append("-H \"").append(entry.getKey()).append(":");
            for (String value : entry.getValue())
                builder.append(" ").append(value);
            builder.append("\" \\\n  ");
        }

        // Body
        if (body != null)
            builder.append("-d '").append(new String(body)).append("' \\\n  ");

        // URL
        builder.append("\"").append(connection.getURL()).append("\"");

        return builder.toString();
    }

}
