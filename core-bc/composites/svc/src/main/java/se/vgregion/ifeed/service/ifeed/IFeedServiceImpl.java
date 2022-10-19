package se.vgregion.ifeed.service.ifeed;

import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import se.vgregion.common.utils.BeanMap;
import se.vgregion.common.utils.CommonUtils;
import se.vgregion.common.utils.Json;
import se.vgregion.dao.domain.patterns.repository.db.jpa.JpaRepository;
import se.vgregion.ifeed.service.solr.SolrFacetUtil;
import se.vgregion.ifeed.service.solr.client.SolrHttpClient;
import se.vgregion.ifeed.shared.ColumnDef;
import se.vgregion.ifeed.shared.DynamicTableDef;
import se.vgregion.ifeed.shared.DynamicTableSortingDef;
import se.vgregion.ifeed.types.*;
import se.vgregion.varnish.VarnishClient;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.io.Serializable;
import java.util.*;

@Service
public class IFeedServiceImpl implements IFeedService, Serializable {

    private static final Logger LOGGER = LoggerFactory.getLogger(VarnishClient.class);

    @PersistenceContext
    private EntityManager entityManager;

    private JpaRepository<IFeed, Long, Long> iFeedRepo;

    private ObjectRepo objectRepo;

    private JpaRepository<FieldsInf, Long, Long> fieldsInfRepo;

    private JpaRepository<VgrDepartment, Long, Long> departmentRepo;

    private String solrServiceUrl;
    private int latestFilterQueryTotalCount;

    public IFeedServiceImpl(final JpaRepository<IFeed, Long, Long> iFeedRepoParam,
                            JpaRepository<FieldsInf, Long, Long> fieldsInfRepo,
                            JpaRepository<VgrDepartment, Long, Long> departmentRepo) {
        iFeedRepo = iFeedRepoParam;
        this.fieldsInfRepo = fieldsInfRepo;
        this.departmentRepo = departmentRepo;
    }

    public IFeedServiceImpl() {

    }

    @Override
    public final List<IFeed> getIFeeds() {
        return new ArrayList<>(iFeedRepo.findByQuery("select distinct i from IFeed i left join fetch i.ownerships " +
                "left join fetch i.filters left join fetch i.department left join fetch i.group"));
    }

    @Override
    public List<VgrDepartment> getVgrDepartments() {
        Collection<VgrDepartment> result = departmentRepo.findByQuery("select distinct o from " + VgrDepartment.class.getSimpleName() + " o left join fetch o.vgrGroups g order by o.name, g.name");
        return new ArrayList<VgrDepartment>(result);
    }

    @Override
    public VgrDepartment getVgrDepartment(Long id) {
        return departmentRepo.findByPrimaryKey(id);
    }

