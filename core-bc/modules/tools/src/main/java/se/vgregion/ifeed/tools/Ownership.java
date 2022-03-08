package se.vgregion.ifeed.tools;

import java.util.ArrayList;
import java.util.List;

public class Ownership extends Tuple {

    public static List<? extends Ownership> toOwnerships(List<Tuple> findings) {
        List<Ownership> result = new ArrayList<>();
        for (Tuple finding : findings) {
            result.add(toOwnership(finding));
        }
        return result;
    }

    public static Ownership toOwnership(Tuple tuple) {
        Ownership result = new Ownership();
        result.putAll(tuple);
        return result;
    }

    public void delete(DatabaseApi fromHere) {
        fromHere.update("delete from vgr_ifeed_ownership where id = ?", get("id"));
    }

}
