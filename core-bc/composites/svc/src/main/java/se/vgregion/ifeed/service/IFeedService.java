package se.vgregion.ifeed.service;

import java.util.List;

import se.vgregion.ifeed.types.IFeed;

public interface IFeedService {
	public List<IFeed> getIFeeds();

	public IFeed getIFeed(Long id);
	
	public void addIFeed(IFeed iFeed);
	
	public void updateIFeed(IFeed iFeed);
	
	public void removeIFeed(Long id);
}
