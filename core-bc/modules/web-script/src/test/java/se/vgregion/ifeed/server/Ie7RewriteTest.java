package se.vgregion.ifeed.server;

import junit.framework.Assert;
import org.junit.Test;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Created by clalu4 on 2015-06-30.
 */
public class Ie7RewriteTest {

    @Test
    public void removeFinallyBlockFrom() throws IOException, URISyntaxException {
        Assert.assertNotNull("Test file missing", getClass().getResource("/laarge.js"));
        URL script = getClass().getResource("/laarge.js");
        Path p = Paths.get(script.toURI());
        String content = new String(Files.readAllBytes(p));
        String node = Ie7Rewrite.removeFinallyBlockFrom(content);
    }

}