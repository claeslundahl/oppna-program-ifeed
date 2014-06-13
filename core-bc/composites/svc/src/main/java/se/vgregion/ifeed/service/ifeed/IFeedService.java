package se.vgregion.ifeed.service.ifeed;

import java.util.List;
import java.util.Map;

import com.liferay.portal.kernel.cache.ThreadLocalCachable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import se.vgregion.ifeed.types.FieldInf;
import se.vgregion.ifeed.types.FieldsInf;
import se.vgregion.ifeed.types.IFeed;

public interface IFeedService {

    List<IFeed> getIFeeds();

    List<IFeed> getUserIFeeds(String userId);

    List<IFeed> getIFeedsByFilter(Filter filter);

    IFeed getIFeed(Long id);

    IFeed addIFeed(IFeed iFeed);

    @Transactional
    IFeed update(IFeed iFeed);

    IFeed updateIFeed(IFeed iFeed);

    void removeIFeed(Long id);

    List<FieldsInf> getFieldsInfs();

    List<FieldInf> getFieldInfs();

    void storeFieldsInf(FieldsInf inf);

    Map<String, FieldInf> mapFieldInfToId();
}
