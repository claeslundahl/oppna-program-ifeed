package se.vgregion.ifeed.tools.complement;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import se.vgregion.common.utils.MultiMap;
import se.vgregion.ifeed.service.solr.client.SolrHttpClient;
import se.vgregion.ifeed.tools.*;

import java.util.*;
import java.util.stream.Collectors;

public class GoverningDocumentComplementation {

    private static Gson gson = new GsonBuilder().setPrettyPrinting().create();

    private final DatabaseApi database;

    private final List<FieldInf> replacements;

    private List<Mapper> mappers;

    private final List<Filter> defaultFiltersToAdd = new ArrayList<>();

    public GoverningDocumentComplementation(DatabaseApi database) {
        this.database = database;
        String sql = "select leaf.*\n" +
                "from field_inf leaf\n" +
                "join field_inf branch on leaf.parent_pk = branch.pk\n" +
                "join field_inf trunk on branch.parent_pk = trunk.pk\n" +
                "where trunk.name = 'Gömda'\n" +
                "order by 1, 2, 3";

        List<Tuple> newFilters = database.query(sql);

        replacements = newFilters.stream().map(t -> FieldInf.toFieldInf(t)).collect(Collectors.toList());

        defaultFiltersToAdd.addAll(Arrays.asList(new Filter(
                        Map.of(
                                "filterkey", "vgrsd:DomainExtension.domain",
                                "filterquery", "Styrande dokument",
                                "operator", "matching"
                        ),
                        getOrCreateFromOrInDatabase(new FieldInf("vgrsd:DomainExtension.domain", "Dokumenttyp", null))
                )
        ));
    }

    private List<Mapper> getMappers() {
        if (mappers == null) {
            mappers = new ArrayList<>(
                    Arrays.asList(
                            new Mapper(new HashSet<>(Arrays.asList(
                                    "dc.publisher.project-assignment",
                                    "dc.creator.project-assignment",
                                    "dc.type.process.name",
                                    "dc.type.file.process",
                                    "dc.type.file",
                                    "dc.type.document.serie",
                                    "dc.type.document.id",
                                    "dc.type.document",
                                    "dc.type.record")),
                                    getOrCreateFromOrInDatabase(new FieldInf("vgrsy:DomainExtension.vgrsy:SubjectLocalClassification", "Egen ämnesindelning", null))),
                            new Mapper("dc.subject.authorkeywords", getOrCreateFromOrInDatabase(new FieldInf("vgr:VgrExtension.vgr:Tag", "Företagsnyckelord", null))),
                            new Mapper("dc.publisher.forunit.id", getOrCreateFromOrInDatabase(new FieldInf("vgr:VgrExtension.vgr:PublishedForUnit.id", "Upprättat för enhet", null))),
                            new Mapper("dc.creator.recordscreator.id", getOrCreateFromOrInDatabase(new FieldInf("core:ArchivalObject.core:Producer", "Myndighet", null))),
                            //new Mapper("dc.creator.recordscreator.id", new FieldInf("DIDL.Item.Descriptor.Statement.vgrsd:DomainExtension", "Domän Styrande dokument", null)),
                            new Mapper("dc.creator.recordscreator.id", getOrCreateFromOrInDatabase(new FieldInf("core:ArchivalObject.core:Producer", "Myndighet", null))),
                            new Mapper("dc.keywords", getOrCreateFromOrInDatabase(new FieldInf("vgrsd:DomainExtension.vgrsd:CodeGroup.vgrsd:Code.path", "Kodverk (SweMeSH)", "SweMeSH/"))),
                            new CoverageHsaCodeMapper("dc.coverage.hsacode", getOrCreateFromOrInDatabase(new FieldInf("vgrsd:DomainExtension.vgrsd:CodeGroup.vgrsd:Code.path", "Kodverk (Verksamhet)", "Verksamhetskod/"))),
                            // Kompletterande flöde - ersätt BariumID med Titel (för de dokument som fortfarande är publicerade,
                            // avpublicerade dokument följer inte med dvs det kan blir så att det inte blir lika många poster i det kompletternade flödet)
                            new DocumentIdMapper("dc.source.documentid", getOrCreateFromOrInDatabase(new FieldInf("vgr:VgrExtension.vgr:Title", "Titel", null))),
                            // vgrsd:DomainExtension.vgrsd:ValidityArea för rutin och riktlinje. Det här måste hanteras på icke-standard-vis.
                            //new Mapper("dc.type.document.structure", new FieldInf("core:ArchivalObject.core:ObjectType", "Handlingstyp", null)),
                            new DocumentStructureMapper(this),
                            new Mapper("dc.title", getOrCreateFromOrInDatabase(new FieldInf("vgr:VgrExtension.vgr:Title", "Titel", null))),
                            // 3 olika kodverk i SOFIA, i feed behöver kompletternade flödet ta hänsyn till detta, se flik "Målgrupp HOS" för mappningsregler.
                            //Kodverk Legitimerade yrken
                            //Kodverk Specialistutbildningar
                            //Kodverk HosPersKat
                            new TermsAudienceMapper("dcterms.audience",
                                    getOrCreateFromOrInDatabase(new FieldInf("vgrsd:DomainExtension.vgrsd:CodeGroup.vgrsd:Code.path", "Titel", null)),
                                    getOrCreateFromOrInDatabase(new FieldInf("vgrsd:DomainExtension.vgrsd:CodeGroup.vgrsd:Code.path", "Titel", null)),
                                    getOrCreateFromOrInDatabase(new FieldInf("vgrsd:DomainExtension.vgrsd:CodeGroup.vgrsd:Code.path", "Titel", null))),
                            new Mapper("dc.date.validto", getOrCreateFromOrInDatabase(new FieldInf("vgrsd:DomainExtension.vgrsd:ValidTo", "Giltighetsdatum tom", null))
                            )));
        }
        return mappers;
    }

