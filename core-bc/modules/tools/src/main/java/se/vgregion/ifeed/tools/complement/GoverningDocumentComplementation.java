package se.vgregion.ifeed.tools.complement;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.commons.lang.mutable.MutableBoolean;
import se.vgregion.common.utils.MultiMap;
import se.vgregion.ifeed.tools.*;

import java.util.*;
import java.util.stream.Collectors;

public class GoverningDocumentComplementation {

    private static Gson gson = new GsonBuilder().setPrettyPrinting().create();

    private final DatabaseApi database;

    private List<FieldInf> replacements;

    private List<Mapper> mappers;

    @Deprecated // The logic should be handled with condition in the three of field_inf:s.
    private final List<Filter> defaultFiltersToAdd = new ArrayList<>();

    public GoverningDocumentComplementation(DatabaseApi database) {

        this.database = database;
        getReplacements();

/*        defaultFiltersToAdd.addAll(Arrays.asList(new Filter(
                        Map.of(
                                "filterkey", "vgrsd:DomainExtension.domain",
                                "filterquery", "Styrande dokument",
                                "operator", "matching"
                        ),
                        getOrCreateFromOrInDatabase(new FieldInf("vgrsd:DomainExtension.domain", "Domän Styrande dokument", null))
                )
        ));*/
        getOrCreateFromOrInDatabase(new FieldInf("vgrsd:DomainExtension.domain", "Domän Styrande dokument", null));
    }

    private FieldInf getFieldInf(Long withParentPk, String andName) {
        String sql = "select * from field_inf fi where name = ? and "
                + (withParentPk == null ? "parent_pk is null" : "parent_pk = ?");
        List<Tuple> hits = null;
        if (withParentPk == null) {
            hits = database.query(sql, andName);
        } else {
            hits = database.query(sql, andName, withParentPk);
        }
        if (hits.isEmpty()) {
            return null;
        }
        return FieldInf.toFieldInf(hits.get(0));
    }

