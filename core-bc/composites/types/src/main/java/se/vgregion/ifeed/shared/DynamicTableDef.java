package se.vgregion.ifeed.shared;

import com.google.gson.annotations.Expose;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by clalu4 on 2014-08-21.
 */
@Entity
@Table(name = "vgr_ifeed_dynamic_table")
public class DynamicTableDef extends AbstractEntity {

    @Id
    @GeneratedValue
    protected Long id;
    private String name;

    private String fontSize;
    private String defaultSortColumn;
    private String defaultSortOrder;
    private String feedId;
    private String feedHome;

    @Column(name = "max_hit_limit")
    private Integer maxHitLimit = 0;

    private Boolean showTableHeader = true;
    private Boolean linkOriginalDoc = false;
    private Boolean hideRightColumn = false;

    @OneToMany(mappedBy = "tableDef", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ColumnDef> columnDefs = new ArrayList<ColumnDef>();

    @OneToMany(mappedBy = "tableDef", cascade = CascadeType.ALL)
    // @OrderBy("name index")
    private List<DynamicTableSortingDef> extraSorting = new ArrayList<DynamicTableSortingDef>();

    @ManyToOne
    @JoinColumn(name = "fk_ifeed_id")
    @Expose(serialize = false, deserialize = false)
    private se.vgregion.ifeed.types.IFeed ifeed;

    @Column(name = "fk_ifeed_id", updatable = false, insertable = false)
    private Long fkIfeedId;

    /**
     * Pass in text from the column element property 'columnes' (yes miss-spelled) - and the method will initialize
     * the objects in the columnDefs property of this object.
     *
     * @param fromText the text describing the values of the columnDefs collection.
     */
    public void createColumnDefs(String fromText) {
        String[] fragments = fromText.split("[,]");
        for (String fragment : fragments) {
            ColumnDef columnDef = new ColumnDef();
            columnDef.parseAndSet(fragment);
            columnDefs.add(columnDef);
        }
    }

    public String getFontSize() {
        return fontSize;
    }

    public void setFontSize(String fontSize) {
        this.fontSize = fontSize;
    }

    public String getDefaultSortColumn() {
        return defaultSortColumn;
    }

    public void setDefaultSortColumn(String defaultSortColumn) {
        this.defaultSortColumn = defaultSortColumn;
    }

    public String getDefaultSortOrder() {
        return defaultSortOrder;
    }

    public void setDefaultSortOrder(String defaultSortOrder) {
        this.defaultSortOrder = defaultSortOrder;
    }

    public String getFeedId() {
        if ((feedId == null || "".equals(feedId)) && getFkIfeedId() != null) {
            return getFkIfeedId().toString();
        }
        return feedId;
    }

    public boolean isFeedIdEmpty() {
        return  ((feedId == null || "".equals(feedId)) && getFkIfeedId() != null);
    }

    public void setFeedId(String feedId) {
        this.feedId = feedId;
    }

    public Integer getMaxHitLimit() {
        return maxHitLimit;
    }

    public void setMaxHitLimit(Integer maxHitLimit) {
        this.maxHitLimit = maxHitLimit;
    }

    public boolean isShowTableHeader() {
        return showTableHeader;
    }

    public void setShowTableHeader(boolean showTableHeader) {
        this.showTableHeader = showTableHeader;
    }

    public boolean isLinkOriginalDoc() {
        return linkOriginalDoc;
    }

    public void setLinkOriginalDoc(boolean linkOriginalDoc) {
        this.linkOriginalDoc = linkOriginalDoc;
    }

    public boolean isHideRightColumn() {
        return hideRightColumn;
    }

    public void setHideRightColumn(boolean hideRightColumn) {
        this.hideRightColumn = hideRightColumn;
    }

    public List<ColumnDef> getColumnDefs() {
        return columnDefs;
    }

    public void setColumnDefs(List<ColumnDef> columnDefs) {
        this.columnDefs = columnDefs;
    }

    public String getFeedHome() {
        return feedHome;
    }

    public void setFeedHome(String feedHome) {
        this.feedHome = feedHome;
    }

    @Override
    String toStringImpl() {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("defaultSortOrder", defaultSortOrder);
        map.put("showTableHeader", showTableHeader);
        // map.put("feedId", feedId);
        map.put("feedHome", feedHome);
        map.put("maxHitLimit", maxHitLimit);
        map.put("hideRightColumn", hideRightColumn);
        map.put("fontSize", fontSize);
        map.put("columnDefs", columnDefs);
        map.put("linkOriginalDoc", linkOriginalDoc);
        map.put("defaultSortColumn", defaultSortColumn);
        return map.toString();
    }

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    public Long getFkIfeedId() {
        return fkIfeedId;
    }

    public void setFkIfeedId(Long fkIfeedId) {
        this.fkIfeedId = fkIfeedId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public se.vgregion.ifeed.types.IFeed getIfeed() {
        return ifeed;
    }

    public void setIfeed(se.vgregion.ifeed.types.IFeed ifeed) {
        this.ifeed = ifeed;
    }

    public List<DynamicTableSortingDef> getExtraSorting() {
        return extraSorting;
    }

    public void setExtraSorting(List<DynamicTableSortingDef> extraSorting) {
        this.extraSorting = extraSorting;
    }

    /*
    public static void main(String[] args) {
        IfeedDynamicTableDef bean = new IfeedDynamicTableDef();
        BeanMap bm = new BeanMap(bean);
        StringBuilder sb = new StringBuilder();
        for (Object key : bm.keySet()) {
            sb.append("map.put(\"" + key + "\", " + key + ");\n");
        }
        System.out.println(sb);
    }
    */
}
