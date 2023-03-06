package se.vgregion.ifeed.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import se.vgregion.ifeed.types.Feed;

public interface FeedRepository extends JpaRepository<Feed, Long> {

}
