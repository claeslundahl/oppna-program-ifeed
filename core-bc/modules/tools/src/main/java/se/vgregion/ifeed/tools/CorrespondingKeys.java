package se.vgregion.ifeed.tools;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class CorrespondingKeys {

    final public Set<String> fromThese = new HashSet<>();

    final public String newKey;

    CorrespondingKeys(String newKey, String... fromThese) {
        this.newKey = newKey;
        this.fromThese.addAll(
                Arrays.asList(fromThese)
        );
    }

}
