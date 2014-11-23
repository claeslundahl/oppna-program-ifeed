package se.vgregion.ifeed.service.ifeed;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.transaction.annotation.Transactional;

import se.vgregion.dao.domain.patterns.repository.db.jpa.JpaRepository;
import se.vgregion.ifeed.service.push.IFeedPublisher;
import se.vgregion.ifeed.types.FieldInf;
import se.vgregion.ifeed.types.FieldsInf;
import se.vgregion.ifeed.types.IFeed;
import se.vgregion.ifeed.types.IFeedFilter;
import se.vgregion.ifeed.types.Ownership;

public class IFeedServiceImpl implements IFeedService {

    private JpaRepository<IFeed, Long, Long> iFeedRepo;
    private JpaRepository<FieldsInf, Long, Long> fieldsInfRepo;
    private IFeedPublisher iFeedPublisher;

    public IFeedServiceImpl(final JpaRepository<IFeed, Long, Long> iFeedRepoParam, IFeedPublisher iFeedPublisher,
            JpaRepository<FieldsInf, Long, Long> fieldsInfRepo) {
        iFeedRepo = iFeedRepoParam;
        this.iFeedPublisher = iFeedPublisher;
        this.fieldsInfRepo = fieldsInfRepo;
    }

    public IFeedServiceImpl() {

    }

    @Override
    public final List<IFeed> getIFeeds() {
        return new ArrayList<IFeed>(iFeedRepo.findAll());
    }

    @Override
    public final List<IFeed> getUserIFeeds(final String userId) {
        ArrayList<IFeed> result = new ArrayList<IFeed>(
                iFeedRepo
                        .findByQuery(
                                "SELECT distinct ifeed FROM IFeed ifeed "
                                        + "WHERE ifeed.userId=?1 or ifeed.id in (select o.ifeedId from Ownership o where o.userId=?2)",
                                new Object[] { userId, userId }));

        return result;
    }

    @Override
    public final IFeed getIFeed(final Long id) {
        return iFeedRepo.find(id);
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

        try {
            iFeedPublisher.addIFeed(mergedIfeed);
            iFeedPublisher.publish();
        } catch (Exception e) {
            e.printStackTrace();
        }
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

    public IFeedPublisher getiFeedPublisher() {
        return iFeedPublisher;
    }

    public void setiFeedPublisher(IFeedPublisher iFeedPublisher) {
        this.iFeedPublisher = iFeedPublisher;
    }

}
