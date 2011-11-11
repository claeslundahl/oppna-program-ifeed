package se.vgregion.ifeed.service.ifeed;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.transaction.annotation.Transactional;

import se.vgregion.dao.domain.patterns.repository.db.jpa.JpaRepository;
import se.vgregion.ifeed.service.push.IFeedPublisher;
import se.vgregion.ifeed.types.IFeed;
import se.vgregion.ifeed.types.IFeedFilter;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.service.ResourceLocalService;


public class IFeedServiceImpl implements IFeedService {

    private JpaRepository<IFeed, Long, Long> iFeedRepo;
    private IFeedPublisher iFeedPublisher;
    private ResourceLocalService resourceService;


    public IFeedServiceImpl(
            final JpaRepository<IFeed, Long, Long> iFeedRepoParam,
                IFeedPublisher iFeedPublisher,
                    ResourceLocalService resourceService) {
        iFeedRepo = iFeedRepoParam;
        this.iFeedPublisher = iFeedPublisher;
        this.resourceService = resourceService;
    }

    @Override
    public final List<IFeed> getIFeeds() {
        return new ArrayList<IFeed>(iFeedRepo.findAll());
    }

    @Override
    public final List<IFeed> getUserIFeeds(final String userId) {
        return new ArrayList<IFeed>(
            iFeedRepo.findByQuery(
                "SELECT ifeed FROM IFeed ifeed WHERE ifeed.userId=?1",
                    new Object[] { userId }));
    }

    @Override
    public final IFeed getIFeed(final Long id) {
        return iFeedRepo.find(id);
    }

    @Override
    @Transactional
    public final IFeed addIFeed(final IFeed iFeed) {
    	try {
			resourceService.addResources(0, 0, 0, "se.vgregion.ifeed.types.IFeed",
			        iFeed.getId().longValue(), false, false, true);
		} catch (PortalException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SystemException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
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
        iFeedPublisher.publish();
        mergedIfeed.setTimestamp();
        mergedIfeed = iFeedRepo.merge(mergedIfeed);
        return mergedIfeed;

    }

    private boolean filterChanged(final IFeed oldIFeed, final IFeed iFeed) {
        Collection<IFeedFilter> oldFilters = oldIFeed.getFilters();
        Collection<IFeedFilter> newFilters = iFeed.getFilters();

        return !CollectionUtils.isEqualCollection(oldFilters, newFilters);
    }
}
