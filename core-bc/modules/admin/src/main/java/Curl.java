import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by clalu4 on 2016-03-23.
 */
public class Curl {

    public static void main(String[] arg) throws IOException {
        URL url = new URL(arg[0]);
        InputStream is = url.openStream();
        int ptr = 0;
        StringBuffer buffer = new StringBuffer();
        while ((ptr = is.read()) != -1) {
            buffer.append((char)ptr);
        }
        System.out.println(buffer);
    }

}
