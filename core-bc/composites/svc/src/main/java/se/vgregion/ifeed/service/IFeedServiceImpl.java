package se.vgregion.ifeed.service;

import java.util.ArrayList;
import java.util.List;

import se.vgregion.ifeed.service.store.FeedStore;
import se.vgregion.ifeed.types.IFeed;


public class IFeedServiceImpl implements IFeedService {

	private FeedStore store;
	
	@Override
	public List<IFeed> getIFeeds() {
		return new ArrayList<IFeed>(store.getIFeeds());
	}

	@Override
	public IFeed getIFeed(Long id) {
		return store.getIFeed(id);
	}

	@Override
	public void addIFeed(IFeed iFeed) {
		store.addIFeed(iFeed);
	}

	@Override
	public void removeIFeed(Long id) {
		store.removeIFeed(id);
	}
	
	@Override
	public void updateIFeed(IFeed iFeed) {
		store.replaceIFeed(iFeed);
	}

	public void setStore(FeedStore store) {
		this.store = store;
	}

	public FeedStore getStore() {
		return store;
	}
}
