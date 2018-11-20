package se.vgregion.ifeed.backingbeans;


import net.sf.cglib.beans.BeanMap;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import se.vgregion.common.utils.CommonUtils;
import se.vgregion.common.utils.Json;
import se.vgregion.ifeed.types.*;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.util.*;

/**
 * Created by Monica on 2014-03-28.
 */
@Component(value = "iFeedModelBean")
@Scope("session")
public class IFeedModelBean extends IFeed implements Serializable {

    private static final long serialVersionUID = -2277251806545192506L;

    private String ownershipUserIds;

    private IFeed initalFeed;

    public IFeedModelBean() {
        super();
    }

    private List<String> newOwnershipNames = new MirrorOwnershipToTextList(ownerships);

    private void copy(Object from, Object into) {
        BeanMap fromMap = BeanMap.create(from);
        BeanMap intoMap = BeanMap.create(into);
        intoMap.putAll(fromMap);
    }

    public void copyValuesFromIFeed(IFeed iFeed) {
        ownershipsAsList = null;
        filtersAsList = null;
        copy(iFeed, this);
        getOwnerships().clear();
        getOwnerships().addAll(iFeed.getOwnerships());
        newOwnershipNames = new MirrorOwnershipToTextList(getOwnerships());
        getDynamicTableDefs().clear();
        getDynamicTableDefs().addAll(iFeed.getDynamicTableDefs());
        long time = System.currentTimeMillis();
        VgrGroup group = iFeed.getGroup();
        System.out.println("Getting group takes " + (System.currentTimeMillis() - time));
        setGroup(group);
        setDepartment(iFeed.getDepartment());
        getComposites().clear();
        getComposites().addAll(iFeed.getComposites());
        getPartOf().clear();
        getPartOf().addAll(iFeed.getPartOf());
        setInitalFeed(iFeed);
        setSortDirection("asc");
    }

    //List for display purposes
    private final List<Ownership> ownershipList = new AbstractList<Ownership>() {
        @Override
        public int size() {
            return ownerships.size();
        }

        @Override
        public Ownership get(int index) {
            return new ArrayList<Ownership>(ownerships).get(index);
        }

        @Override
        public Iterator<Ownership> iterator() {
            return ownerships.iterator();
        }

        @Override
        public boolean add(Ownership ownership) {
            return !ownerships.contains(ownership);
        }

        @Override
        public void add(int index, Ownership element) {
            ownerships.add(element);
        }

        @Override
        public boolean addAll(Collection<? extends Ownership> c) {
            return ownerships.addAll(c);
        }

        @Override
        public boolean addAll(int index, Collection<? extends Ownership> c) {
            return ownerships.addAll(c);
        }
    };

    /**
     * Returns a list of Ownerships from the set of Ownerships
     */
    public List<Ownership> getOwnershipList() {
       /*if(ownerships != null) {
           for (Ownership ownership : ownerships) {
               ownershipList.add(ownership);
           }
       }*/
        return ownershipList;
    }

    public String getOwnershipUserIds() {
        String ownershipUserId = "";
        if (ownerships != null) {
            for (Ownership ownership : ownerships) {
                if (ownershipUserId == "") {
                    ownershipUserId = ownership.getUserId();
                } else {
                    ownershipUserId = ownershipUserId + ", " + ownership.getUserId();
                }
            }
        }
        return ownershipUserId;
    }

    public void setOwnershipUserIds(String ownershipUserIds) {
        this.ownershipUserIds = ownershipUserIds;
    }

    /**
     * Clears fields in IFeedModelBean so that the input fields
     * will be empty in the gui and no incorrect data will be saved.
     */
    public void clearBean() {
        BeanMap bm = BeanMap.create(this);

        for (Object key : bm.keySet()) {
            if (!"class".equals(key)) {
                try {
                    Class type = bm.getPropertyType((String) key);
                    if (!type.isPrimitive()) {
                        Object value = bm.get(key);
                        if (value instanceof Collection) {
                            ((Collection) value).clear();
                        } else {
                            bm.put(key, null);
                        }
                    } else {
                        if (type == Long.TYPE) {
                            bm.put(key, 0l);
                        }
                    }
                } catch (UnsupportedOperationException uoe) {
                    System.out.println("Key " + key + " caused UnsupportedOperationException");
                }
            }
        }
    }

