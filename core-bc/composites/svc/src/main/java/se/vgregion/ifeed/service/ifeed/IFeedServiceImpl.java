package se.vgregion.ifeed.service.ifeed;

import net.sf.cglib.beans.BeanMap;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import se.vgregion.dao.domain.patterns.repository.db.jpa.JpaRepository;
import se.vgregion.ifeed.service.solr.SolrFacetUtil;
import se.vgregion.ifeed.shared.ColumnDef;
import se.vgregion.ifeed.shared.DynamicTableDef;
import se.vgregion.ifeed.shared.DynamicTableSortingDef;
import se.vgregion.ifeed.types.*;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.io.Serializable;
import java.util.*;

@Service
public class IFeedServiceImpl implements IFeedService, Serializable {

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
        return new ArrayList<IFeed>(iFeedRepo.findAll());
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
    public final List<IFeed> getUserIFeeds(final String userId) {
        ArrayList<IFeed> result = new ArrayList<IFeed>(
                iFeedRepo
                        .findByQuery(
                                "SELECT distinct ifeed FROM IFeed ifeed "
                                        + "WHERE ifeed.userId=?1 or ifeed.id in (select o.ifeedId from Ownership o where o.userId=?2)",
                                new Object[]{userId, userId}));
        init(result);
        initializedFeeds.set(null);
        return result;
    }

    @Override
    @Transactional
    public final IFeed getFeedForSolrQuery(final Long id) {
        return getFeedForSolrQueryImpl(id, new HashSet<>());
    }

    @Transactional
    private final IFeed getFeedForSolrQueryImpl(final Long id, Set<Long> traversal) {
        traversal.add(id);
        long now = System.currentTimeMillis();
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

        System.out.println("Time to get feed from db was " + (System.currentTimeMillis() - now) + " feed id " + id);

        return ifeed;
    }

    @Override
    @Transactional
    public List<IFeed> getIFeedsByFilter(Filter filter, int start, int end) {
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

        long countResult = findTotalCount(jpql, args);

        latestFilterQueryTotalCount = (int) countResult;

        ArrayList<IFeed> rv = new ArrayList<IFeed>(result);

        /*long now = System.currentTimeMillis();
        for (IFeed feed : rv) {
            for (IFeed composite : feed.getComposites()) {
                init(composite);
            }
            for (IFeed composite : feed.getPartOf()) {
                init(composite);
            }
            for (DynamicTableDef dtf : feed.getDynamicTableDefs()) {
                for (ColumnDef column : dtf.getColumnDefs()) {
                    column.getId();
                }
            }
        }
        System.out.println("Time for init inside getIFeedsByFilter " + (System.currentTimeMillis() - now)) ;*/
        initializedFeeds.set(null);

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

    @Override
    @Transactional
    public final IFeed getIFeed(final Long id) {
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

        IFeed result = iFeedRepo.find(id);

        // IFeed result = iFeedRepo.find(id);
        init(result);
        initializedFeeds.set(null);
        return result;
    }

    @Transactional
    private void init(Collection<IFeed> result) {
        if (result == null) {
            return;
        }
        for (IFeed item : result) {
            init(item);
        }
    }

    private static ThreadLocal<Set<IFeed>> initializedFeeds = new ThreadLocal<>();

    @Transactional
    private void init(IFeed result) {
        if (initializedFeeds.get() == null) {
            initializedFeeds.set(new HashSet<>());
        }
        if (initializedFeeds.get().contains(result)) {
            return;
        }
        if (result == null) {
            throw new NullPointerException("An ifeed must be found to");
        }

        // result.getDynamicTableDefs().addAll(lookupDynamicTableDefs(result.getId()));

        List<DynamicTableDef> dynamicTableDefs = result.getDynamicTableDefs();
        if (dynamicTableDefs != null) {
            for (DynamicTableDef dynamicTable : dynamicTableDefs) {
                if (dynamicTable.getColumnDefs() != null) {
                    for (ColumnDef columnDef : dynamicTable.getColumnDefs()) {

                    }
                }
                if (dynamicTable.getExtraSorting() != null) {
                    for (DynamicTableSortingDef dynamicTableSortingDef : dynamicTable.getExtraSorting()) {

                    }
                }
            }
        }
        if (result.getDepartment() != null) {
            for (VgrGroup group : result.getDepartment().getVgrGroups()) {
                for (IFeed otherFeed : group.getMemberFeeds()) {
                    otherFeed.toString();
                }
            }
        }
        result.getComposites().toString();
        result.getPartOf().toString();
        result.getFilters().toString();
    }

    @Override
    @Transactional
    public final IFeed addIFeed(final IFeed iFeed) {
        return iFeedRepo.store(iFeed);
    }

    @Override
    @Transactional
    public final void removeIFeed(final Long id) {
        iFeedRepo.remove(id);
        iFeedRepo.flush();
    }

    @Override
    @Transactional
    public VgrDepartment loadDepartment(Long id) {
        VgrDepartment result = objectRepo.findByPrimaryKey(VgrDepartment.class, id);
        for (VgrGroup vg : result.getVgrGroups()) {
            new HashMap(BeanMap.create(vg));
            for (IFeed feed : vg.getMemberFeeds()) {
                new HashMap(BeanMap.create(feed));
            }
        }
        return result;
    }

    @Override
    @Transactional
    public final void removeIFeed(IFeed feed) {
        feed = objectRepo.findByPrimaryKey(IFeed.class, feed.getId());
        for (DynamicTableDef dynamicTableDef : feed.getDynamicTableDefs()) {
            for (ColumnDef columnDef : dynamicTableDef.getColumnDefs()) {
                objectRepo.delete(columnDef);
            }
            dynamicTableDef.getColumnDefs().clear();
            objectRepo.delete(dynamicTableDef);
        }
        feed.getDynamicTableDefs().clear();
        objectRepo.delete(feed);
    }

    @Override
    @Transactional
    public final IFeed update(final IFeed iFeed) {
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
    public final IFeed updateIFeed(final IFeed iFeed) {
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

        return !CollectionUtils.isEqualCollection(oldFilters, newFilters);
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
        return new ArrayList<FieldsInf>(fieldsInfRepo.findByQuery("select fi from FieldsInf fi order by fi.version asc, fi.id asc"));
    }

    @Transactional
    @Override
    public List<FieldInf> getFieldInfs() {
        List<FieldsInf> fieldsInfs = getFieldsInfs();
        if (fieldsInfs.isEmpty()) {
            return new ArrayList<FieldInf>();
        }
        return fieldsInfs.get(fieldsInfs.size() - 1).getFieldInfs();
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
            if (fi.isLabel()) {
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
    public List<String> fetchFilterSuggestion(IFeed feed, String fieldId) {
        return SolrFacetUtil.fetchFacets(getSolrServiceUrl(), feed, fieldId);
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
        } else {
            objectRepo.merge(instance);
        }
    }

}
