package se.vgregion.ifeed.service.store;

import java.util.Collection;
import java.util.Collections;

import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import se.vgregion.dao.domain.patterns.repository.db.jpa.JpaRepository;
import se.vgregion.ifeed.types.IFeed;

public class JPAFeedStore implements FeedStore {

	private JpaRepository<IFeed, Long, Long> repository;

	@Override
	public Collection<IFeed> getIFeeds() {
		return Collections.unmodifiableCollection(repository.findAll());
	}

	@Override
	public IFeed getIFeed(Long id) {
		return repository.find(id);
	}

	@Override
	@Transactional(propagation=Propagation.REQUIRED)
	public void addIFeed(IFeed iFeed) {
		repository.store(iFeed);
	}

	@Override
	public void replaceIFeed(IFeed iFeed) {
		repository.merge(iFeed);
	}

	@Override
	@Transactional
	public void removeIFeed(Long id) {
		repository.remove(id);
	}

	@Override
	public void clear() {
		// TODO Auto-generated method stub
	}

	public void setRepository(JpaRepository<IFeed, Long, Long> repository) {
		this.repository = repository;
	}

	public JpaRepository<IFeed, Long, Long> getRepository() {
		return repository;
	}

//	public void initSample() {
//		
//		Date now = new Date();
//
//		IFeed feed1 = new IFeed();
//		List<String> filters1 = Arrays.asList("format:PDF-dokument",
//				"infotype:Dokument", "collection:webbdok");
//		feed1.setFilters(filters1);
//		feed1.setName("I'm feed one");
//		feed1.setTimestamp(now);
//		feed1.setDescription("En exempelbeskrivning");
//		feed1.setUserId(new Long(12345));
//
//		IFeed feed2 = new IFeed();
//		List<String> filters2 = Arrays.asList("format:PDF-dokument",
//				"infotype:Dokument");
//		feed2.setFilters(filters2);
//		feed2.setName("I'm feed two");
//		feed2.setTimestamp(now);
//		feed2.setDescription("En annan exempelbeskrivning");
//		feed2.setUserId(new Long(12345));
//
//		IFeed feed3 = new IFeed();
//		List<String> filters3 = Arrays.asList("format:PDF-dokument",
//				"infotype:Dokument", "collection:webbdok");
//		feed3.setFilters(filters3);
//		feed3.setName("I'm feed three");
//		feed3.setTimestamp(now);
//		feed3.setDescription("Exempelbeskrivning...");
//		feed3.setUserId(new Long(12345));
//
//		IFeed feed4 = new IFeed();
//		List<String> filters4 = Arrays.asList("format:PDF-dokument",
//				"infotype:Dokument", "scopelabel:Regionkansliet");
//		feed4.setFilters(filters4);
//		feed4.setName("I'm feed four");
//		feed4.setTimestamp(now);
//		feed4.setDescription("Beskrivning igen");
//		feed4.setUserId(new Long(12345));
//
//		addIFeed(feed1);
//		addIFeed(feed2);
//		addIFeed(feed3);
//		addIFeed(feed4);
//	}
}
