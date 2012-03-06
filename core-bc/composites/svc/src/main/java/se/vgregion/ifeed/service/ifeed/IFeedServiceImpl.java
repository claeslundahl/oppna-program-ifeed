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
        return new ArrayList<IFeed>(iFeedRepo.findByQuery("SELECT ifeed FROM IFeed ifeed WHERE ifeed.userId=?1",
                new Object[] { userId }));
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
        oldIFeed.setFilters(iFeed.getFilters());
        oldIFeed.setSortDirection(iFeed.getSortDirection());
        oldIFeed.setSortField(iFeed.getSortField());

        IFeed mergedIfeed = iFeedRepo.merge(oldIFeed);
        iFeedPublisher.addIFeed(mergedIfeed);
        try {
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

        return !CollectionUtils.isEqualCollection(oldFilters, newFilters);
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
