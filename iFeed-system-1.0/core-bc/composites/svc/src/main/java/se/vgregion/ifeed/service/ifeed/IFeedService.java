package se.vgregion.ifeed.service.ifeed;

import java.util.Collection;

import se.vgregion.ifeed.types.IFeed;

public interface IFeedService {
    Collection<IFeed> getIFeeds();

    Collection<IFeed> getUserIFeeds(String userId);

    IFeed getIFeed(Long id);

    void addIFeed(IFeed iFeed);

    void updateIFeed(IFeed iFeed);

    void removeIFeed(Long id);

}
