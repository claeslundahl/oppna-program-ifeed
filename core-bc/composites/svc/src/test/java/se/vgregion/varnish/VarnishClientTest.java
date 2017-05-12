package se.vgregion.varnish;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
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
    // https://ifeed-stage.vgregion.se/iFeed-web/meta.json?instance=116106&f=dc.title&f=dc.date.issued&f=dc.title

    String url = "https://ifeed-stage.vgregion.se/iFeed-web/meta.json?instance=116106&f=dc.title&f=dc.date.issued&f=dc.title&callback=__gwt_jsonp__.P0.onSuccess";
    client.clearJsonImp(url);

    if (true) return;
    url = url.replace("://", "");
    url = url.substring(url.indexOf('/') + 1);
    System.out.println(url);

    //client.clear("ifeed-stage.vgregion.se/iFeed-web/documentlists/91344/?by=&dir=asc");
    //client.clear("ifeed-stage.vgregion.se/iFeed-web/documentlists/91344/?by=&dir=asc");

    client.clearJson("iFeed-web/meta.json?instance=116106&f=dc.title&f=dc.date.issued&f=dc.title");
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