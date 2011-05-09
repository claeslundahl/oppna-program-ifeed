package se.vgregion.ifeed.service;

import java.util.ArrayList;
import java.util.List;
import org.springframework.transaction.annotation.Transactional;
import se.vgregion.dao.domain.patterns.repository.db.jpa.JpaRepository;
import se.vgregion.ifeed.types.IFeed;

public class IFeedServiceImpl implements IFeedService {

    private JpaRepository<IFeed, Long, Long> iFeedRepo;

    public IFeedServiceImpl(JpaRepository<IFeed, Long, Long> iFeedRepo) {
        this.iFeedRepo = iFeedRepo;
    }

    @Override
    public List<IFeed> getIFeeds() {
        return new ArrayList<IFeed>(iFeedRepo.findAll());
    }
    
    @Override
    public List<IFeed> getUserIFeeds(String userId) {
    	return new ArrayList<IFeed>(iFeedRepo.findByQuery("SELECT ifeed FROM IFeed ifeed WHERE ifeed.userId=?1", new Object[] { userId }));
    }

    @Override
    public IFeed getIFeed(Long id) {
        return iFeedRepo.find(id);
    }

    @Override
    @Transactional
    public void addIFeed(IFeed iFeed) {
        iFeedRepo.store(iFeed);
    }

    @Override
    @Transactional
    public void removeIFeed(Long id) {
        iFeedRepo.remove(id);
    }

    @Override
    @Transactional
    public void updateIFeed(IFeed iFeed) {
        iFeedRepo.merge(iFeed);
    }

}