    FieldInf getOrCreateFromOrInDatabase(FieldInf fi) {
        for (FieldInf replacement : replacements) {
            if (equals(fi.get("id"), replacement.get("id")) && equals(fi.get("query_prefix"), replacement.get("query_prefix"))) {
                return replacement;
            }
        }
        FieldInf firstAsSample = replacements.get(0);
        firstAsSample.remove("apelon_id");
        System.out.println(gson.toJson(firstAsSample));
        firstAsSample.put("pk", SequenceUtil.getNextHibernateSequeceValue(database));
        firstAsSample.put("name", fi.get("name"));
        firstAsSample.put("id", fi.get("id"));
        //throw new RuntimeException("Did not find hidden field inf in db for: \n" + fi.get("id"));
        database.insert("field_inf", firstAsSample);
        replacements.add(firstAsSample);
        return firstAsSample;
    }

    public Feed clone(Feed feed, String withNewUserId) {
        Feed result = new Feed(feed);
        result.fill(database);
        result.put("userid", withNewUserId);
        return result;
    }

    private Feed getFeed(Number withId) {
        List<Tuple> items = database.query("select * from vgr_ifeed where id = ?", withId);
        if (items.isEmpty()) return null;
        return Feed.toFeed(items.get(0));
    }

    public Feed makeComplement(Feed that) {
        Feed existing = getFeed(((Long) that.get("id")) * -1);
        if (existing != null) {
            existing.fill(database);
            existing.delete(database);
        }
        Feed result = toCompletingFeed(that);
        CompositeLink cl = new CompositeLink((Long) that.get("id"), (Long) result.get("id"));
        result.getComposites().add(cl);
        result.insert(database);
        return result;
    }

    public Feed makeComplement(Number to) {
        return makeComplement(getFeed(to));
    }

    public Feed toCompletingFeed(Feed fromThat) {
        final Feed result = clone(fromThat, "lifra1");
        removeAllFiltersThatHaveNoTranslation(result);
        moveAllSameFiltersUnderOrJunction(result);
        moveAllFiltersWhenMoreThanOneUnderAndJunction(result);
        replaceFilterKeysAndType(result);
        addDefaultFilters(result);
        removeOldIdentitiesAndAddMinusFeedId(result);
        return result;
    }

    private void addDefaultFilters(Feed toThat) {
        for (Filter filter : defaultFiltersToAdd) {
            Filter clone = new Filter(filter);
            clone.setFieldInf(filter.getFieldInf());
            clone.put("field_inf_pk", filter.getFieldInf().get("pk"));
            toThat.getFilters().add(clone);
        }
    }

    private void removeOldIdentitiesAndAddMinusFeedId(Feed result) {
        final Long ifeedId = ((Long) result.get("id")) * -1;
        result.put("id", ifeedId);
        result.getFilters().forEach(root -> {
            root.visit(f -> {
                f.remove("ifeed_id");
                f.remove("id");
            });
        });
        result.getFilters().forEach(f -> {
            f.put("ifeed_id", ifeedId);
            f.remove("parent_id");
        });
    }

    void replaceFilterKeysAndType(Feed inHere) {
        inHere.getFilters().forEach(filter -> {
            filter.visit(that -> {
                String fk = (String) that.get("filterkey");
                if (fk != null && !"".equals(fk.trim())) {
                    replaceFilterKeyAndType(inHere, that);
                }
            });
        });
    }

