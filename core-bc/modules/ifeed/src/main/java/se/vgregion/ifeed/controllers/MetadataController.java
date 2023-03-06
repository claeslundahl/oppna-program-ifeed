package se.vgregion.ifeed.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import se.vgregion.ifeed.repository.MetadataRepository;
import se.vgregion.ifeed.service.MetadataService;
import se.vgregion.ifeed.service.util.MetadataItem;
import se.vgregion.ifeed.types.Metadata;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/metadata")
public class MetadataController {

    @Autowired
    private MetadataService metadataService;

    @Autowired
    private MetadataRepository metadataRepository;

    @GetMapping("/{id}")
    public Metadata get(@PathVariable("id") Long id) throws HttpRequestMethodNotSupportedException {
        return metadataRepository.findById(id).orElse(null);
    }

    @GetMapping
    public Iterable<Metadata> getAll(Pageable sorting) {
        return metadataRepository.findAll(sorting);
    }

    @GetMapping("/import")
    public List<Metadata> imp() {
        return metadataService.importFromConfiguredServiceToOwnDatabase();
    }

    @GetMapping("/grouped")
    public Map<String, List<MetadataItem>> grouped() {
        return metadataService.importFromConfiguredServiceToOwnDatabaseTemp();
    }

}
