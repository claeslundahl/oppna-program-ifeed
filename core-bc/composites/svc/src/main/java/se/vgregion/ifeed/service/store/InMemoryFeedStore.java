package se.vgregion.ifeed.service.store;

import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;

import se.vgregion.ifeed.types.IFeed;


public class InMemoryFeedStore implements FeedStore {
	
	private ConcurrentHashMap<Long, IFeed> store = new ConcurrentHashMap<Long, IFeed>();

	
	public Collection<IFeed> getIFeeds() {
		return store.values();
	}
	
	public IFeed getIFeed(Long id) {
		return store.get(id);
	}
	
	public void addIFeed(IFeed iFeed) {
		//TODO: check for duplicates
		store.put(iFeed.getId(), iFeed);
	}
	
	public void replaceIFeed(IFeed iFeed) {
		//TODO: check if there is a feed to replace
		store.replace(iFeed.getId(), iFeed);
	}
	
	public void removeIFeed(Long id) {
		store.remove(id);
	}
	
	public void clear(){
		store.clear();
	}
}