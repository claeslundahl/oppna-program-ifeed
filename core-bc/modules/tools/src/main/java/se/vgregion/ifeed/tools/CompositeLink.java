package se.vgregion.ifeed.tools;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public class CompositeLink extends Tuple {

    public CompositeLink() {
        super();
    }

    public CompositeLink(Map<String, Object> item) {
        super();
        putAll(item);
    }

    public CompositeLink(Long partof_id, Long composites_id) {
        put("partof_id", partof_id);
        put("composites_id", composites_id);
    }

    public static Collection<CompositeLink> toCompositeLinks(List<Tuple> findings) {
        Collection<CompositeLink> result = new ArrayList<>();
        for (Tuple finding : findings) {
            result.add(new CompositeLink(finding));
        }
        return result;
    }


    public void insert(DatabaseApi database) {
        // Fixa härnäst så att man får en bidning till den gamla. vgr_ifeed_vgr_ifeed. partof_id är nyckeln för det gamla flödet.
        // composites_id är för den nya (den med minus-id).
        List<Tuple> hits = database.query(
                "select * from vgr_ifeed_vgr_ifeed where partof_id = ? and composites_id = ?",
                get("partof_id"), get("composites_id"));
        if (hits.size() > 0)
            throw new RuntimeException();
        database.insert("vgr_ifeed_vgr_ifeed", this);
    }

    public void delete(DatabaseApi fromHere) {
        fromHere.update(
                "delete from vgr_ifeed_vgr_ifeed where partof_id = ? and composites_id = ?",
                get("partof_id"), get("composites_id")
        );
    }

}
