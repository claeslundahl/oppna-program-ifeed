package se.vgregion.ifeed.types;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Arrays;

/**
 * @author Patrik Björk
 */
@Entity
@Table(name = "vgr_ifeed_user")
public class User {

    public User() {
        super();
    }

    public User(String id) {
        this();
        setId(id);
    }

    // Normally VGR ID
    @Id
    private String id;

    @Column
    private String firstName;

    @Column
    private String lastName;

    @Column
    private String mail;

    @Column
    private String displayName;

    @Column
    private byte[] thumbnailPhoto;

    @Column
    private Boolean inactivated = false;

    @Column
    private boolean admin;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setThumbnailPhoto(byte[] thumbnailPhoto) {
        this.thumbnailPhoto = thumbnailPhoto;
    }

    public byte[] getThumbnailPhoto() {
        return thumbnailPhoto;
    }

    public Boolean getInactivated() {
        return inactivated;
    }

    public void setInactivated(Boolean inactivated) {
        this.inactivated = inactivated;
    }

    public boolean isAdmin() {
        return admin;
    }

    public void setAdmin(boolean admin) {
        this.admin = admin;
    }


    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("User{");
        sb.append("id='").append(id).append('\'');
        sb.append(", firstName='").append(firstName).append('\'');
        sb.append(", lastName='").append(lastName).append('\'');
        sb.append(", mail='").append(mail).append('\'');
        sb.append(", displayName='").append(displayName).append('\'');
        sb.append(", thumbnailPhoto=").append(Arrays.toString(thumbnailPhoto));
        sb.append(", inactivated=").append(inactivated);
        sb.append(", admin=").append(admin);
        sb.append('}');
        return sb.toString();
    }
    
}