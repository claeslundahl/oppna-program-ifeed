package se.vgregion.ifeed.service.store;

import java.util.Collection;

import org.springframework.transaction.annotation.Transactional;

import se.vgregion.ifeed.types.IFeed;


public interface FeedStore {
	public Collection<IFeed> getIFeeds();
	
	public IFeed getIFeed(Long id);
	
	@Transactional
	public void addIFeed(IFeed iFeed);
	
	@Transactional
	public void replaceIFeed(IFeed iFeed);
	
	@Transactional
	public void removeIFeed(Long id);
	
	public void clear();
}