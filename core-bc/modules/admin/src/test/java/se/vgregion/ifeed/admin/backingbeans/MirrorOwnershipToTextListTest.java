package se.vgregion.ifeed.admin.backingbeans;

import junit.framework.TestCase;
import org.junit.Test;
import se.vgregion.ifeed.types.Ownership;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

public class MirrorOwnershipToTextListTest extends TestCase {

    @Test
    public void testScenario() {
        Collection<Ownership> ownerships = new HashSet<Ownership>();

        for (int i = 0; i < 10; i++) {
            Ownership o = new Ownership();
            o.setUserId("id-" + i);
            ownerships.add(o);
        }

        List<String> newOwnershipNames = new MirrorOwnershipToTextList(ownerships);

        int i = 0;
        for (Ownership o : ownerships) {
            assertEquals(o.getUserId(), newOwnershipNames.get(i));
            i++;
        }

        ArrayList<Ownership> list = new ArrayList<Ownership>();
        newOwnershipNames = new MirrorOwnershipToTextList(list);
        newOwnershipNames.add("foo");
        assertEquals(1, list.size());
    }

}