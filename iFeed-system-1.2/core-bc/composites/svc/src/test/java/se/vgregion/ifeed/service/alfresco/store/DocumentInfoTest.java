package se.vgregion.ifeed.service.alfresco.store;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.mockito.internal.util.reflection.FieldSetter;

public class DocumentInfoTest {

    DocumentInfo info;

    String vgrNs = DocumentInfo.VGR_NAMESPACE;
    String dcNs = DocumentInfo.DC_NAMESPACE;

    @Before
    public void setUp() throws Exception {

    }

    private Map<String, Object> populateWithMetadata(DocumentInfo info) {
        Map<String, Object> metadata = new HashMap<String, Object>();
        metadata.put(vgrNs + dcNs + "key1", "value1");
        metadata.put(vgrNs + dcNs + "key2", "value2");
        metadata.put("key3", "value3");
        metadata.put(vgrNs + "key3", "value3");
        metadata.put(dcNs + "key3", "value3");
        info.setMetadata(metadata);

        return metadata;
    }

    @Test
    public void setMetadata() {
        info = new DocumentInfo();
        populateWithMetadata(info);

        Map<String, Object> result = info.getMetadata();
        Assert.assertTrue(result.containsKey(dcNs + "key1"));
        Assert.assertTrue(result.containsKey(dcNs + "key2"));
        Assert.assertFalse(result.containsKey(dcNs + "key3"));
    }

    @Test
    public void testToString() throws NoSuchFieldException, SecurityException {
        info = new DocumentInfo();
        Class<DocumentInfo> clazz = DocumentInfo.class;

        Field availableFrom = clazz.getDeclaredField("availableFrom");
        FieldSetter fs = new FieldSetter(info, availableFrom);
        fs.set(100d);

        populateWithMetadata(info);

        String result = info.toString();

        Assert.assertTrue(result.contains(dcNs + "key1"));
        Assert.assertTrue(result.contains("value1"));
    }
}
