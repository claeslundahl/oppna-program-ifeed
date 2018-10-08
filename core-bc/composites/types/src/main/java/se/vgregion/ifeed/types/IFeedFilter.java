package se.vgregion.ifeed.types;

import com.google.gson.annotations.Expose;
import org.hibernate.annotations.ForeignKey;
import se.vgregion.dao.domain.patterns.entity.AbstractEntity;
import se.vgregion.ifeed.types.util.Junctor;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Entity
@Table(name = "vgr_ifeed_filter")
public final class IFeedFilter extends AbstractEntity<Long> implements Serializable {

    @Id
    @GeneratedValue
    private Long id;

    private static final long serialVersionUID = -8141707337621433677L;

    @Column
    private String filterQuery;

    private String filterKey;

    private String operator = "matching"; // matching | greater | lesser

    @Transient
    private FieldInf fieldInf;

    @Transient
    private Object filterQueryForDisplay;

    @Deprecated
    @Enumerated(EnumType.STRING)
    private FilterType.Filter filter;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ifeed_id")
    @ForeignKey(name = "fk_ifeed_filter_ifeed")
    @Expose(serialize = false, deserialize = true)
    private IFeed feed;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    @ForeignKey(name = "fk_ifeed_filter_parent")
    @Expose(serialize = false, deserialize = true)
    private IFeedFilter parent;

    @OneToMany(mappedBy = "parent", targetEntity = IFeedFilter.class, fetch = FetchType.LAZY)
    private List<IFeedFilter> children = new ArrayList<IFeedFilter>();

    public IFeedFilter() {
        // To make Hibernate happy
    }

    public IFeedFilter(FilterType.Filter filter, String filterQuery, String filterKey) {
        this.filter = filter;
        this.filterQuery = filterQuery;
        this.filterKey = filterKey;
    }

    public IFeedFilter(String filterQuery, String filterKey) {
        this.filterQuery = filterQuery;
        this.filterKey = filterKey;
    }

    public FilterType.Filter getFilter() {
        return filter;
    }

    public String getFilterQuery() {
        return filterQuery;
    }

    public void setFilterQuery(String filterQuery) {
        this.filterQuery = filterQuery;
    }

    public String getFilterKey() {
        return filterKey;
    }

    public void setFilterKey(String filterKey) {
        this.filterKey = filterKey;
    }

    public FieldInf getFieldInf() {
        return fieldInf;
    }

    public void setFieldInf(FieldInf fieldInf) {
        this.fieldInf = fieldInf;
    }

    public Object getFilterQueryForDisplay() {
        return filterQueryForDisplay;
    }

    public void setFilterQueryForDisplay(Object filterQueryForDisplay) {
        this.filterQueryForDisplay = filterQueryForDisplay;
    }

    public String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }

    @Override
    public String toString() {
        return "IFeedFilter{" +
                "filterQuery='" + filterQuery + '\'' +
                ", filterKey='" + filterKey + '\'' +
                ", operator='" + operator + '\'' +
                ", fieldInf=" + fieldInf +
                ", filterQueryForDisplay=" + filterQueryForDisplay +
                ", filter=" + filter +
                '}';
    }

    public Long getId() {
        return id;
    }


    public void setId(Long id) {
        this.id = id;
    }

    public IFeed getFeed() {
        return feed;
    }

    public void setFeed(IFeed feed) {
        this.feed = feed;
    }

    public IFeedFilter getParent() {
        return parent;
    }

    public void setParent(IFeedFilter parent) {
        this.parent = parent;
    }

    public List<IFeedFilter> getChildren() {
        return children;
    }

    public void setChildren(List<IFeedFilter> children) {
        this.children = children;
    }

    public void initFieldInfs(Collection<FieldInf> infs) {
        for (FieldInf inf : infs) {
            if (inf.getId().equals(getFilterKey())) {
                setFieldInf(inf);
                continue;
            }
        }
        for (IFeedFilter child : children) {
            child.initFieldInfs(infs);
        }
    }

    public String toText() {
        String label = (getFieldInf() != null && getFieldInf().getName() != null) ? getFieldInf().getName() : filterKey;
        if (children.isEmpty()) {
            if (operator == null || operator.equals("matching") || operator.isEmpty()) {
                return label + " = " + (filterQuery);
            } else {
                if (operator.equals("greater")) {
                    return label + " > " + (filterQuery);
                } else if (operator.equals("lesser")) {
                    return label + " < " + (filterQuery);
                }
            }
        } else {
            String o = operator.equalsIgnoreCase("and") ? " OCH " : " ELLER ";
            Junctor ls = new Junctor(o);
            for (IFeedFilter child : children) {
                ls.add(child.toText());
            }
            return ls.toQuery();
        }
        return "";
    }

    public String toQuery() {
        if (children.isEmpty()) {
            if (operator == null || operator.equals("matching") || operator.isEmpty()) {
                return escapeFieldName(filterKey) + ":" + escapeValue(filterQuery);
            } else {
                if (operator.equals("greater")) {
                    return escapeFieldName(filterKey) + ":[" + escapeValue(filterQuery) + "TO *]";
                } else if (operator.equals("lesser")) {
                    return escapeFieldName(filterKey) + ":[* TO " + escapeValue(filterQuery) + "]";
                }
            }
        } else {
            String o = operator.equalsIgnoreCase("and") ? " AND " : " OR ";
            Junctor ls = new Junctor(o);
            for (IFeedFilter child : children) {
                ls.add(child.toQuery());
            }
            return ls.toQuery();
        }
        return "";
    }

    static String keyRegex = "([+\\-!\\(\\){}\\[\\]^\"~?:\\\\]|[&\\|]{2})";

    static String valueRegex = "([+!\\(\\){}\\[\\]^\"~?:\\\\]|[&\\|]{2})";

    static String escapeValue(String forSolr) {
        String escaped = forSolr.replaceAll(valueRegex, "\\\\$1");
        /*if (escaped.contains(" ")) {
            return String.format("\"%s\"", escaped);
        } else {
            return escaped;
        }*/
        return escaped;
    }

    static String escapeFieldName(String forSolr) {
        String escaped = forSolr.replaceAll(keyRegex, "\\\\$1");
        // return myEscapedString;
        /*if (escaped.contains(" ")) {
            return String.format("\"%s\"", escaped);
        } else {
            return escaped;
        }*/
        return escaped;
    }

    public boolean isContainer() {
        if (operator == null || operator.isEmpty())
            return false;
        return (operator.equalsIgnoreCase("and") || operator.equalsIgnoreCase("or"));
    }

}
