package se.vgregion.ifeed.formbean;

import se.vgregion.ldap.BaseVgrOrganization;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class VgrOrganization extends BaseVgrOrganization {

    private boolean open;

    private boolean added;

    private int level;

    private transient VgrOrganization parent;

    public VgrOrganization() {
        super();
        /*id = new Id(this);*/
    }

    public VgrOrganization(String dn, String hsaIdentity) {
        super(dn, hsaIdentity);
    }

    /*private Id id;*/

    public String getLabel() {
        if (getOu() != null) {
            return getOu();
        }
        return getCn();
    }

    private final VgrOrganization me = this;

    private final List<VgrOrganization> children = new ArrayList<VgrOrganization>() {

        @Override
        public boolean add(VgrOrganization organization) {
            organization.setParent(me);
            return super.add(organization);
        }

        @Override
        public void add(int index, VgrOrganization element) {
            element.setParent(me);
            super.add(index, element);
        }

        @Override
        public boolean addAll(Collection<? extends VgrOrganization> c) {
            for (VgrOrganization organization : c) {
                organization.setParent(me);
            }
            return super.addAll(c);
        }

        @Override
        public boolean addAll(int index, Collection<? extends VgrOrganization> c) {
            for (VgrOrganization organization : c) {
                organization.setParent(me);
            }
            return super.addAll(index, c);
        }

    };

    /*
    public String getId() {
        String result = getDn();
        if (result != null) {
            result = result.replaceAll(Pattern.quote(","), " ");
        }
        if (getLabel() != null) {
            result += "LABEL" + getLabel();
        }
        return result;
    }*/

    public String getQuery() {
        return getHsaIdentity(); // + "|" + getLabel();
    }

    /*public String getId() {
        return Json.toJson(id);
    }*/

    public List<VgrOrganization> getChildren() {
        return children;
    }

    public boolean isOpen() {
        return open;
    }

    public void setOpen(boolean open) {
        this.open = open;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public boolean isAdded() {
        return added;
    }

    public void setAdded(boolean added) {
        this.added = added;
    }

    public VgrOrganization getParent() {
        return parent;
    }

    public void setParent(VgrOrganization parent) {
        this.parent = parent;
    }

/*    public static class Id extends BaseVgrOrganization {

        private VgrOrganization parent;

        public Id(VgrOrganization parent) {
            this.parent = parent;
        }

        public String getLabel() {
            return parent.getLabel();
        }

        @Override
        public String getHsaIdentity() {
            return parent.getHsaIdentity();
        }

    }*/

    public void init() {
        setLeaf(children.isEmpty());
        children.forEach(c -> {
            c.setParent(VgrOrganization.this);
            c.init();
        });
    }

}