    void replaceFilterKeyAndType(Feed parent, Filter inHere) {
        for (Mapper mapper : getMappers()) {
            if (mapper.getFromKeys().contains(inHere.get("filterkey"))) {
                Filter nv = mapper.convert(inHere);
                if (nv == null) {
                    parent.getFilters().remove(inHere);
                    if (inHere.getParent() != null) {
                        inHere.getParent().getChildren().remove(inHere);
                    }
                    return;
                }
                inHere.putAll(nv);
                return;
            }
        }
        throw new RuntimeException("Did not find mapper for: \n" + gson.toJson(inHere));
    }

    public void moveAllFiltersWhenMoreThanOneUnderAndJunction(Feed inThat) {
        if (inThat.getFilters().size() > 1) {
            Filter and = new Filter();
            and.put("operator", "and");
            and.getChildren().addAll(inThat.getFilters());
            inThat.getFilters().clear();
            inThat.getFilters().add(and);
        }
    }

    public List<Feed> findFeedsWithRelevantFilters() {
        String sql = "select distinct vi.*, most_relevant_filters.find_count from vgr_ifeed vi,\n" +
                "(\n" +
                "select ifeed_id, count(distinct vif.ifeed_id || vif.filterkey) find_count\n" +
                "from vgr_ifeed_filter vif\n" +
                "where vif.filterkey in (\n %s" + " \n) " +
                "and vif.ifeed_id > 0\n" +
                "group by 1\n" +
                "order by 2 desc\n" +
                ") most_relevant_filters\n" +
                "where most_relevant_filters.ifeed_id = vi.id\n" +
                "order by most_relevant_filters.find_count desc";

        sql = String.format(sql, getMappers().stream().map(m -> m.getFromKeys().stream()
                .map(i -> String.format("'%s'", i)).collect(Collectors.joining(", "))).collect(Collectors.joining(", "))
        );

        List<Tuple> items = database.query(sql);
        items.forEach(i -> i.remove("find_count"));
        return Feed.toFeeds(items);
    }

    public static void main(String[] args) {
        DatabaseApi local = DatabaseApi.getLocalApi();
        GoverningDocumentComplementation gdc = new GoverningDocumentComplementation(local);

        try {
            Feed before = gdc.getFeed(4468522);
            before.fill(local);
            System.out.println(before.toText());
            System.out.println();
            Feed ni = gdc.makeComplement(4468522);
            System.out.println(ni.toText());
            gdc.commit();

            // SolrHttpClient client = SolrHttpClient.newInstanceFromConfig();
            /*for (String name : client.fetchAllFieldNames()) {
                System.out.println(name);
            };*/
            /*for (Mapper mapper : gdc.getMappers()) {
                mapper.getFromKeys().forEach(s -> {
                    NavigableSet<Object> values = client.findAllValues(s);
                    System.out.println(s + " = " + values.size());
                });
            }*/

        } catch (Exception e) {
            gdc.rollback();
            throw new RuntimeException(e);
        }
    }

    public void rollback() {
        database.rollback();
    }

    public void commit() {
        database.commit();
    }

    private void removeAllFiltersThatHaveNoTranslation(Feed inThat) {
        Set<String> allKeys = new HashSet<>();
        getMappers().forEach(m -> allKeys.addAll(m.getFromKeys()));

        List<Filter> tmp = new ArrayList<>(inThat.getFilters());
        inThat.getFilters().clear();

        for (Filter filter : tmp) {
            if (allKeys.contains(filter.get("filterkey"))) {
                inThat.getFilters().add(filter);
            }
        }
    }

    /**
     * If there are several filters with same key and query-prefix - then put them into an enclosing 'or' node.
     *
     * @param inThat An ordinary feed with 'flat' structure in its Filter:s. No nesting constructions there - but could
     *               be after the method is finished.
     */
    private void moveAllSameFiltersUnderOrJunction(Feed inThat) {
        MultiMap<String, Filter> byKey = new MultiMap<>();
        for (Filter filter : inThat.getFilters()) {
            byKey.get(filter.get("filterkey")).add(filter);
        }
        inThat.getFilters().clear();
        for (String key : byKey.keySet()) {
            List<Filter> filters = byKey.get(key);
            if (filters.size() > 1) {
                Filter or = new Filter();
                or.put("operator", "or");
                inThat.getFilters().add(or);
                for (Filter filter : filters) {
                    or.getChildren().add(filter);
                }
            } else {
                inThat.getFilters().add(filters.get(0));
            }
        }
    }

    static boolean equals(Object o1, Object o2) {
        if (o1 == o2) {
            return true;
        }
        if (o1 == null || o2 == null) {
            return false;
        }
        return o1.equals(o2);
    }

}
