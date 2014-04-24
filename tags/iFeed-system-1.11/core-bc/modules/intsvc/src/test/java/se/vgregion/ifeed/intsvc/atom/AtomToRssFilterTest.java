package se.vgregion.ifeed.intsvc.atom;

import org.junit.Test;

import java.io.*;
import java.net.URL;

/**
 * Created with IntelliJ IDEA.
 * User: portaldev
 * Date: 2013-04-18
 * Time: 07:07
 * To change this template use File | Settings | File Templates.
 */
public class AtomToRssFilterTest {

    @Test
    public void toRss() throws IOException {
        URL url = this.getClass().getResource("/sample-atom-feed.txt");
        AtomToRssFilter atrf = new AtomToRssFilter();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        InputStream is = url.openStream();
        int b = is.read();
        while (b!=-1) {
            baos.write(b);
            b = is.read();
        }

        OutputStream out = new ByteArrayOutputStream();
        atrf.toRss(baos.toByteArray(), out);
    }

}
