package se.vgregion.ifeed.viewer;

import org.junit.Test;

public class LockdownFilterTest {

    @Test
    public void getBlockWebWithErrorFromConfiguration() {
        boolean result = LockdownFilter.getBlockWebWithErrorFromConfiguration();
        System.out.println(result);
    }

}