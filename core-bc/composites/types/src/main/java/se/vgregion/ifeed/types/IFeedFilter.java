package se.vgregion.ifeed.types;

import com.google.gson.annotations.Expose;
import se.vgregion.dao.domain.patterns.entity.AbstractEntity;
import se.vgregion.ifeed.types.util.Junctor;

import javax.persistence.*;
import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.function.Consumer;

@Entity
@Table(name = "vgr_ifeed_filter")
public class IFeedFilter extends AbstractEntity<Long> implements Serializable {

    @Id
    @GeneratedValue
    private Long id;

    private static final long serialVersionUID = -8141707337621433677L;

    @Column
    private String filterQuery;

    @Column
    private String filterKey;

    @Column
    private String operator = "matching";

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "field_inf_pk")
    private FieldInf fieldInf;

    @Transient
    private Object filterQueryForDisplay;

    @Deprecated
    @Enumerated(EnumType.STRING)
    private FilterType.Filter filter;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ifeed_id")
    /*@ForeignKey(name = "fk_ifeed_filter_ifeed")*/
    @Expose(serialize = false, deserialize = true)
    private IFeed feed;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "parent_id")
    /*@ForeignKey(name = "fk_ifeed_filter_parent")*/
    @Expose(serialize = false, deserialize = true)
    private IFeedFilter parent;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "apelon_id")
    @Expose(serialize = false, deserialize = false)
    private Metadata metadata;

    @OneToMany(mappedBy = "parent", targetEntity = IFeedFilter.class, fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<IFeedFilter> children = new ArrayList<>();

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
        return this.filter;
    }

    public String getFilterQuery() {
        return this.filterQuery;
    }

    public void setFilterQuery(String filterQuery) {
        this.filterQuery = filterQuery;
    }

    public String getFilterKey() {
        return this.filterKey;
    }

    public void setFilterKey(String filterKey) {
        this.filterKey = filterKey;
    }

    public FieldInf getFieldInf() {
        return this.fieldInf;
    }

    public void setFieldInf(FieldInf fieldInf) {
        this.fieldInf = fieldInf;
    }

    public Object getFilterQueryForDisplay() {
        return this.filterQueryForDisplay;
    }

    public void setFilterQueryForDisplay(Object filterQueryForDisplay) {
        this.filterQueryForDisplay = filterQueryForDisplay;
    }

    public String getOperator() {
        return this.operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }

    public String toString() {
        return "IFeedFilter{filterQuery='" + this.filterQuery + '\'' + ", filterKey='" + this.filterKey + '\'' + ", operator='" + this.operator + '\'' + ", fieldInf=" + this.fieldInf + ", filterQueryForDisplay=" + this.filterQueryForDisplay + ", filter=" + this.filter + '}';
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public IFeed getFeed() {
        return this.feed;
    }

    public void setFeed(IFeed feed) {
        this.feed = feed;
    }

    public IFeedFilter getParent() {
        return this.parent;
    }

    public void setParent(IFeedFilter parent) {
        this.parent = parent;
    }

    public List<IFeedFilter> getChildren() {
        return this.children;
    }

    public void setChildren(List<IFeedFilter> children) {
        this.children = children;
    }

    public void initFieldInfs(Collection<FieldInf> infs) {
        for (FieldInf inf : infs) {
            if (inf.getId() != null && inf.getId().equals(getFilterKey()))
                setFieldInf(inf);
        }
        for (IFeedFilter child : this.children)
            child.initFieldInfs(infs);
    }

    public String toText() {
        String label = (getFieldInf() != null && getFieldInf().getName() != null) ? getFieldInf().getName() : this.filterKey;
        if (this.children.isEmpty()) {
            if (this.operator == null || this.operator.equals("matching") || this.operator.isEmpty())
                return label + " = " + this.filterQuery;
            if (this.operator.equals("greater"))
                return label + " > " + this.filterQuery;
            if (this.operator.equals("lesser"))
                return label + " < " + this.filterQuery;
        } else {
            String o = this.operator.equalsIgnoreCase("and") ? " OCH " : " ELLER ";
            Junctor ls = new Junctor(o);
            for (IFeedFilter child : this.children)
                ls.add(child.toText());
            return ls.toQuery();
        }
        return "";
    }

    static String toEmptyIfNull(String s) {
        if (s == null) {
            return "";
        }
        return s;
    }

    public String toQuery(List<Field> meta) {
        return toQueryImp(meta, this);
    }

    public static String toQueryImp(List<Field> meta, IFeedFilter iff) {
        String valueToLookFor; // = toEmptyIfNull((iff.getFieldInf() != null) ? iff.getFieldInf().getQueryPrefix() : "") + iff.getFilterQuery();
        FieldInf fi = iff.getFieldInf();
        if (fi != null) {
            if (fi.getQueryPrefix() != null && !"".equals(fi.getQueryPrefix())) {
                valueToLookFor = fi.getQueryPrefix() + "*" + iff.getFilterQuery();
                String result = escapeFieldName(iff.filterKey) + ":" + escapeValue(iff.getFilterKey(), valueToLookFor, iff.operator, fi);
                return result;
            }
            if ("d:text_fix".equals(fi.getType())) {
                Metadata md = iff.getMetadata();
                if (md != null && md.getFilterQuery() != null && !"".equals(md.getFilterQuery().trim())) {
                    valueToLookFor = md.getFilterQuery();

                    String result = escapeFieldName(iff.filterKey) + ":" + escapeValue(iff.getFilterKey(), valueToLookFor, iff.operator, fi);
                    return result;
                }
            }
        }

        List<IFeedFilter> children = iff.getChildren();
        String operator = iff.getOperator();
        String filterKey = iff.getFilterKey();
        String filterQuery = iff.getFilterQuery();
        if (children.isEmpty()) {
            boolean isMatchingDate = false;
            if (operator == null || operator.equals("matching") || operator.isEmpty()) {
                if (meta != null) {
                    Field field = meta.stream().filter(f -> f.getName().equals(filterKey)).findAny().orElse(null);
                    if (field != null) {
                        Field f1 = ((Field) meta.stream().filter(f -> f.getName().equals(filterKey)).findAny().orElse(null));
                        String type = f1.getType();
                        if (type != null && type.equals("tdate") || iff.getFieldInf() != null && iff.getFieldInf().getType().equals("d:date"))
                            isMatchingDate = true;
                    }
                }
                if (isMatchingDate) {
                    String textDate = escapeValue(filterKey, filterQuery, operator, iff.getFieldInf());
                    return escapeFieldName(filterKey) + ":[" + textDate + "T00:00:00Z TO " + textDate + "T23:59:59Z]";
                }
                return escapeFieldName(filterKey) + ":" + escapeValue(filterKey, filterQuery, operator, iff.getFieldInf());
            }
            if (operator.equals("greater"))
                return escapeFieldName(filterKey) + ":[" +
                        addZeroTimeToDateWhenApplicable(meta, filterKey, escapeValue(filterKey, filterQuery, operator, iff.getFieldInf())) + " TO *]";
            if (operator.equals("lesser"))
                return escapeFieldName(filterKey) + ":[* TO " +
                        addZeroTimeToDateWhenApplicable(meta, filterKey, escapeValue(filterKey, filterQuery, operator, iff.getFieldInf())) + "]";
        } else {
            String o = (operator == null || "and".equalsIgnoreCase(operator)) ? " AND " : " OR ";
            Junctor ls = new Junctor(o);
            for (IFeedFilter child : children)
                ls.add(child.toQuery(meta));
            return ls.toQuery();
        }
        return "";
    }

    private static String addZeroTimeToDateWhenApplicable(List<Field> meta, String key, String initialValue) {
        if (initialValue == null || meta == null)
            return initialValue;
        Field field = meta.stream().filter(f -> f.getName().equals(key)).findFirst().orElse(null);
        if (field == null) {
            // throw new RuntimeException("Field with k/v " + key + " / " + initialValue + " is null!");
        } else if ((field != null && "d:date".equals(field.getType()) || "tdate".equals(field.getType()) && !initialValue.endsWith("Z"))) {
            initialValue = initialValue + "T00:00:00Z";
        }
        return initialValue;
    }

    // static String keyRegex = "([+!\\(\\){}\\[\\]^\"~?:\\\\]|[&\\|]{2})";
    static String keyRegex = "([+\\-!\\(\\){}\\[\\]^\"~?:\\\\]|[&\\|]{2})";

    static String valueRegex = "([+!\\(\\){}\\[\\]^\"~?:\\\\]|[&\\|]{2})";

    @Deprecated
    public static String toUtcDateIfPossible(String dateStr) {
        SimpleDateFormat out = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        String[] dateFormats = {"yyyy-MM-dd", "MMM dd, yyyy hh:mm:ss Z"};
        for (String dateFormat : dateFormats) {
            try {
                return out.format((new SimpleDateFormat(dateFormat)).parse(dateStr));
            } catch (ParseException parseException) {
            }
        }
        return dateStr;
    }

    static boolean isSomeKindOfDate(String withKey) {
        if (withKey == null)
            return false;
        return withKey.contains("date");
    }

    static boolean isDigit(String s) {
        try {
            Integer.parseInt(s);
            return true;
        } catch (NumberFormatException nfe) {
            return false;
        }
    }

    private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

    static Date toDate(String yyyyMMdd) {
        try {
            return sdf.parse(yyyyMMdd);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

    static String toYyyyMmDd(Date date) {
        return sdf.format(date);
    }

    public static String escapeValue(String withKey, String forSolr, String andPurpose, FieldInf fieldInf) {
        if (forSolr == null)
            return "";
        if (isSomeKindOfDate(withKey) || fieldInf != null && fieldInf.getType() != null && fieldInf.getType().contains("date"))
            if (forSolr.startsWith("+") || (forSolr.startsWith("-") && forSolr.length() > 1 && isDigit(forSolr.substring(1)))) {
                Date now = new Date();
                long daysOff = Integer.parseInt(forSolr.substring(1));
                if (forSolr.startsWith("-"))
                    daysOff = -daysOff;
                daysOff *= 86400000L;
                Date otherDay = new Date(now.getTime() + daysOff);
                forSolr = sdf.format(otherDay);
            } else if (forSolr.matches("\\d{4}-\\d{2}-\\d{2}")) {
                if (andPurpose == null || andPurpose.equals("matching")) {
                    //forSolr = forSolr + "*";
                } else if (andPurpose.equals("greater")) {
                    Date date = toDate(forSolr);
                    date.setTime(date.getTime() - 86400000L);
                    forSolr = toYyyyMmDd(date) + "T00:00:00Z";
                } else if (andPurpose.equals("lesser")) {
                    Date date = toDate(forSolr);
                    date.setTime(date.getTime() + 86400000L);
                    forSolr = toYyyyMmDd(date) + "T00:00:00Z";
                } else {
                    forSolr = toUtcDateIfPossible(forSolr);
                }
            } else {
                forSolr = toUtcDateIfPossible(forSolr);
            }
        String escaped = forSolr.replaceAll(valueRegex, "\\\\$1").replace("/", "\\/");
        if (escaped.contains(" ")) {
            escaped = escaped.replace(" ", "\\ ");
            return escaped;
        }
        return escaped;
    }

    public static String escapeFieldName(String forSolr) {
        if (forSolr == null)
            return null;
        String escaped = forSolr.replaceAll(keyRegex, "\\\\$1");
        if (escaped.startsWith("\\-")) {
            escaped = "-" + escaped.substring(2);
        }
        return escaped;
    }

    public boolean isContainer() {
        if (this.operator == null || this.operator.isEmpty())
            return false;
        return (this.operator.equalsIgnoreCase("and") || this.operator.equalsIgnoreCase("or"));
    }

    public IFeedFilter() {
    }

    public Metadata getMetadata() {
        return metadata;
    }

    public void setMetadata(Metadata metadata) {
        this.metadata = metadata;
    }

    public void visit(Consumer<IFeedFilter> guest) {
        guest.accept(this);
        if (getChildren() != null) {
            for (IFeedFilter child : getChildren()) {
                child.visit(guest);
            }
        }
    }
}
