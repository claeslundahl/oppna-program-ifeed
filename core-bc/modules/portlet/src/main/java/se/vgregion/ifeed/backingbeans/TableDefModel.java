package se.vgregion.ifeed.backingbeans;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import se.vgregion.common.utils.BeanMap;
import se.vgregion.ifeed.shared.ColumnDef;
import se.vgregion.ifeed.shared.DynamicTableDef;
import se.vgregion.ifeed.shared.DynamicTableSortingDef;
import se.vgregion.ifeed.types.FieldInf;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Created by clalu4 on 2014-08-21.
 */
@Component(value = "tableDef")
@Scope("session")
public class TableDefModel extends DynamicTableDef {

    public TableDefModel() {
        super();
        setDefaultSortColumn("dc.title");
        setDefaultSortOrder("asc");
        setFontSize("auto");
    }

    @Value("#{app}")
    private Application app;

    @Value("#{navigationModelBean}")
    private NavigationModelBean navigationModelBean;

    public String toTableMarkup() {
        return toTableMarkupImpl();
    }

    public String toPostingTableMarkup() {
        return toTableMarkupImpl(getFeedId(), true);
    }

    public List<String> toTableMarkupWithLineBreaks() {
        String text = toTableMarkupImpl();
        String[] texts = text.split(Pattern.quote("\n"));
        return Arrays.asList(texts);
    }

    public String toRunnableInstanceTableMarkup() {
        return toTableMarkupImpl(app.getIFeedModelBean().toJson(), false);
    }

    private String yesOrNo(boolean b) {
        if (b) {
            return "yes";
        } else {
            return "no";
        }
    }


    private String toTableMarkupImpl() {
        return toTableMarkupImpl(getFeedId(), false);
    }

    private String toTableMarkupImpl(String textAsFeedId, boolean posting) {
        if (getColumnDefs().isEmpty()) {
            return "Lägg till kolumner för att få fram en kod.";
        }
        List<String> concat = new ArrayList<String>();
        for (ColumnDef columnDef : getColumnDefs()) {
            concat.add(join("|", columnDef.getName(), columnDef.getLabel().replace(",", "").replace("|", ""), columnDef.getAlignment(), columnDef.getWidth()));
        }
        String columns = join(concat, ",");

        String result = "<div \n" +
                "\tclass=\"ifeedDocList\" \n" +
                "\tcolumnes=\"" + columns + "\" \n" +
                "\tfontSize=\"" + format(getFontSize()) + "\" \n" +
                "\tdefaultsortcolumn=\"" + format(getDefaultSortColumn()) + "\" \n" +
                "\tdefaultsortorder=\"" + format(getDefaultSortOrder()) + "\" \n" +
                "\textraSortColumns=\"" + (getExtraSorting().toString().replace("\"", "&quot;")) + "\" \n" +
                "\tshowTableHeader=\"" + yesOrNo(isShowTableHeader()) + "\" \n" +
                "\tlinkOriginalDoc=\"" + yesOrNo(isLinkOriginalDoc()) + "\" \n" +
                "\tlimit=\"" + getMaxHitLimit() + "\" \n" +
                "\thiderightcolumn=\"" + yesOrNo(isHideRightColumn()) + "\" \n" +
                //"\tfeedid=\"" + getFeedId() + "\">\n" +
                "\tusepost=\"" + yesOrNo(posting) + "\" \n" +
                "\tfeedid='" + textAsFeedId + "'>\n" +
                "</div><noscript><iframe src='http://ifeed.vgregion.se/iFeed-web/documentlists/" + getFeedId() +
                "/?by=" + format(getDefaultSortColumn()) +
                "&dir=" + format(getDefaultSortOrder()) + "' id='iframenoscript' name='iframenoscript' " +
                "style='width: 100%; height: 400px' frameborder='0'>\n" +
                "</iframe>\n" +
                "</noscript>";

        return result;
    }

    @Override
    public String getFeedId() {
        if (app != null && app.getIFeedModelBean() != null) {
            return app.getIFeedModelBean().getId() + "";
        }
        return super.getFeedId();
    }

    private static <T> T format(T s) {
        if (s == null) {
            throw new NullPointerException();
        }
        return s;
    }

    public void createColumn() {
        // dc.title
        ColumnDef column = new ColumnDef();
        column.setAlignment("left");
        // FieldInf fieldTemplate = app.getFilters().get(0).getChildren().get(0);
        FieldInf fieldTemplate = getFirstBestFieldInf();
        column.setName(fieldTemplate.getId());
        column.setLabel(fieldTemplate.getName());
        column.setWidth("70");
        getColumnDefs().add(column);
    }

    private FieldInf getFirstBestFieldInf() {
        for (FieldInf fieldInf : app.getFieldSuitableForSorting()) {
            for (FieldInf child : fieldInf.getChildren()) {
                for (FieldInf grandChild : child.getChildren()) {
                    return grandChild;
                }
            }
        }
        throw new RuntimeException();
    }

    private FieldInf getFirstBestFieldInf(FieldInf fi) {
        if (isOkToUseInWebScript(fi)) {
            return fi;
        }
        for (FieldInf child : fi.getChildren()) {
            FieldInf r = getFirstBestFieldInf(child);
            if (r != null) {
                return r;
            }
        }
        return null;
    }

    private static boolean isOkToUseInWebScript(FieldInf fi) {
        return fi.isInHtmlView() && fi.getId() != null && !fi.getId().trim().equals("");
    }

    public void createExtraSortColumn() {
        DynamicTableSortingDef item = new DynamicTableSortingDef();
        FieldInf fieldTemplate = app.getFilters().get(0).getChildren().get(0);
        item.setName(fieldTemplate.getId());
        item.setDirection("asc");
        getExtraSorting().add(item);
        int i = 1;
        for (DynamicTableSortingDef sorting : getExtraSorting()) {
            sorting.setIndex(i++);
        }
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

    public void editFlow(DynamicTableDef dynamicTableDef) {
        new BeanMap(this).putAllApplicable(new BeanMap(dynamicTableDef));
        setColumnDefs(dynamicTableDef.getColumnDefs());
        navigationModelBean.setUiNavigation("EDIT_JSONP");
    }

    /*
    public DynamicTableDef toDynamicTableDef() {
        DynamicTableDef result = new TableDefModel();
        new BeanMap(result).putAllApplicable(new BeanMap(this));
        return result;
    }
    */

    public void editNewJsonp() {
        editFlow(new TableDefModel());
    }
}
