package se.vgregion.ifeed.service.ifeed;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import se.vgregion.dao.domain.patterns.repository.db.jpa.JpaRepository;
import se.vgregion.ifeed.service.solr.SolrFacetUtil;
import se.vgregion.ifeed.shared.ColumnDef;
import se.vgregion.ifeed.shared.DynamicTableDef;
import se.vgregion.ifeed.types.*;

import java.io.Serializable;
import java.util.*;

@Service
public class IFeedServiceImpl implements IFeedService, Serializable {

    private JpaRepository<IFeed, Long, Long> iFeedRepo;

    private ObjectRepo objectRepo;

    private JpaRepository<FieldsInf, Long, Long> fieldsInfRepo;

    private JpaRepository<VgrDepartment, Long, Long> departmentRepo;
    private String solrServiceUrl;

    public IFeedServiceImpl(final JpaRepository<IFeed, Long, Long> iFeedRepoParam,
                            JpaRepository<FieldsInf, Long, Long> fieldsInfRepo, JpaRepository<VgrDepartment, Long, Long> departmentRepo) {
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
    public void delete(VgrDepartment department) {
        try {
            departmentRepo.remove(department.getId());
            departmentRepo.flush();
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
        return result;
    }

    @Override
    @Transactional
    public List<IFeed> getIFeedsByFilter(Filter filter) {
        List<Object> values = new ArrayList<Object>();
        String jpql = filter.toJpqlQuery(values);
        List<IFeed> result = (List<IFeed>) iFeedRepo.findByQuery(jpql, values.toArray());
        init(result);
        return new ArrayList<IFeed>(result);
    }

    @Override
    @Transactional
    public final IFeed getIFeed(final Long id) {
        IFeed result = iFeedRepo.find(id);
        init(result);
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

    @Transactional
    private void init(IFeed result) {
        List<DynamicTableDef> dynamicTableDefs = result.getDynamicTableDefs();
        if (dynamicTableDefs != null) {
            for (DynamicTableDef dynamicTable : dynamicTableDefs) {
                if (dynamicTable.getColumnDefs() != null) {
                    for (ColumnDef columnDef : dynamicTable.getColumnDefs()) {

                    }
                }
            }
        }
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

        IFeed result = iFeedRepo.merge(iFeed);
        iFeedRepo.flush();
        return result;
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
        return new ArrayList<FieldsInf>(fieldsInfRepo.findByQuery("select fi from FieldsInf fi order by fi.id"));
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
        fieldsInfRepo.store(inf);
        fieldsInfRepo.flush();
    }

    @Transactional
    @Override
    public Map<String, FieldInf> mapFieldInfToId() {
        Map<String, FieldInf> result = new HashMap<String, FieldInf>();
        mapFieldInfToId(getFieldInfs(), result);
        return result;
    }

    void mapFieldInfToId(List<FieldInf> fields, Map<String, FieldInf> result) {
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

}
