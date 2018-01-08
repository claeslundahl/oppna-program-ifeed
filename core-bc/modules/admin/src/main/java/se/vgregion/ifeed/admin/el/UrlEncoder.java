package se.vgregion.ifeed.admin.el;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * @author Patrik Bergström
 */
public class UrlEncoder {

    public static String urlEncode(String url) throws UnsupportedEncodingException {
        return URLEncoder.encode(url, "UTF-8");
    }
}
