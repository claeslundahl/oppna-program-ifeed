package se.vgregion.ifeed.backingbeans;

import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.TreeNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import se.vgregion.ifeed.formbean.VgrOrganization;
import se.vgregion.ifeed.types.IFeed;
import se.vgregion.ifeed.types.IFeedFilter;
import se.vgregion.ldap.LdapSupportService;

import javax.annotation.PostConstruct;
import java.io.*;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;

@Component(value = "vgrOrganizationsHome")
@Scope("session")
public class VgrOrganizationsHome implements Serializable {
    // Kopierat från VgrOrganizationHome...

    private String lastHsaIdClicked;

    private TreeNode root = new DefaultTreeNode("Root", null);

    private List<VgrOrganization> organizations;

    @Autowired
    private LdapSupportService ldapOrganizationService;

    @PostConstruct
    public void init() {
        //organizations = getRootOrganizations();
        organizations = getRootOrganizations();
        toTreeNodes(organizations, root);
        getAllOrganizationsRoot();
    }

    public VgrOrganization findVgrOrganizationByHsaId(String id) {
        return findVgrOrganizationByHsaId(id, getAllOrganizationsRoot());
    }

    private VgrOrganization findVgrOrganizationByHsaId(String id, VgrOrganization organization) {
        if (organization == null) {
            return null;
        }
        if (id == null) {
            throw new NullPointerException();
        }
        if (id.equals(organization.getHsaIdentity())) {
            return organization;
        }
        for (VgrOrganization child : organization.getChildren()) {
            VgrOrganization result = findVgrOrganizationByHsaId(id, child);
            if (result != null) {
                return result;
            }
        }
        return null;
    }

    public List<VgrOrganization> getRootOrganizations() {
        VgrOrganization org = new VgrOrganization();
        org.setDn("Ou=org");
        List<VgrOrganization> result = ldapOrganizationService.findChildNodes(org);
        return result;
    }

    public void loadChildrenFlat(Application application, VgrOrganization ofThisOrg) {
        loadChildren(ofThisOrg);
        setLastHsaIdClicked(ofThisOrg.getHsaIdentity());
        int index = organizations.indexOf(ofThisOrg) + 1;
        for (VgrOrganization vgrOrganization : ofThisOrg.getChildren()) {
            vgrOrganization.setLevel(ofThisOrg.getLevel() + 1);
        }
        organizations.addAll(index, ofThisOrg.getChildren());
        initAddedValue(application, organizations);
    }

    void initAddedValue(Application application, List<VgrOrganization> vgrOrganizationOrgs) {
        IFeedModelBean iFeedModelBean = application.getIFeedModelBean();
        for (VgrOrganization organization : vgrOrganizationOrgs) {
            IFeedFilter filter = new IFeedFilter();
            filter.setFilterKey(organization.getHsaIdentity());
            filter.setFieldInf(application.getNewFilter());
            boolean selected = iFeedModelBean.getFilters().contains(filter);
            organization.setAdded(selected);

        }
    }

    public void closeChildrenFlat(VgrOrganization ofThisOrg) {
        //int index = organizations.indexOf(ofThisOrg);
        setLastHsaIdClicked(ofThisOrg.getHsaIdentity());
        ofThisOrg.setOpen(false);
        organizations.removeAll(ofThisOrg.getChildren());
        for (VgrOrganization vgrOrganization : ofThisOrg.getChildren()) {
            closeChildrenFlat(vgrOrganization);
        }
    }

    public void loadAllOrganizations(VgrOrganization ofThisOrg) {
        ofThisOrg.setOpen(true);
        loadChildrenImpl(ofThisOrg);
        for (VgrOrganization child : ofThisOrg.getChildren()) {
            loadAllOrganizations(child);
        }
    }

    private static VgrOrganization allOrganizationsRoot;

