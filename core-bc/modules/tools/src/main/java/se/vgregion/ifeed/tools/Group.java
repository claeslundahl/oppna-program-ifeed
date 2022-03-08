package se.vgregion.ifeed.tools;

import java.util.ArrayList;
import java.util.List;

public class Group extends Tuple {

    public static List<? extends Group> toGroups(List<Tuple> findings) {
        List<Group> result = new ArrayList<>();
        for (Tuple finding : findings) {
            result.add(toGroup(finding));
        }
        return result;
    }

    public static Group toGroup(Tuple tuple) {
        Group result = new Group();
        result.putAll(tuple);
        return result;
    }

}
