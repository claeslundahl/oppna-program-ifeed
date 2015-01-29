package se.vgregion.ifeed.backingbeans;
 
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.PostConstruct;

import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.TreeNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import se.vgregion.ifeed.formbean.VgrOrganization;
import se.vgregion.ldap.LdapSupportService;

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
        organizations = getRootOrganizations();
        toTreeNodes(organizations, root);
    }

    public List<VgrOrganization> getRootOrganizations() {
        VgrOrganization org = new VgrOrganization();
        org.setDn("Ou=org");
        List<VgrOrganization> result = ldapOrganizationService.findChildNodes(org);
        return result;
        /*loadChildren(org);
        return org.getChildren();*/
    }

    public void loadChildrenFlat(VgrOrganization ofThisOrg) {
        loadChildren(ofThisOrg);
        setLastHsaIdClicked(ofThisOrg.getHsaIdentity());
        int index = organizations.indexOf(ofThisOrg) + 1;
        for (VgrOrganization vgrOrganization : ofThisOrg.getChildren()) {
            vgrOrganization.setLevel(ofThisOrg.getLevel() + 1);
        }
        organizations.addAll(index, ofThisOrg.getChildren());
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

    public void loadChildren(VgrOrganization ofThisOrg) {
        ofThisOrg.setOpen(!ofThisOrg.isOpen());
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