package se.vgregion.ifeed.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.transaction.annotation.Transactional;

import se.vgregion.dao.domain.patterns.repository.Repository;
import se.vgregion.ifeed.types.IFeed;

public class IFeedServiceImpl implements IFeedService {

    private Repository<IFeed, Long> iFeedRepo;

    public IFeedServiceImpl(Repository<IFeed, Long> iFeedRepo) {
        this.iFeedRepo = iFeedRepo;
    }

    @Override
    public List<IFeed> getIFeeds() {
        return new ArrayList<IFeed>(iFeedRepo.findAll());
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
