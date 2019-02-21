package se.vgregion.ifeed.service.ifeed;

import java.util.List;
import java.util.Map;

import org.springframework.transaction.annotation.Transactional;
import se.vgregion.ifeed.shared.DynamicTableDef;
import se.vgregion.ifeed.types.*;

public interface IFeedService {

    List<IFeed> getIFeeds();

    List<VgrDepartment> getVgrDepartments();

    VgrDepartment getVgrDepartment(Long id);

    VgrDepartment save(VgrDepartment department);

    @Transactional
    void delete(VgrGroup group);

    @Transactional
    void delete(Object group);

    void delete(VgrDepartment department);

    List<IFeed> getUserIFeeds(String userId);


    @Transactional
    IFeed getFeedForSolrQuery(Long id);

    List<IFeed> getIFeedsByFilter(Filter filter, int start, int end);

    int getLatestFilterQueryTotalCount();

    IFeed getIFeed(Long id);

    IFeed addIFeed(IFeed iFeed);

    @Transactional
    VgrDepartment loadDepartment(Long id);

    @Transactional
    void removeIFeed(IFeed feed);

    @Transactional
    IFeed update(IFeed iFeed);

    @Transactional
    void saveDepartment(VgrDepartment department);

    IFeed updateIFeed(IFeed iFeed);

    void removeIFeed(Long id);

    List<FieldsInf> getFieldsInfs();

    Config getConfig(String withThatId);

    List<FieldInf> getFieldInfs();

    void storeFieldsInf(FieldsInf inf);

    Map<String, FieldInf> mapFieldInfToId();

    List<String> fetchFilterSuggestion(IFeed feed, String fieldId, String starFilter);

    <T> T findByPrimaryKey(Class<T> clazz, Object id);

    @Transactional
    void deleteDepartmentGroups(VgrDepartment department);

    @Transactional
    void deleteDepartmentEntity(VgrDepartment department);

    void save(DynamicTableDef instance);

    @Transactional
    IFeed copyAndPersistFeed(Long withThatKey, String otherUserId);

    String toDocumentPopupHtml(Map<String, Object> forThatItem);

    void save(Config inf);
}
