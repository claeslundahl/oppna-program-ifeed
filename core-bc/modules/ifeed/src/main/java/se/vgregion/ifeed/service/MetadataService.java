package se.vgregion.ifeed.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import se.vgregion.ifeed.repository.MetadataRepository;
import se.vgregion.ifeed.service.util.HttpCertificateClient;
import se.vgregion.ifeed.service.util.MetadataItem;
import se.vgregion.ifeed.types.Metadata;

import javax.transaction.Transactional;
import java.net.http.HttpResponse;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class MetadataService {

    @Value("${metadata.service.certificate.file}")
    private String metadataCertificateFile;

    @Value("${metadata.service.certificate.password}")
    private String metadataCertificatePassword;

    @Value("${metadata.service.url}")
    private String metadataUrl;

    @Value("${metadata.service.items}")
    private String[] metadataItems;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    private MetadataRepository metadataRepository;

    Metadata getOrCreateMetadata(String withName) {
        return getOrCreateMetadata(new Metadata(withName));
    }

    Metadata getOrCreateMetadata(Metadata that) {
        List<Metadata> fromDb = metadataRepository.findByName(that.getName());
        if (fromDb == null || fromDb.isEmpty()) {
            return metadataRepository.save(that);
        }
        return fromDb.get(0);
    }

    public Map<String, List<MetadataItem>> importFromConfiguredServiceToOwnDatabaseTemp() {
        for (String metadataItem : metadataItems) {
            List<MetadataItem> fromService = fetchMetadataItemsFromRemoteService(metadataItem);
            Map<String, List<MetadataItem>> byName = fromService.stream().collect(Collectors.groupingBy(MetadataItem::getName));
            byName.keySet().removeIf(k -> byName.get(k).size() == 1);
            if (true) return byName;

        }
        return null;
    }

    @Scheduled(fixedDelay = 1000 * 60 * 60 * 60 * 24, initialDelay = 1000)
    @Transactional
    public List<Metadata> importFromConfiguredServiceToOwnDatabase() {
        List<Metadata> result = new ArrayList<>();
        for (String metadataItem : metadataItems) {
            System.out.println("metadataItem: " + metadataItem);
            Metadata trunk = getOrCreateMetadata(metadataItem);

            System.out.println("Hittade " + trunk.getChildren().size() + " saker i databasen.");

            List<Metadata> fromService = fetchMetadataItemsFromRemoteService(metadataItem)
                    .stream().map(mi -> mi.toMetadata()).collect(Collectors.toList());

            System.out.println("Hittade " + fromService.size() + " saker i tjänsten.");

            Map<String, List<Metadata>> byName = fromService.stream().collect(Collectors.groupingBy(Metadata::getName));

            Set<String> namesFromService = fromService.stream().map(fs -> fs.getName().toLowerCase().trim()).collect(Collectors.toSet());
            trunk.getChildren().removeIf(c -> !namesFromService.contains(c.getName().toLowerCase().trim()) && c.getFilters().isEmpty());
            trunk.getChildren().forEach(c -> c.setActive(namesFromService.contains(c.getName().toLowerCase().trim())));

            Set<String> namesFromDb = trunk.getChildren().stream()
                    .map(c -> c.getName().toLowerCase().trim()).collect(Collectors.toSet());

            byName.keySet().stream().filter(k->!namesFromDb.contains(k.toLowerCase().trim()))
                    .forEach(k -> trunk.getChildren().add(byName.get(k).get(0)));

            /*byName.entrySet().stream().map(es -> es.getValue().get(0))
                    .filter(fdb -> !namesFromDb.contains(fdb.getName().toLowerCase().trim()))
                    .forEach(fdb -> trunk.getChildren().add(fdb));*/

            if (!trunk.getChildren().isEmpty()) {
                metadataRepository.save(trunk);
                result.add(trunk);
            }
        }
        return result;
    }

    static List<Metadata> toMetadata(Collection<MetadataItem> fromThese) {
        List<Metadata> result = fromThese.stream().map(mi -> mi.toMetadata()).collect(Collectors.toList());
        return result;
    }

    List<MetadataItem> fetchMetadataItemsFromRemoteService(String metadataItem) {
        try {
            List<MetadataItem> result = new ArrayList<>();
            HttpCertificateClient hcc = new HttpCertificateClient(metadataCertificateFile, metadataCertificatePassword);
            HttpResponse<String> response = hcc.get(metadataUrl, "glossary", metadataItem);
            JsonNode jsonNode = objectMapper.readTree(response.body());
            jsonNode.forEach(thing -> result.add(toMetadataItem(thing.toString())));
            return result;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    MetadataItem toMetadataItem(String fromThatJson) {
        try {
            if (fromThatJson == null || fromThatJson.trim().equals(""))
                return null;
            return objectMapper.readValue(fromThatJson, MetadataItem.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }


}