    /*
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        IFeedModelBean that = (IFeedModelBean) o;

        if (id != null ? !id.equals(that.id) : that.id != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }*/

    public List<String> getNewOwnershipNames() {
        return newOwnershipNames;
    }

    public void setNewOwnershipNames(List<String> newOwnershipNames) {
        this.newOwnershipNames = newOwnershipNames;
    }

    public IFeed toIFeed() {
        IFeed iFeed = new IFeed();
        copy(this, iFeed);
        for (Ownership ownership : getOwnerships()) {
            Ownership no = new Ownership();
            copy(ownership, no);
            no.setIfeed(iFeed);
            no.setIfeedId(iFeed.getId());
            iFeed.getOwnerships().add(no);
        }
        /*iFeed.getOwnerships().addAll(getOwnerships());*/
        iFeed.setDepartment(getDepartment());
        iFeed.setGroup(getGroup());
        iFeed.getComposites().addAll(getComposites());


        iFeed.getDynamicTableDefs().addAll(getDynamicTableDefs());

        return iFeed;
    }

    public String toSerializedText() throws UnsupportedEncodingException {
        String result = encode(CommonUtils.toString(toIFeed()));
        return result;
    }

    public String toSerializedText(Serializable o) throws UnsupportedEncodingException {
        String result = encode(CommonUtils.toString(o));
        return result;
    }


    public static String encode(String input) {
        StringBuilder resultStr = new StringBuilder();
        for (char ch : input.toCharArray()) {
            if (isUnsafe(ch)) {
                resultStr.append('%');
                resultStr.append(toHex(ch / 16));
                resultStr.append(toHex(ch % 16));
            } else {
                resultStr.append(ch);
            }
        }
        return resultStr.toString();
    }

    private static char toHex(int ch) {
        return (char) (ch < 10 ? '0' + ch : 'A' + ch - 10);
    }

    private static boolean isUnsafe(char ch) {
        if (ch > 128 || ch < 0)
            return true;
        return " %$&+,/:;=?@<>#%".indexOf(ch) >= 0;
    }

    private List<IFeedFilter> filtersAsList;

    public List<IFeedFilter> getFiltersAsList() {
        if (filtersAsList == null) {
            filtersAsList = new CollectionAsList<IFeedFilter>(getFilters());
        }
        return filtersAsList;
    }

    private List<Ownership> ownershipsAsList;

    public List<Ownership> getOwnershipsAsList() {
        if (ownershipsAsList == null) {
            ownershipsAsList = new CollectionAsList<Ownership>(getOwnerships());
        }
        return ownershipsAsList;
    }

    @Override
    public void removeFilter(IFeedFilter filter) {
        super.removeFilter(filter);
        filtersAsList = new CollectionAsList<IFeedFilter>(getFilters());
    }

    @Override
    public void removeFilter(int index) {
        super.removeFilter(index);
        filtersAsList = new CollectionAsList<IFeedFilter>(getFilters());
    }

    @Override
    public boolean addFilter(IFeedFilter filter) {
        boolean result = super.addFilter(filter);
        filtersAsList = new CollectionAsList<IFeedFilter>(getFilters());
        return result;
    }

    @Override
    public void setDepartment(VgrDepartment department) {
        if (department == null) {
            setGroup(null);
        }
        super.setDepartment(department);
    }

    public IFeed getInitalFeed() {
        return initalFeed;
    }

    public void setInitalFeed(IFeed initalFeed) {
        this.initalFeed = initalFeed;
    }


    // @Override
    public String toJson() {
        return Json.toJson(toIFeed()); //.toJson();
    }

    public boolean hasAnyFilter() {
        Set<IFeedFilter> result = new HashSet<IFeedFilter>();
        gatherNestedFilters(this, new HashSet<IFeed>(), result);
        return !result.isEmpty();
    }

    private void gatherNestedFilters(IFeed iFeed, Set<IFeed> handled, Set<IFeedFilter> result) {
        if (handled.contains(iFeed)) {
            return; // Avoids infinite recursive calls.
        }
        handled.add(iFeed);
        result.addAll(iFeed.getFilters());
        for (IFeed composite : iFeed.getComposites()) {
            gatherNestedFilters(composite, handled, result);
        }
    }

}
