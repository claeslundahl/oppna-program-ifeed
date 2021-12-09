package se.vgregion.ifeed.tools.complement;

import se.vgregion.ifeed.tools.*;

import java.util.List;

public class Hack {

    public static void main(String[] args) {
        DatabaseApi database = DatabaseApi.getLocalApi();
        System.out.println(database.getUrl());
        try {
            GoverningDocumentComplementation gdc = new GoverningDocumentComplementation(database);
            List<Tuple> items = database.query("select * from vgr_ifeed vi where vi.id < 0 and vi.name like ?", new Object[]{"%(sty. dok. komplement)"});
            System.out.println("Hittade " + items.size());
            for (Tuple item : items) {
                Feed feed = Feed.toFeed(item);
                feed.fill(database);
                List<Filter> nfs = gdc.addDefaultFilters(feed);
                for (Filter nf : nfs) {
                    nf.put("id", SequenceUtil.getNextHibernateSequeceValue(database));
                    nf.insert(database, feed, null);
                }
                System.out.println(item);
                // if (true) throw new RuntimeException();
            }
        } catch (Exception e) {
            database.rollback();
            e.printStackTrace();
        }
        database.commit();
    }

}
