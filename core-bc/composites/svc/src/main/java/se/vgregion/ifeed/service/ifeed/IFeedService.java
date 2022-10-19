package se.vgregion.ifeed.service.ifeed;

import org.springframework.transaction.annotation.Transactional;
import se.vgregion.ifeed.shared.ColumnDef;
import se.vgregion.ifeed.shared.DynamicTableDef;
import se.vgregion.ifeed.types.*;

import java.util.*;

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

    FieldInf getFieldInf(String byId);

    Config getConfig(String withThatId);

    List<FieldInf> getFieldInfs();

    void storeFieldsInf(FieldsInf inf);

    Map<String, FieldInf> mapFieldInfToId();

    List<String> fetchFilterSuggestion(IFeed feed, FieldInf fieldId, String starFilter);

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

    public static String toTableMarkup(DynamicTableDef tableDef, boolean posting) {
        final Collection<ColumnDef> columnDefs = tableDef.getColumnDefs();

        if (columnDefs.isEmpty()) {
            return "Lägg till kolumner för att få fram en kod.";
        }
        List<String> concat = new ArrayList<String>();
        for (ColumnDef columnDef : columnDefs) {
            concat.add(join("|", columnDef.getName(), columnDef.getLabel().replace(",", "").replace("|", ""), columnDef.getAlignment(), columnDef.getWidth()));
        }
        String columns = join(concat, ",");

        String result = "<div \n" +
                "\tclass=\"ifeedDocList\" \n" +
                "\tcolumnes=\"" + columns + "\" \n" +
                "\tfontSize=\"" + format(tableDef.getFontSize()) + "\" \n" +
                "\tdefaultsortcolumn=\"" + format(tableDef.getDefaultSortColumn()) + "\" \n" +
                "\tdefaultsortorder=\"" + format(tableDef.getDefaultSortOrder()) + "\" \n" +
                "\textraSortColumns=\"" + (tableDef.getExtraSorting().toString().replace("\"", "&quot;")) + "\" \n" +
                "\tshowTableHeader=\"" + yesOrNo(tableDef.isShowTableHeader()) + "\" \n" +
                "\tlinkOriginalDoc=\"" + yesOrNo(tableDef.isLinkOriginalDoc()) + "\" \n" +
                "\tlimit=\"" + tableDef.getMaxHitLimit() + "\" \n" +
                "\thiderightcolumn=\"" + yesOrNo(tableDef.isHideRightColumn()) + "\" \n" +
                //"\tfeedid=\"" + getFeedId() + "\">\n" +
                "\tusepost=\"" + yesOrNo(posting) + "\" \n" +
                "\tfeedid='" + tableDef.getFeedId() + "'>\n" +
                "</div><noscript><iframe src='http://ifeed.vgregion.se/iFeed-web/documentlists/" + tableDef.getFeedId() +
                "/?by=" + format(tableDef.getDefaultSortColumn()) +
                "&dir=" + format(tableDef.getDefaultSortOrder()) + "' id='iframenoscript' name='iframenoscript' " +
                "style='width: 100%; height: 400px' frameborder='0'>\n" +
                "</iframe>\n" +
                "</noscript>";

        return result;
    }

    /**
     * Concatenates several strings and places another string between each of those.
     *
     * @param junctor what string to concatenate between the other parameters.
     * @param items   the different strings to be concatenated
     * @return as string product of the parameters.
     */
    public static String join(String junctor, String... items) {
        return join(Arrays.asList(items), junctor);
    }

    /**
     * Concatenates several strings and places another string between each of those.
     *
     * @param junctor what string to concatenate between the other parameters.
     * @param list    the different strings to be concatenated
     * @return as string product of the parameters.
     */
    public static String join(List<?> list, String junctor) {
        StringBuilder sb = new StringBuilder();
        if (list.isEmpty()) {
            return "";
        }
        if (list.size() == 1) {
            return list.get(0) + "";
        }

        for (int i = 0, j = list.size() - 1; i < j; i++) {
            sb.append(format(list.get(i)));
            sb.append(junctor);
        }
        sb.append(list.get(list.size() - 1));
        return sb.toString();
    }

    private static String yesOrNo(boolean b) {
        if (b) {
            return "yes";
        } else {
            return "no";
        }
    }

    private static <T> T format(T s) {
        if (s == null) {
            throw new NullPointerException();
        }
        return s;
    }

}