    public VgrOrganization getAllOrganizationsRoot() {
        if (allOrganizationsRoot == null) {
            long startTime = System.currentTimeMillis();
            if (!loadAllOrganizationsRootFromDiscCacheIfFileIsPresent()) {
                synchronized (this) {
                    allOrganizationsRoot = loadAllOrganizationsRoot();
                    long loadTimeSeconds = (System.currentTimeMillis() - startTime) / 1000;
                    System.out.println("Time to load organizations "
                            + loadTimeSeconds + " s ("
                            + (loadTimeSeconds / 60) + " m).");
                    persistAllOrganizationsRootToDiscCache();
                    return allOrganizationsRoot;
                }
            }
        }
        return allOrganizationsRoot;
    }

    /*
    private static Future<VgrOrganization> allOrganizationsRoot;

    public Future<VgrOrganization> getAllOrganizationsRoot() {
        if (allOrganizationsRoot == null) {
            ExecutorService executor = Executors.newSingleThreadExecutor();
            allOrganizationsRoot = executor.submit(new Callable<VgrOrganization>() {
                @Override
                public VgrOrganization call() throws Exception {
                    long startTime = System.currentTimeMillis();
                    VgrOrganization result = loadAllOrganizationsRoot();
                    long loadTimeSeconds = (System.currentTimeMillis() - startTime) / 1000;
                    System.out.println("Time to load organizations "
                            + loadTimeSeconds + " s ("
                            + (loadTimeSeconds / 60) + " m).");
                    return result;
                }
            });
        }
        return allOrganizationsRoot;
    }
*/

    private String getPathToAllOrganizationsRootCacheFile() throws MalformedURLException {
        /*ExternalContext ec = FacesContext.getCurrentInstance().getExternalContext();
        String url = ec.getRealPath("/WEB-INF/iFeedJsf-portlet.xml");
        String webXmlFileUrl = url;
        String f = webXmlFileUrl.substring(0, webXmlFileUrl.length() - "iFeedJsf-portlet.xml".length());
        //f = f.replace('/', File.separatorChar);
        f = f + "allOrganizationsRoot.obj";
        return f;*/
        String home = System.getProperty("user.home");
        return (home + File.separator + ".ifeed" + File.separator + "allOrganizationsRoot.obj");
    }

