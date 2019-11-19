package se.vgregion.ifeed.types;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Expose;
import com.google.gwt.core.shared.GwtIncompatible;
import org.apache.commons.lang.builder.CompareToBuilder;
import se.vgregion.dao.domain.patterns.entity.AbstractEntity;
import se.vgregion.ifeed.shared.DynamicTableDef;
import se.vgregion.ifeed.types.util.Junctor;

import javax.persistence.*;
import java.io.*;
import java.lang.reflect.Field;
import java.util.*;
import java.util.regex.Pattern;

@Entity
@Table(name = "vgr_ifeed")
@GwtIncompatible
public class IFeed extends AbstractEntity<Long> implements Serializable, Comparable<IFeed> {

    private static final long serialVersionUID = -2277251806545192506L;

    @Id
    @GeneratedValue
    protected Long id;

    @OneToMany(mappedBy = "ifeed", cascade = CascadeType.ALL)
    private final List<DynamicTableDef> dynamicTableDefs = new ArrayList<DynamicTableDef>();

    @Version
    private Long version;

    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "feed")
    protected Set<IFeedFilter> filters = new HashSet<>();

    private String name;

    @Temporal(TemporalType.TIMESTAMP)
    private Date timestamp = null;

    private String description;

    private String userId;

    @Transient
    private String creatorName;

    @ManyToOne
    @Expose(serialize = false, deserialize = true)
    private VgrDepartment department;

    @ManyToOne
    @Expose(serialize = false, deserialize = true)
    private VgrGroup group;

    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "ifeed")
    protected Set<Ownership> ownerships = new HashSet<>();

    @ManyToMany(/*cascade = {CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH}*/)
    protected List<IFeed> composites = new DistinctArrayList<>();

    @Expose(serialize = false, deserialize = true)
    @ManyToMany(mappedBy = "composites"/*, cascade = {CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH}*/)
    protected List<IFeed> partOf = new DistinctArrayList<>();

    private String sortField;

    private String sortDirection;

    private Boolean linkNativeDocument;

    public Set<IFeedFilter> getFilters() {
/*        if (filters == null) {
            return Collections.emptySet();
        }*/
        if (filters == null) filters = new HashSet<>();
        return filters;
    }

    public boolean addFilter(IFeedFilter filter) {
        if (filter == null) {
            throw new IllegalArgumentException();
        }
        if (filters == null) {
            filters = new HashSet<IFeedFilter>();
        } else if (filters.contains(filter)) {
            return false;
        }
        filters.add(filter);
        return true;
    }

    public IFeedFilter getFilter(IFeedFilter filter) {
        IFeedFilter f = null;
        List<IFeedFilter> filters = new ArrayList<IFeedFilter>(getFilters());
        int index = filters.indexOf(filter);
        if (index >= 0) {
            f = filters.get(index);
        }
        return f;
    }

    public void setFilters(Set<IFeedFilter> filters) {
        /*if (this.filters == null) {
            this.filters = new HashSet<IFeedFilter>();
        }
        this.filters.clear();
        this.filters.addAll(filters);*/
        this.filters = filters;
    }

    public void removeFilter(IFeedFilter filter) {
        filters.remove(filter);
    }

    public void removeFilter(int index) {
        List<IFeedFilter> temp = new ArrayList<IFeedFilter>(filters);
        temp.remove(index);
        filters.retainAll(temp);
    }

    public void removeFilters() {
        filters.clear();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getTimestamp() {
        if (timestamp == null) {
            return null;
        } else {
            return new Date(timestamp.getTime());
        }
    }

    public void clearTimestamp() {
        this.timestamp = null;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setTimestamp() {
        this.timestamp = new Date();
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public Long getId() {
        return id;
    }

    public String getSortDirection() {
        return sortDirection;
    }

    public String getSortField() {
        return sortField;
    }

    public void setSortDirection(String sortDirection) {
        this.sortDirection = sortDirection;
    }

    public void setSortField(String sortField) {
        this.sortField = sortField;
    }

    public void setVersion(Long version) {
        this.version = version;
    }

    public Long getVersion() {
        return version;
    }

    @Override
    public final int compareTo(final IFeed o) {
        if (o == null) {
            return +1;
        }
        return new CompareToBuilder().append(name, o.name).toComparison();
    }

    public Set<Ownership> getOwnerships() {
        return ownerships;
    }

    private void setOwnerships(Set<Ownership> ownerships) {
        this.ownerships = ownerships;
    }

    public String getOwnershipsText() {
        List<String> names = new ArrayList<String>();
        for (Ownership ownership : getOwnerships()) {
            names.add(ownership.getUserId());
        }
        names.remove(getUserId());
        names.add(0, getUserId());
        String text = names.toString();
        text = text.replaceAll(Pattern.quote("["), "").replaceAll(Pattern.quote("]"), "");
        return text;
    }

    public String getCreatorName() {
        return creatorName;
    }

    public void setCreatorName(String creatorName) {
        this.creatorName = creatorName;
    }

    public Boolean getLinkNativeDocument() {
        if (linkNativeDocument == null) {
            return false;
        }
        return linkNativeDocument;
    }

    public void setLinkNativeDocument(Boolean linkNativeDocument) {
        this.linkNativeDocument = linkNativeDocument;
    }

    public VgrDepartment getDepartment() {
        return department;
    }

    public void setDepartment(VgrDepartment department) {
        this.department = department;
    }

    public VgrGroup getGroup() {
        return group;
    }

    public void setGroup(VgrGroup group) {
        this.group = group;
    }

    @GwtIncompatible
    private String toJson() {
        try {
            return toJsonImpl();
        } catch (Exception e) {
            //  throw new RuntimeException(e);
            return e.getMessage();
        }

    }

    @GwtIncompatible
    private void gatherAllNestedFeeds(Set<IFeed> intoThis) {
        if (!intoThis.contains(this)) {
            intoThis.add(this);
            for (IFeed feed : composites) {
                feed.gatherAllNestedFeeds(intoThis);
            }
        }
    }

    public Set<IFeed> getAllNestedFeedsFlattly() {
        final Set<IFeed> result = new HashSet<IFeed>();
        gatherAllNestedFeeds(result);
        return result;
    }

    @GwtIncompatible
    static private <T> T copy(T instance) {
        ObjectOutputStream oos = null;
        ObjectInputStream ois = null;
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            oos = new ObjectOutputStream(baos);

            oos.writeObject(instance);
            oos.flush();
            ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
            ois = new ObjectInputStream(bais);
            return (T) ois.readObject();
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            try {
                oos.close();
                ois.close();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    @GwtIncompatible
    private String toJsonImpl() throws IOException, ClassNotFoundException {
        final Set<String> excludeFields = new HashSet<String>(Arrays.asList("iFeed", "ifeed", "ownerships",
                "department", "group", "ifeedDynamicTableDefs", "tableDef", "partOf"));
        Gson gson = new GsonBuilder().addSerializationExclusionStrategy(new ExclusionStrategy() {
            @Override
            public boolean shouldSkipField(FieldAttributes fieldAttributes) {
                return excludeFields.contains(fieldAttributes.getName());
            }

            @Override
            public boolean shouldSkipClass(Class<?> aClass) {
                return false;
            }
        }).create();

        if (!composites.isEmpty()) {
            // To handle circular dependencies
            IFeed container = new IFeed();
            container.getComposites().addAll(copy(getAllNestedFeedsFlattly()));
            long seq = 0;
            for (IFeed feed : container.getComposites()) {
                feed.getComposites().clear();
                feed.setId(seq--); // to make it unique
            }
            return gson.toJson(container);
        }

        return gson.toJson(this);
    }

    @GwtIncompatible
    public static void main(String[] args) throws IOException, ClassNotFoundException {
        FileInputStream fin = new
                FileInputStream("C:\\Users\\clalu4\\Downloads\\iFeed.obj.1");

        ObjectInputStream obj_in =
                new ObjectInputStream(fin);

        IFeed iFeed = (IFeed) obj_in.readObject();
        findCirkular(iFeed);
    }

    @GwtIncompatible
    static void findCirkular(Object currentNode) {
        try {
            findCirkular(currentNode, new ArrayList<String>(), new IdentityHashMap());
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @GwtIncompatible
    static void findCirkular(Object currentNode, List<String> stack, IdentityHashMap passed) throws IllegalAccessException {
        if (currentNode instanceof Collection) for (Object o : ((Collection) currentNode)) {
            findCirkular(o, stack, passed);
        }
        if (currentNode == null || currentNode.getClass().getName().startsWith("java.")) return;
        if (passed.containsKey(currentNode)) {
            // Nothing
        } else {
            passed.put(currentNode, null);
            Field[] fields = currentNode.getClass().getDeclaredFields();
            for (Field field : fields) {
                field.setAccessible(true);
                Object nextNode = field.get(currentNode);
                stack.add(field.getName());
                findCirkular(nextNode, stack, passed);
                if (!stack.isEmpty())
                    stack.remove(stack.size() - 1);
            }
        }
    }

    @GwtIncompatible
    public static IFeed fromJson(String ifeed) {
        try {
            Gson gson = new GsonBuilder().create();
            return gson.fromJson(ifeed, IFeed.class);
        } catch (Exception e) {
            return null;
        }
    }

    public List<DynamicTableDef> getDynamicTableDefs() {
        return dynamicTableDefs;
    }

    public List<IFeed> getComposites() {
        return composites;
    }

    private static class DistinctArrayList<E> extends ArrayList<E> {
        @Override
        public boolean add(E e) {
            if (!contains(e)) {
                return super.add(e);
            }
            return false;
        }

        @Override
        public void add(int index, E element) {
            if (!contains(element)) {
                return;
            }
            super.add(index, element);
        }

        @Override
        public boolean addAll(Collection<? extends E> c) {
            ArrayList<E> clone = new ArrayList<E>(c);
            clone.removeAll(this);
            return super.addAll(clone);
        }

        @Override
        public boolean addAll(int index, Collection<? extends E> c) {
            ArrayList<E> clone = new ArrayList<E>(c);
            clone.removeAll(this);
            return super.addAll(index, clone);
        }
    }

    @PreRemove
    void preRemove() {
        composites.clear();
        for (IFeed feet : partOf) {
            feet.getComposites().remove(this);
        }
        partOf.clear();
    }

    public List<IFeed> getPartOf() {
        return partOf;
    }

    @Transient
    boolean toStringRuns = false;

    @Override
    public String toString() {
        if (toStringRuns) {
            return super.hashCode() + "";
        }
        try {
            toStringRuns = true;
            return "IFeed{" +
                    "id=" + id +
                    ", dynamicTableDefs=" + dynamicTableDefs +
                    ", version=" + version +
                    ", filters=" + filters +
                    ", name='" + name + '\'' +
                    ", timestamp=" + timestamp +
                    ", description='" + description + '\'' +
                    ", userId='" + userId + '\'' +
                    ", creatorName='" + creatorName + '\'' +
                    ", department=" + department +
                    ", group=" + group +
                    ", ownerships=" + ownerships +
                    ", composites=" + composites +
                    // ", partOf=" + partOf +
                    ", sortField='" + sortField + '\'' +
                    ", sortDirection='" + sortDirection + '\'' +
                    ", linkNativeDocument=" + linkNativeDocument +
                    '}';
        } finally {
            toStringRuns = false;
        }
    }

    public String toQuery() {
        Junctor or = new Junctor(" OR ");
        Set<IFeed> flat = getAllNestedFeedsFlattly();
        for (IFeed feed : flat) {
            or.add(feed.toQueryImp());
        }
        return or.toQuery();
    }

    private String toQueryImp() {
        if (filters == null || filters.isEmpty()) {
            return "";
        }

        if (filters.size() == 1) {
            return filters.iterator().next().toQuery();
        }

        Junctor and = new Junctor(" AND ");
        Map<String, List<IFeedFilter>> keyToFilters = new HashMap<String, List<IFeedFilter>>() {
            @Override
            public List<IFeedFilter> get(Object key) {
                if (!containsKey(key)) {
                    put(String.valueOf(key), new ArrayList<IFeedFilter>());
                }
                return super.get(key);
            }
        };

        for (IFeedFilter filter : filters) {
            if (filter.isContainer()) {
                and.add(filter.toQuery());
            } else {
                keyToFilters.get(filter.getFilterKey()).add(filter);
            }
        }

        for (String key : keyToFilters.keySet()) {
            List<IFeedFilter> values = keyToFilters.get(key);
            if (values.size() > 1 && !values.get(0).isContainer()) {
                Junctor or = new Junctor(" OR ");
                for (IFeedFilter value : values) {
                    or.add(value.toQuery());
                }
                and.add(or.toQuery());
            } else {
                and.add(values.get(0).toQuery());
            }
        }

        return and.toQuery();
    }

}