    @Override
    @Transactional
    public VgrDepartment save(VgrDepartment department) {
        try {
            if (department.getId() == null) {
                VgrDepartment result = departmentRepo.persist(department);
                departmentRepo.flush();
                return result;
            }
            return departmentRepo.merge(department);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    @Override
    @Transactional
    public void delete(VgrGroup group) {
        objectRepo.delete(group);
        objectRepo.flush();
    }

    @Override
    @Transactional
    public void delete(Object group) {
        objectRepo.delete(group);
        objectRepo.flush();
    }

    @Override
    @Transactional
    public void delete(VgrDepartment department) {
        try {
            objectRepo.delete(department);
            objectRepo.flush();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    @Override
    @Transactional
    public List<IFeed> getUserIFeeds(final String userId) {
        ArrayList<IFeed> result = new ArrayList<IFeed>(
                iFeedRepo
                        .findByQuery(
                                "SELECT distinct ifeed FROM IFeed ifeed "
                                        + "left join fetch ifeed.ownerships "
                                        + " left join fetch ifeed.filters left join fetch ifeed.department left join fetch ifeed.group "
                                        + "WHERE ifeed.userId=?1 or ifeed.id in (select o.ifeedId from Ownership o where o.userId=?2)",
                                new Object[]{userId, userId}));

        initializedFeeds.set(null);
        // init(result);
        return result;
    }


    @Override
    @Transactional
    public IFeed getFeedForSolrQuery(final Long id) {
        IFeed result = getFeedForSolrQueryImpl(id, new HashSet<>());
        return result;
    }

    @Transactional
    protected IFeed getFeedForSolrQueryImpl(final Long id, Set<Long> traversal) {
        traversal.add(id);
        // long now = System.currentTimeMillis();
        ArrayList<IFeed> result = new ArrayList<IFeed>(
                iFeedRepo
                        .findByQuery(
                                "SELECT distinct ifeed " +
                                        "FROM IFeed ifeed " +
                                        "left join fetch ifeed.filters f "
                                        + "left join fetch ifeed.composites c "
                                        + "WHERE ifeed.id = ?1",
                                new Object[]{id}));

        if (result.isEmpty()) {
            return null;
        }

        IFeed ifeed = result.get(0);
        for (IFeed feed : ifeed.getComposites()) {

        }
        Json.toJson(ifeed);
        ifeed.toQuery(null);
        ifeed.getFilters().forEach(f -> {
            f.getMetadata();
            IFeedFilter p = f.getParent();
            if (p != null) {
                IFeedFilter pp = p.getParent();
                FieldInf fi = pp.getFieldInf();
                Set<DefaultFilter> dfs = fi.getDefaultFilters();
                if (dfs != null) {
                    for (DefaultFilter df : dfs) {
                        System.out.println(df);
                    }
                }
            }
        });
        entityManager.detach(ifeed);

        if (!ifeed.getComposites().isEmpty()) {
            List<IFeed> comps = new ArrayList<>();
            for (IFeed feed : ifeed.getComposites()) {
                if (!traversal.contains(feed.getId())) {
                    comps.add(getFeedForSolrQueryImpl(feed.getId(), traversal));
                }
            }
            ifeed.getComposites().clear();
            ifeed.getComposites().addAll(comps);
        }

        // entityManager.detach(ifeed);
        return ifeed;
    }

    @Override
    @Transactional
    public List<IFeed> getIFeedsByFilter(Filter filter, int start, int end) {
        long now = System.currentTimeMillis();
        List<Object> values = new ArrayList<Object>();
        String jpql = filter.toJpqlQuery(values);

        Query query = entityManager.createQuery(jpql);
        Object[] args = values.toArray();
        if (args != null) {
            for (int i = 0; i < args.length; i++) {
                query.setParameter(i + 1, args[i]);
            }
        }

        query.setFirstResult(start);
        query.setMaxResults(end - start);

        List<IFeed> result = query.getResultList();

        long countResult = findTotalCount(jpql.replace(" fetch ", " "), args);
        latestFilterQueryTotalCount = (int) countResult;
        ArrayList<IFeed> rv = new ArrayList<IFeed>(result);
        initializedFeeds.set(null);
        /*for (IFeed feed : rv) {
            init(feed);
        }*/
        initializedFeeds.set(null);
        now = System.currentTimeMillis() - now;

        return rv;
    }

    private long findTotalCount(String jpql, Object[] args) {

        // Not a very robust solution but we happen to know that the query always ends with an "order by" clause.
        jpql = jpql.replaceAll("order.*", ""); // Remove everything after and including "order".

        Query queryTotal = entityManager.createQuery("select count(o) from IFeed o where o in (" + jpql + ")");
        if (args != null) {
            for (int i = 0; i < args.length; i++) {
                queryTotal.setParameter(i + 1, args[i]);
            }
        }

        return (long) (Long) queryTotal.getSingleResult();
    }

    @Override
    public int getLatestFilterQueryTotalCount() {
        return latestFilterQueryTotalCount;
    }

    private SolrHttpClient client = SolrHttpClient.newInstanceFromConfig();

    @Override
    @Transactional
    public IFeed getIFeed(final Long id) {
        if (iFeedRepo == null) {
            throw new RuntimeException("iFeedRepo is not initialized");
        }
        if (id == null) {
            throw new RuntimeException("Id of feed cannot be null.");
        }

        /*Collection<IFeed> result =
            iFeedRepo .findByQuery(
                "SELECT distinct ifeed " +
                    "FROM IFeed ifeed " +
                    "left join fetch ifeed.filters f "
                    + "left join fetch ifeed.composites c "
                    + "left join fetch ifeed.dynamicTableDefs dtd "
                    + "left join fetch ifeed.dynamicTableDefs dtd "
                    + "WHERE ifeed.id = ?1",
                new Object[]{id});*/

        // IFeed result = iFeedRepo.find(id);

        IFeed result = objectRepo.findByPrimaryKey(IFeed.class, id);
        String s = result.toQuery(client.fetchFields());
        System.out.println(s);
        for (IFeedFilter filter : result.getFilters()) {
            init(filter);
        }
        if (result == null) {
            throw new RuntimeException();
        }
        initializedFeeds.set(null);
        for (DynamicTableDef dtd : result.getDynamicTableDefs()) {
            dtd.getExtraSorting();
        }
        result.getFilters().forEach(f -> System.out.println(f.getMetadata()));
        init(result);

        initializedFeeds.set(null);

        result.toQuery(null);

        return result;
    }

    private void init(IFeedFilter filter) {
        init(filter.getFieldInf());
        for (IFeedFilter child : filter.getChildren()) {
            init(child);
        }
    }

    private void init(FieldInf fieldInf) {
        if (fieldInf != null) {
            init(fieldInf.getParent());
        }
    }

    @Transactional
    public void init(Collection<IFeed> result) {
        if (result == null) {
            return;
        }
        for (IFeed item : result) {
            init(item);
        }
    }

    private static ThreadLocal<Set<IFeed>> initializedFeeds = new ThreadLocal<>();

    @Transactional
    protected void init(IFeed result) {
        try {
            initImp(result);
        } catch (Exception e) {
            LOGGER.error("Problem with result.id = " + result.getId());
            throw new RuntimeException(e);
        }
    }

    private void initImp(IFeed result) {
        if (result == null) {
            throw new NullPointerException("An ifeed must be found to");
        }

        if (initializedFeeds.get() == null) {
            initializedFeeds.set(new HashSet<>());
        }
        if (initializedFeeds.get().contains(result)) {
            return;
        }

        initializedFeeds.get().add(result);

        List<DynamicTableDef> dynamicTableDefs = result.getDynamicTableDefs();
        if (dynamicTableDefs != null) {
            for (DynamicTableDef dynamicTable : dynamicTableDefs) {
                if (dynamicTable.getColumnDefs() != null) {
                    for (ColumnDef columnDef : dynamicTable.getColumnDefs()) {
                        columnDef.toString();
                    }
                }
                if (dynamicTable.getExtraSorting() != null) {
                    for (DynamicTableSortingDef dynamicTableSortingDef : dynamicTable.getExtraSorting()) {
                        dynamicTableSortingDef.toString();
                    }
                }
            }
        }

        for (IFeedFilter filter : result.getFilters()) {
            BeanMap bm = new BeanMap(filter);
            for (String key : bm.keySet()) {
                bm.get(key);
                filter.getMetadata();
            }
        }

        if (result.getDepartment() != null) {
            // TODO: Think about this!
            /*for (VgrGroup group : result.getDepartment().getVgrGroups()) {
                for (IFeed otherFeed : group.getMemberFeeds()) {
                    init(otherFeed);
                }
            }*/
        }

        result.getComposites().toString();
        result.getPartOf().toString();
        for (IFeed feed : result.getPartOf()) {
            init(feed);
        }
        for (IFeed feed : result.getComposites()) {
            init(feed);
        }
        result.getFilters().toString();
        // result.toJson();
        // long time = System.currentTimeMillis();
        Json.toJson(result);
        // System.out.println("Time for json is " + (System.currentTimeMillis() - time));
        /*for (IFeed feed : result.getPartOf()) {
            Json.toJson(feed);
            feed.getPartOf().toString();
        }*/
    }

    @Override
    @Transactional
    public IFeed addIFeed(final IFeed iFeed) {
        IFeed r = iFeedRepo.store(iFeed);
        if (r != null) {
            Json.toJson(r);
            //r.toJson();
        }
        return r;
    }

    @Override
    @Transactional
    public void removeIFeed(final Long id) {
        iFeedRepo.remove(id);
        iFeedRepo.flush();
    }

    @Override
    @Transactional
    public VgrDepartment loadDepartment(Long id) {
        VgrDepartment result = objectRepo.findByPrimaryKey(VgrDepartment.class, id);
        for (VgrGroup vg : result.getVgrGroups()) {
            new HashMap(new BeanMap(vg));
            for (IFeed feed : vg.getMemberFeeds()) {
                new HashMap(new BeanMap(feed));
            }
        }
        return result;
    }

    @Override
    @Transactional
    public void removeIFeed(IFeed feed) {
        iFeedRepo.remove(feed.getId());
    }

    public void remove(IFeed feed) {
        EntityManager em = objectRepo.getEntityManager();
        Session session = (Session) em.getDelegate();
        //Connection con = session.connection();
    }


    @Override
    @Transactional
    public IFeed update(final IFeed iFeed) {
        iFeed.putFalseIntoEachFilterUseAndThatHasOnlyOneOccurrence();
        for (DynamicTableDef dynamicTableDef : iFeed.getDynamicTableDefs()) {
            dynamicTableDef.setIfeed(iFeed);
            if (dynamicTableDef.getId() == null) {
                objectRepo.persist(dynamicTableDef);
            } else {
                dynamicTableDef = objectRepo.merge(dynamicTableDef);
            }
            for (ColumnDef columnDef : dynamicTableDef.getColumnDefs()) {
                columnDef.setTableDef(dynamicTableDef);

                assert columnDef.getName() != null : "ColumnDef name cannot be null";
                if (columnDef.getId() == null) {
                    objectRepo.persist(columnDef);
                } else {
                    columnDef = objectRepo.merge(columnDef);
                    assert columnDef.getName() != null : "After merge - ColumnDef name cannot be null";
                }
            }
        }
        IFeed fromDb = getIFeed(iFeed.getId());
        for (DynamicTableDef dynamicTableDef : fromDb.getDynamicTableDefs()) {
            if (!iFeed.getDynamicTableDefs().contains(dynamicTableDef)) {
                objectRepo.delete(dynamicTableDef);
            }
        }

        for (IFeedFilter iFeedFilter : iFeed.getFilters()) {
            iFeedFilter.setFeed(iFeed);
        }

        IFeed result = iFeedRepo.merge(iFeed);
        iFeedRepo.flush();
        return result;
    }

    @Override
    @Transactional
    public void saveDepartment(VgrDepartment department) {
        try {
            if (department.getId() != null) {
                VgrDepartment fromDb = findByPrimaryKey(VgrDepartment.class, department.getId());
                fromDb.setName(department.getName());

                for (VgrGroup groupFromDb : fromDb.getVgrGroups()) {
                    if (!department.getVgrGroups().contains(groupFromDb)) {
                        for (IFeed feed : groupFromDb.getMemberFeeds()) {
                            feed.setGroup(null);
                            update(feed);
                        }
                    } else {
                        VgrGroup group = department.getVgrGroups().get(department.getVgrGroups().indexOf(groupFromDb));
                        if (group.getId() != null) {
                            objectRepo.merge(group);
                        }
                    }
                }

                for (VgrGroup vgrGroup : new ArrayList<VgrGroup>(fromDb.getVgrGroups())) {
                    if (!department.getVgrGroups().contains(vgrGroup)) {
                        fromDb.getVgrGroups().remove(vgrGroup);
                        delete(vgrGroup);
                    }
                }

                for (VgrGroup vgrGroup : department.getVgrGroups()) {
                    if (!fromDb.getVgrGroups().contains(vgrGroup)) {
                        fromDb.getVgrGroups().add(vgrGroup);
                    }
                }
                department = fromDb;
            }
            for (VgrGroup group : department.getVgrGroups()) {
                group.setDepartment(department);
            }
            save(department);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    @Override
    @Transactional
    public IFeed updateIFeed(final IFeed iFeed) {
        IFeed oldIFeed = iFeedRepo.find(iFeed.getId());
        if (oldIFeed == null || filterChanged(oldIFeed, iFeed)) {
            oldIFeed.clearTimestamp();
        } else {
            oldIFeed.setTimestamp();
        }

        oldIFeed.setName(iFeed.getName());
        oldIFeed.setDescription(iFeed.getDescription());
        oldIFeed.setSortDirection(iFeed.getSortDirection());
        oldIFeed.setSortField(iFeed.getSortField());
        oldIFeed.setLinkNativeDocument(iFeed.getLinkNativeDocument());

        oldIFeed.getOwnerships().clear();

        for (Ownership ownership : iFeed.getOwnerships()) {
            ownership.setIfeedId(oldIFeed.getId());
            ownership.setIfeed(oldIFeed);
            oldIFeed.getOwnerships().add(ownership);
        }

        oldIFeed.getDynamicTableDefs().clear();
        for (DynamicTableDef dynamicTableDef : iFeed.getDynamicTableDefs()) {
            oldIFeed.getDynamicTableDefs().add(dynamicTableDef);
        }

        oldIFeed.removeFilters();
        iFeedRepo.merge(oldIFeed);
        iFeedRepo.flush();
        // Strange thing to do? Yes - but it does not work otherwise. Old, removed, filters are kept in the db
        // despite being removed from the collection.
        // This behavior have not been repeated with unit-test though.

        oldIFeed = iFeedRepo.find(oldIFeed.getId());

        for (IFeedFilter filter : iFeed.getFilters()) {
            if (filter.getFilterQuery() != null && !"".equals(filter.getFilterQuery().trim())) {
                oldIFeed.addFilter(filter);
            }
        }

        IFeed mergedIfeed = iFeedRepo.merge(oldIFeed);

        mergedIfeed.setTimestamp();
        mergedIfeed = iFeedRepo.merge(mergedIfeed);
        return mergedIfeed;
    }

    private boolean filterChanged(final IFeed oldIFeed, final IFeed iFeed) {
        Collection<IFeedFilter> oldFilters = oldIFeed.getFilters();
        Collection<IFeedFilter> newFilters = iFeed.getFilters();
        if (!(equals(oldIFeed.getName(), iFeed.getName())
                && equals(oldIFeed.getLinkNativeDocument(), iFeed.getLinkNativeDocument()) && equals(
                oldIFeed.getDescription(), iFeed.getDescription()))) {
            return true;
        }

        return oldFilters.equals(newFilters);
    }

    private boolean equals(Object o1, Object o2) {
        if (o1 == o2) {
            return true;
        }
        if (o1 == null || o2 == null) {
            return false;
        }
        return o1.equals(o2);
    }

    @Override
    public List<FieldsInf> getFieldsInfs() {
        ArrayList<FieldsInf> result = new ArrayList<FieldsInf>(fieldsInfRepo.findByQuery("select fi from FieldsInf fi order by fi.id asc"));
        return result;
    }

    @Override
    public FieldInf getFieldInf(String byId) {
        return mapFieldInfToId().get(byId);
    }

    @Override
    public Config getConfig(String withThatId) {
        return objectRepo.findByPrimaryKey(Config.class, withThatId);
    }

    @Transactional
    @Override
    public List<FieldInf> getFieldInfs() {
        Collection<FieldInf> result = objectRepo.findByQuery(FieldInf.class, "select fi from FieldInf fi where fi.parentPk is null order by position");
        for (FieldInf fieldInf : result) {
            fieldInf.init();
        }
        return new ArrayList<>(result);

        /*List<FieldsInf> fieldsInfs = getFieldsInfs();
        if (fieldsInfs.isEmpty()) {
            return new ArrayList<FieldInf>();
        }
        return fieldsInfs.get(fieldsInfs.size() - 1).getFieldInfs();*/
    }

    @Transactional
    @Override
    public void storeFieldsInf(FieldsInf inf) {
        //fieldsInfRepo.store(inf);
        if (inf.getId() == null) {
            fieldsInfRepo.persist(inf);
        } else {
            fieldsInfRepo.merge(inf);
        }
        fieldsInfRepo.flush();
    }

    @Transactional
    @Override
    public Map<String, FieldInf> mapFieldInfToId() {
        Map<String, FieldInf> result = new HashMap<String, FieldInf>();
        mapFieldInfToId(getFieldInfs(), result);
        return result;
    }

    public static void mapFieldInfToId(List<FieldInf> fields, Map<String, FieldInf> result) {
        for (FieldInf fi : fields) {
            if (fi.getLabel()) {
                mapFieldInfToId(fi.getChildren(), result);
            } else {
                result.put(fi.getId(), fi);
            }
        }
    }

    public JpaRepository<IFeed, Long, Long> getiFeedRepo() {
        return iFeedRepo;
    }

    public void setiFeedRepo(JpaRepository<IFeed, Long, Long> iFeedRepo) {
        this.iFeedRepo = iFeedRepo;
    }

    public JpaRepository<FieldsInf, Long, Long> getFieldsInfRepo() {
        return fieldsInfRepo;
    }

    public void setFieldsInfRepo(JpaRepository<FieldsInf, Long, Long> fieldsInfRepo) {
        this.fieldsInfRepo = fieldsInfRepo;
    }

    public JpaRepository<VgrDepartment, Long, Long> getDepartmentRepo() {
        return departmentRepo;
    }

    public void setDepartmentRepo(JpaRepository<VgrDepartment, Long, Long> departmentRepo) {
        this.departmentRepo = departmentRepo;
    }

    public String getSolrServiceUrl() {
        return solrServiceUrl;
    }

    public void setSolrServiceUrl(String solrServiceUrl) {
        this.solrServiceUrl = solrServiceUrl;
    }

    @Override
    public List<String> fetchFilterSuggestion(IFeed feed, FieldInf field, String starFilter) {
        // FieldInf field = getFieldInf(fieldId);
        return SolrFacetUtil.fetchFacets(getSolrServiceUrl(), feed, field, starFilter);
    }

    public ObjectRepo getObjectRepo() {
        return objectRepo;
    }

    public void setObjectRepo(ObjectRepo objectRepo) {
        this.objectRepo = objectRepo;
    }


    /**
     * Find data in the db by ites primary key.
     *
     * @param clazz what type to find.
     * @param id    search key to use.
     * @param <T>   type that are being returned.
     * @return list with zero or more results.
     */
    @Override
    public <T> T findByPrimaryKey(Class<T> clazz, Object id) {
        return objectRepo.findByPrimaryKey(clazz, id);
    }

    @Override
    @Transactional
    public void deleteDepartmentGroups(VgrDepartment department) {
        department = objectRepo.getReference(VgrDepartment.class, department.getId());
        //addMemberFeedsToGroups(department.getVgrGroups());
        for (IFeed ifeed : department.getMemberFeeds()) {
            ifeed.setDepartment(null);
            objectRepo.merge(ifeed);
        }
        for (VgrGroup group : new ArrayList<VgrGroup>(department.getVgrGroups())) {
            for (IFeed feed : group.getMemberFeeds()) {
                feed.setGroup(null);
                objectRepo.merge(feed);
            }
            // iFeedService.delete(iFeedService.findByPrimaryKey(VgrGroup.class, group.getId()));
            if (group.getId() != null) {
                department.getVgrGroups().remove(group);
                objectRepo.delete(group);
            }
        }
    }


    @Override
    @Transactional
    public void deleteDepartmentEntity(VgrDepartment department) {
        department = objectRepo.findByPrimaryKey(VgrDepartment.class, department.getId());
        objectRepo.delete(department);
    }

    @Override
    @Transactional
    public void save(DynamicTableDef instance) {
        if (instance.getId() == null) {
            objectRepo.persist(instance);
            for (ColumnDef columnDef : instance.getColumnDefs()) {
                columnDef.setTableDef(instance);
                objectRepo.persist(columnDef);
            }
        } else {
            objectRepo.merge(instance);
            for (ColumnDef columnDef : instance.getColumnDefs()) {
                columnDef.setTableDef(instance);
                objectRepo.merge(columnDef);
            }
        }

    }

    @Override
    @Transactional
    public IFeed copyAndPersistFeed(Long withThatKey, String otherUserId) {
        assert otherUserId != null && !otherUserId.isEmpty();
        IFeed result = findByPrimaryKey(IFeed.class, withThatKey);
        init(result);
        result = copy(result);
        result.setUserId(otherUserId);
        result.setCreatorName(otherUserId);
        objectRepo.persist(result);
        objectRepo.flush();
        update(result);
        return result;
    }

    @Override
    public String toDocumentPopupHtml(Map<String, Object> forThatItem) {
        Config config = objectRepo.findByPrimaryKey(Config.class, "popup");
        List<DocumentPopupConf> confs = CommonUtils.fromCsv(DocumentPopupConf.class, config.getSetting());
        StringBuilder sb = new StringBuilder();
        for (DocumentPopupConf conf : confs) {

        }
        return null;
    }

    @Override
    public void save(Config inf) {
        objectRepo.merge(inf);
    }

    public IFeed copy(IFeed that) {
        IFeed result = new IFeed();
        result.setCreatorName(that.getCreatorName());

        for (IFeedFilter filter : that.getFilters()) {
            IFeedFilter nf = new IFeedFilter();
            nf.setFieldInf(filter.getFieldInf());
            nf.setFilterQuery(filter.getFilterQuery());
            nf.setFilterKey(filter.getFilterKey());
            nf.setOperator(filter.getOperator());
            nf.setMetadata(filter.getMetadata());
            result.addFilter(nf);
        }

        result.setDepartment(that.getDepartment());
        result.setDescription(that.getDescription());
        result.setGroup(that.getGroup());
        result.setLinkNativeDocument(that.getLinkNativeDocument());
        result.setSortDirection(that.getSortDirection());
        result.setUserId(that.getUserId());
        result.setName(that.getName());

        for (Ownership o : that.getOwnerships()) {
            Ownership no = new Ownership();
            no.setIfeed(result);
            no.setUserId(o.getUserId());
            no.setName(o.getName());
            result.getOwnerships().add(no);
        }

        for (DynamicTableDef oldTable : that.getDynamicTableDefs()) {
            DynamicTableDef newTable = new DynamicTableDef();
            newTable.setIfeed(result);
            newTable.setDefaultSortColumn(oldTable.getDefaultSortColumn());
            newTable.setDefaultSortOrder(oldTable.getDefaultSortOrder());
            newTable.setFeedHome(oldTable.getFeedHome());
            newTable.setFontSize(oldTable.getFontSize());
            newTable.setHideRightColumn(oldTable.isHideRightColumn());
            newTable.setLinkOriginalDoc(oldTable.isLinkOriginalDoc());
            newTable.setMaxHitLimit(oldTable.getMaxHitLimit());
            newTable.setName(oldTable.getName());
            newTable.setShowTableHeader(oldTable.isShowTableHeader());

            for (ColumnDef oldColumn : oldTable.getColumnDefs()) {
                ColumnDef newColumn = new ColumnDef();
                newColumn.setAlignment(oldColumn.getAlignment());
                newColumn.setLabel(oldColumn.getLabel());
                newColumn.setTableDef(newTable);
                newColumn.setName(oldColumn.getName());
                newColumn.setWidth(oldColumn.getWidth());
                newTable.getColumnDefs().add(newColumn);
            }

            result.getDynamicTableDefs().add(newTable);
        }

        for (IFeed composite : that.getComposites()) {
            result.getComposites().add(composite);
        }

        return result;
    }

}