    public List<FieldInf> getReplacements() {
        if (replacements == null) {
            FieldInf root = getFieldInf(null, "Gömda");
            if (root == null) {
                root = FieldInf.toFieldInf(new Tuple(Map.of(
                        "pk", SequenceUtil.getNextHibernateSequeceValue(database),
                        "name", "Gömda")));
                database.insert("field_inf", root);
            }
            FieldInf branch = getFieldInf((Long) root.get("pk"), "Styrande dokument");
            if (branch == null) {
                branch = FieldInf.toFieldInf(new Tuple(Map.of(
                        "pk", SequenceUtil.getNextHibernateSequeceValue(database),
                        "name", "Styrande dokument",
                        "parent_pk", root.get("pk"))));
                database.insert("field_inf", branch);
            }
            branch.load(database);
            if (branch.getChildren().isEmpty()) {
                FieldInf leaf = new FieldInf();
                leaf.put("in_html_view", false);
                leaf.put("level", 2);
                leaf.put("in_tooltip", false);
                leaf.put("type", "d:text_fix");
                leaf.put("filter", false);
                leaf.put("expanded", false);
                leaf.put("parent_pk", branch.get("pk"));
                leaf.put("name", "Titel");
                leaf.put("pk", SequenceUtil.getNextHibernateSequeceValue(database));
                leaf.put("id", "vgr:VgrExtension.vgr:Title");
                leaf.put("position", 135);
                database.insert("field_inf", leaf);
                branch.getChildren().add(leaf);
                // branch.load(database);
            }
            if (branch.getDefaultFilters().isEmpty()) {
                DefaultFilter df = new DefaultFilter(
                        Map.of("id", SequenceUtil.getNextHibernateSequeceValue(database),
                                "field_inf_pk", branch.get("pk"),
                                "filterkey", "vgrsd:DomainExtension.domain",
                                "filterquery", "Styrande dokument")
                );
                database.insert("default_filter", df);
                branch.getDefaultFilters().add(df);
            }
            replacements = branch.getChildren();
        }
        return replacements;
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
                            new RecordsCreatorMapper(new HashSet<>(Arrays.asList("dc.creator.recordscreator", "dc.creator.recordscreator.id")), getOrCreateFromOrInDatabase(new FieldInf("core:ArchivalObject.core:Producer.id", "Myndighet", null))),
                            new Mapper("dc.subject.keywords", getOrCreateFromOrInDatabase(new FieldInf("vgrsd:DomainExtension.vgrsd:CodeGroup.vgrsd:Code.path", "Kodverk (SweMeSH)", "SweMeSH/"))),
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
                            new Mapper("dc.creator.forunit.id", getOrCreateFromOrInDatabase(new FieldInf("vgr:VgrExtension.vgr:CreatedByUnit.id", "Upprättat av enhet", null, "d:ldap_org_value"))),
                            new TermsAudienceMapper("dcterms.audience",
                                    getOrCreateFromOrInDatabase(new FieldInf("vgrsd:DomainExtension.vgrsd:CodeGroup.vgrsd:Code.path", "HosPersKat", "HosPersKat/")),
                                    getOrCreateFromOrInDatabase(new FieldInf("vgrsd:DomainExtension.vgrsd:CodeGroup.vgrsd:Code.path", "Legitimerade yrken", "Legitimerade yrken/")),
                                    getOrCreateFromOrInDatabase(new FieldInf("vgrsd:DomainExtension.vgrsd:CodeGroup.vgrsd:Code.path", "Specialistutbildningar", "Specialistutbildningar/"))),
                            new Mapper("dc.date.validto", getOrCreateFromOrInDatabase(new FieldInf("vgrsd:DomainExtension.vgrsd:ValidTo", "Giltighetsdatum tom", null, "d:date")))));

        }

        return mappers;
    }

    FieldInf getOrCreateFromOrInDatabase(FieldInf fi) {
        for (FieldInf replacement : replacements) {
            if (equals(fi.get("id"), replacement.get("id")) && equals(fi.get("query_prefix"), replacement.get("query_prefix"))) {
                return replacement;
            }
        }
        FieldInf firstAsSample = new FieldInf(replacements.get(0));
        firstAsSample.putAll(fi);
        firstAsSample.remove("apelon_id");
        firstAsSample.put("pk", SequenceUtil.getNextHibernateSequeceValue(database));
        database.insert("field_inf", firstAsSample);
        System.out.println(gson.toJson(firstAsSample));
        replacements.add(firstAsSample);
        return firstAsSample;
    }

    public Feed clone(Feed feed, String withNewUserId) {
        Feed result = new Feed(feed);
        result.fill(database);
        result.getComposites().clear();
        result.getPartOf().clear();
        result.put("userid", withNewUserId);
        result.put("name", result.get("name") + "(sty. dok. komplement)");
        return result;
    }

    public Feed getFeed(Number withId) {
        List<Tuple> items = database.query("select * from vgr_ifeed where id = ?", withId);
        if (items.isEmpty()) return null;
        return Feed.toFeed(items.get(0));
    }

    public Feed makeComplement(Feed that) {
        if (that == null) {
            System.out.println(" Hittades inte!");
            return null;
        }
        Feed existing = getFeed(((Long) that.get("id")) * -1);
        if (existing != null) {
            existing.fill(database);
            existing.delete(database);
        }
        Feed result = toCompletingFeed(that);
        final MutableBoolean foundAnyFilterWithField = new MutableBoolean(false);
        result.getFilters().forEach(root -> {
            root.visit(f -> {
                if (f.get("filterkey") != null && !f.get("filterkey").toString().trim().equals("")) {
                    foundAnyFilterWithField.setValue(true);
                }
            });
        });
        if (!foundAnyFilterWithField.booleanValue()) {
            return null;
        }
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

    public List<Filter> addDefaultFilters(Feed toThat) {
        List<Filter> result = new ArrayList<>();
        for (Filter filter : defaultFiltersToAdd) {
            Filter clone = new Filter(filter);
            clone.setFieldInf(filter.getFieldInf());
            clone.put("field_inf_pk", filter.getFieldInf().get("pk"));
            clone.put("ifeed_id", toThat.get("id"));
            toThat.getFilters().add(clone);
            result.add(clone);
        }
        return result;
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
        new ArrayList<>(inHere.getFilters()).forEach(filter -> {
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
                inHere.clear();
                inHere.setFieldInf(nv.getFieldInf());
                inHere.putAll(nv);
                inHere.getChildren().addAll(nv.getChildren());
                return;
            }
        }
        // throw new RuntimeException("Did not find mapper for: \n" + gson.toJson(inHere));
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
            System.out.println(local.getUrl());
            gdc.makeComplement(450807771L);
            //if (true) return;
            gdc.commit();
        } catch (Exception e) {
            gdc.rollback();
            throw new RuntimeException(e);
        }
    }

    public void rollback() {
        database.rollback();
    }

    public void commit() {
        SequenceUtil.checkAndOrFixHibernateIndex(database);
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

    public List<Filter> getDefaultFiltersToAdd() {
        return defaultFiltersToAdd;
    }

}
