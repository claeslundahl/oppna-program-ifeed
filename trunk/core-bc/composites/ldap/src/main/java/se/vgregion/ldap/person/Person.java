/**
 * 
 */
package se.vgregion.ldap.person;

import org.apache.commons.lang.builder.ToStringBuilder;

/**
 * @author anders
 * 
 */
public class Person {
    private String firstName;
    private String lastName;
    private String userName;
    private String organisation;
    private String email;

    public Person() {

    }

    public Person(String firstName, String lastName, String userName, String organisation, String email) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.userName = userName;
        this.organisation = organisation;
        this.email = email;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getUserName() {
        return userName;
    }

    public String getOrganisation() {
        return organisation;
    }

    public String getEmail() {
        return email;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

    public String getNiceName() {
        return handleNullPretty(getFirstName())
                + " " + handleNullPretty(getLastName())
                + " (" + handleNullPretty(getUserName()) + ")";
    }

    private String handleNullPretty(String s) {
        if (s == null) {
            return "";
        }
        return s;
    }

}
