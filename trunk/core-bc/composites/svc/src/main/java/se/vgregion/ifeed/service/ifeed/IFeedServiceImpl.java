package se.vgregion.ifeed.service.ifeed;

import java.util.ArrayList;
import java.util.Collection;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.transaction.annotation.Transactional;

import se.vgregion.dao.domain.patterns.repository.db.jpa.JpaRepository;
import se.vgregion.ifeed.types.IFeed;
import se.vgregion.ifeed.types.IFeedFilter;

public class IFeedServiceImpl implements IFeedService {

    private JpaRepository<IFeed, Long, Long> iFeedRepo;

    public IFeedServiceImpl(
            final JpaRepository<IFeed, Long, Long> iFeedRepoParam) {
        iFeedRepo = iFeedRepoParam;
    }

    @Override
    public final Collection<IFeed> getIFeeds() {
        return new ArrayList<IFeed>(iFeedRepo.findAll());
    }

    @Override
    public final Collection<IFeed> getUserIFeeds(final String userId) {
        return new ArrayList<IFeed>(iFeedRepo.findByQuery(
            "SELECT ifeed FROM IFeed ifeed WHERE ifeed.userId=?1",
                new Object[] {userId}));
    }

    @Override
    public final IFeed getIFeed(final Long id) {
        return iFeedRepo.find(id);
    }

    @Override
    @Transactional
    public final void addIFeed(final IFeed iFeed) {
        iFeedRepo.store(iFeed);
    }

    @Override
    @Transactional
    public final void removeIFeed(final Long id) {
        iFeedRepo.remove(id);
    }

    @Override
    @Transactional
    public final void updateIFeed(final IFeed iFeed) {
        IFeed oldIFeed = iFeedRepo.find(iFeed.getId());
        if (oldIFeed == null || filterChanged(oldIFeed, iFeed)) {
            iFeed.clearTimestamp();
        }
        iFeedRepo.merge(iFeed);
    }

    private boolean filterChanged(final IFeed oldIFeed, final IFeed iFeed) {
        Collection<IFeedFilter> oldFilters = oldIFeed.getFilters();
        Collection<IFeedFilter> newFilters = iFeed.getFilters();

        return !CollectionUtils.isEqualCollection(oldFilters, newFilters);
    }

}
