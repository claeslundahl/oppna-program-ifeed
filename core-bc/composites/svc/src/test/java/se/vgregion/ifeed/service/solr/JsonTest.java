package se.vgregion.ifeed.service.solr;

import org.junit.Test;

import java.util.Arrays;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Created by clalu4 on 2017-01-10.
 */
public class JsonTest {

    @Test
    public void parse() throws Exception {
        Object result = Json.parse("{\"message\":\"hej\"}");
        assertTrue(result instanceof Map);

        // result = Json.parse("[1,2,3,4]");
        // assertEquals(result, Arrays.asList(1, 2, 3, 4));
    }

}