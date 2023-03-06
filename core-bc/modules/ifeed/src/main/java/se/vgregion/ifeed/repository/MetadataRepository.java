package se.vgregion.ifeed.repository;

import org.springframework.data.repository.PagingAndSortingRepository;
import se.vgregion.ifeed.types.Metadata;

import java.util.List;

public interface MetadataRepository extends PagingAndSortingRepository<Metadata, Long> {

    List<Metadata> findByName(String that);

/*    @Query("select m from Metadata m where m.")
    Page<Metadata> findByParentName(String name);*/

}
