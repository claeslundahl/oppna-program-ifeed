package se.vgregion.varnish;

import org.codehaus.jackson.map.ObjectMapper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Created by clalu4 on 2017-04-07.
 */
public class VarnishClientTest {

    public static void main(String[] args) throws IOException {

        /*String json = toText(new URL("http://ifeed.vgregion.se/iFeed-web/documentlists/36260/metadata.json?by=&dir=asc"));
        ArrayList<HashMap<String, Object>> result =
                new ObjectMapper().readValue(json, ArrayList.class);

        for (HashMap<String, Object> map : result) {
            System.out.println(map);
            String id = (String) map.get("id");
            String[] idFrags = id.split(Pattern.quote(":"));
            String uuid = idFrags[idFrags.length - 1];
            System.out.println(uuid);
        }*/

        //http://ifeed.vgregion.se/iFeed-web/documents/dfc6c32e-63b8-4a2b-b18d-683704f58139/metadata
        //https://ifeed-stage.vgregion.se/iFeed-web/documentlists/91344/?by=&dir=asc

        //System.out.println(result);

        VarnishClient client = VarnishClient.newVarnishClient();
        // client.clear("https://ifeed-stage.vgregion.se/iFeed-web/documentlists/91344/?by=&dir=asc");
        client.clear("ifeed-stage.vgregion.se/iFeed-web/documentlists/91344/?by=&dir=asc");

    }

    static String toText(URL url) {
        try {
            URLConnection conn = url.openConnection();
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8))) {
                return reader.lines().collect(Collectors.joining("\n"));
            }
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

}