package se.vgregion.ifeed.service.ifeed;

import java.util.List;

import se.vgregion.ifeed.types.IFeed;

public interface IFeedService {
    List<IFeed> getIFeeds();

    List<IFeed> getUserIFeeds(String userId);

    IFeed getIFeed(Long id);

    IFeed addIFeed(IFeed iFeed);

    IFeed updateIFeed(IFeed iFeed);

    void removeIFeed(Long id);

}