    void persistAllOrganizationsRootToDiscCache() {
        try {
            String f = getPathToAllOrganizationsRootCacheFile();
            File file = new File(f);
            if (file.exists()) {
                file.delete();
            }
            file.createNewFile();

            FileOutputStream fout = new FileOutputStream(f);
            ObjectOutputStream oos = new ObjectOutputStream(fout);
            oos.writeObject(allOrganizationsRoot);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    VgrOrganization getAllOrganizationsRootFromDiscCache() {
        try {
            String f = getPathToAllOrganizationsRootCacheFile();
            FileInputStream fin = new FileInputStream(f);
            ObjectInputStream ois = new ObjectInputStream(fin);
            Object obj = ois.readObject();
            return (VgrOrganization) obj;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    private boolean loadAllOrganizationsRootFromDiscCacheIfFileIsPresent() {
        try {
            String f = getPathToAllOrganizationsRootCacheFile();
            File file = new File(f);
            if (file.exists()) {
                allOrganizationsRoot = getAllOrganizationsRootFromDiscCache();
                return true;
            }
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    private VgrOrganization loadAllOrganizationsRoot() {
        final VgrOrganization result = new VgrOrganization();
        result.setDn("Ou=org");
        result.setOpen(true);
        loadAllOrganizations(result);
        return result;
    }

    private VgrOrganization findOrganizationByHsaId(String hsaId) {
        try {
            return findOrganizationByHsaIdImpl(hsaId, getAllOrganizationsRoot());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private VgrOrganization findOrganizationByHsaIdImpl(String hsaId, VgrOrganization currentToCheck) {
        if (hsaId.equals(currentToCheck.getHsaIdentity())) {
            return currentToCheck;
        }
        for (VgrOrganization child : currentToCheck.getChildren()) {
            VgrOrganization possibleResult = findOrganizationByHsaIdImpl(hsaId, child);
            if (possibleResult != null) {
                return possibleResult;
            }
        }
        return null;
    }

    public void loadChildren(VgrOrganization ofThisOrg) {
        ofThisOrg.setOpen(!ofThisOrg.isOpen());
        loadChildrenImpl(ofThisOrg);
    }

    public void loadChildrenImpl(VgrOrganization ofThisOrg) {

        if (!ofThisOrg.isOpen()) {
            //ofThisOrg.getChildren().clear();
        } else /*if (ofThisOrg.getChildren() == null || ofThisOrg.getChildren().isEmpty())*/ {
            VgrOrganization vo = new VgrOrganization();
            vo.setDn(ofThisOrg.getDn());
            List<VgrOrganization> result = ldapOrganizationService.findChildNodes(vo);
            for (VgrOrganization child : result) {
                VgrOrganization vo2 = new VgrOrganization();
                vo2.setDn(child.getDn());
                child.getChildren().addAll(ldapOrganizationService.findChildNodes(vo2));
                child.setLeaf(child.getChildren().isEmpty());
                //loadChildren(child);
            }
            ofThisOrg.getChildren().clear();
            ofThisOrg.getChildren().addAll(result);
            toTreeNodes(organizations, root);
            ofThisOrg.setLeaf(result.isEmpty());
        }
    }

    public TreeNode getRoot() {
        return root;
    }

    private List<TreeNode> toTreeNodes(List<VgrOrganization> organizations, TreeNode parent) {
        parent.getChildren().clear();
        List<TreeNode> result = new ArrayList<TreeNode>();
        for (final VgrOrganization vo : organizations) {
            DefaultTreeNode dtn = new DefaultTreeNode(vo, parent) {
                @Override
                public boolean isExpanded() {
                    return vo.isOpen();
                }
            };
            if (!vo.getChildren().isEmpty()) {
                toTreeNodes(vo.getChildren(), dtn);
                dtn.setExpanded(true);
            }
        }
        return result;
    }

    public int indexOfOrgInFeed(Application application, VgrOrganization organization) {
        try {
            if (organization == null) {
                return -1;
            }
            IFeed iFeed = application.getIFeedModelBean();
            int i = 0;
/*            if (iFeed == null) throw new NullPointerException();
            if (iFeed.getFilters() == null) throw new RuntimeException();
            if (organization == null) throw new RuntimeException();*/
            for (IFeedFilter iFeedFilter : iFeed.getFilters()) {
                if (equals(iFeedFilter.getFilterQuery(), organization.getHsaIdentity()) &&
                        equals(iFeedFilter.getFilterKey(), application.getNewFilter().getId())) {
                    return i;
                }
                i++;
            }
            return -1;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public void loadFilterQueryForDisplay(IFeedFilter filter) {
        filter.setFilterQueryForDisplay(findOrganizationByHsaId(filter.getFilterQuery()).getLabel());
    }

    public void toggleOrganizationCondition(Application application, VgrOrganization organization) {
        int index = indexOfOrgInFeed(application, organization);
        IFeed iFeed = application.getIFeedModelBean();
        if (index == -1) {
            IFeedFilter filter = new IFeedFilter(organization.getHsaIdentity(), application.getNewFilter().getId());
            filter.setFilterQueryForDisplay(organization.getLabel());
            filter.setFieldInf(application.getNewFilter());
            iFeed.addFilter(filter);
            loadFilterQueryForDisplay(filter);
        } else {
            iFeed.removeFilter(index);
        }
        setLastHsaIdClicked(organization.getHsaIdentity());
    }

    private boolean equals(Object o1, Object o2) {
        if (o1 == o2) {
            return true;
        }
        if (o1 == null || o2 == null) {
            return false;
        }
        return o1.equals(o2);
    }

    /*public String toJson() {
        return Json.toJson(getAllOrganizationsRoot());
    }*/

    public List<VgrOrganization> getOrganizations() {
        return organizations;
    }

    public void setOrganizations(List<VgrOrganization> organizations) {
        this.organizations = organizations;
    }

    public String getLastHsaIdClicked() {
        return lastHsaIdClicked;
    }

    public void setLastHsaIdClicked(String lastHsaIdClicked) {
        this.lastHsaIdClicked = lastHsaIdClicked;
    }
}