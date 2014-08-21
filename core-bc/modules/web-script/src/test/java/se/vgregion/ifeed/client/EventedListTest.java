package se.vgregion.ifeed.client;

import junit.framework.TestCase;

import java.util.ArrayList;
import java.util.List;

public class EventedListTest extends TestCase {

    public void testAddAll() throws Exception {
        EventedList<String> el = new EventedList<String>();
        List<String> strings = new ArrayList<String>();
        strings.add("a");
        strings.add("b");

        el.getAddSpies().add(new EventedList.Spy<String>() {
            @Override
            public void event(String item, int index) {
                System.out.println(item);
            }

            @Override
            public boolean isRemoveAble() {
                return false;
            }
        });

        el.addAll(strings);

    }
}