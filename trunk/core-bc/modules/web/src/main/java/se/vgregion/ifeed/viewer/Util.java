package se.vgregion.ifeed.viewer;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class Util {

	public static String urlEncode(String value) throws UnsupportedEncodingException {
		return URLEncoder.encode(value, "UTF-8");
	}

}
