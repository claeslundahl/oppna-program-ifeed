package se.vgregion.ifeed.admin.identity;

import com.fasterxml.jackson.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LiferayUser {

    public LiferayUser() {
        super();
    }

    public LiferayUser(String screenName) {
        this();
        this.screenName = screenName;
    }

    private List<Role> roles = new ArrayList<>();

    public String getScreenName() {
        return screenName;
    }

    public void setScreenName(String screenName) {
        this.screenName = screenName;
    }

    public List<Role> getRoles() {
        return roles;
    }

    public void setRoles(List<Role> roles) {
        this.roles = roles;
    }


    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonPropertyOrder({
        "agreedToTermsOfUse",
        "comments",
        "companyId",
        "contactId",
        "createDate",
        "defaultUser",
        "emailAddress",
        "emailAddressVerified",
        "facebookId",
        "failedLoginAttempts",
        "firstName",
        "graceLoginCount",
        "greeting",
        "jobTitle",
        "languageId",
        "lastFailedLoginDate",
        "lastLoginDate",
        "lastLoginIP",
        "lastName",
        "ldapServerId",
        "lockout",
        "lockoutDate",
        "loginDate",
        "loginIP",
        "middleName",
        "modifiedDate",
        "openId",
        "portraitId",
        "reminderQueryAnswer",
        "reminderQueryQuestion",
        "screenName",
        "status",
        "timeZoneId",
        "userId",
        "uuid"
    })

    @JsonProperty("agreedToTermsOfUse")
    private Boolean agreedToTermsOfUse;
    @JsonProperty("comments")
    private String comments;
    @JsonProperty("companyId")
    private Integer companyId;
    @JsonProperty("contactId")
    private Integer contactId;
    @JsonProperty("createDate")
    private Integer createDate;
    @JsonProperty("defaultUser")
    private Boolean defaultUser;
    @JsonProperty("emailAddress")
    private String emailAddress;
    @JsonProperty("emailAddressVerified")
    private Boolean emailAddressVerified;
    @JsonProperty("facebookId")
    private Integer facebookId;
    @JsonProperty("failedLoginAttempts")
    private Integer failedLoginAttempts;
    @JsonProperty("firstName")
    private String firstName;
    @JsonProperty("graceLoginCount")
    private Integer graceLoginCount;
    @JsonProperty("greeting")
    private String greeting;
    @JsonProperty("jobTitle")
    private String jobTitle;
    @JsonProperty("languageId")
    private String languageId;
    @JsonProperty("lastFailedLoginDate")
    private Object lastFailedLoginDate;
    @JsonProperty("lastLoginDate")
    private Integer lastLoginDate;
    @JsonProperty("lastLoginIP")
    private String lastLoginIP;
    @JsonProperty("lastName")
    private String lastName;
    @JsonProperty("ldapServerId")
    private Integer ldapServerId;
    @JsonProperty("lockout")
    private Boolean lockout;
    @JsonProperty("lockoutDate")
    private Object lockoutDate;
    @JsonProperty("loginDate")
    private Integer loginDate;
    @JsonProperty("loginIP")
    private String loginIP;
    @JsonProperty("middleName")
    private String middleName;
    @JsonProperty("modifiedDate")
    private Integer modifiedDate;
    @JsonProperty("openId")
    private String openId;
    @JsonProperty("portraitId")
    private Integer portraitId;
    @JsonProperty("reminderQueryAnswer")
    private String reminderQueryAnswer;
    @JsonProperty("reminderQueryQuestion")
    private String reminderQueryQuestion;
    @JsonProperty("screenName")
    private String screenName;
    @JsonProperty("status")
    private Integer status;
    @JsonProperty("timeZoneId")
    private String timeZoneId;
    @JsonProperty("userId")
    private Integer userId;
    @JsonProperty("uuid")
    private String uuid;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("agreedToTermsOfUse")
    public Boolean getAgreedToTermsOfUse() {
        return agreedToTermsOfUse;
    }

    @JsonProperty("agreedToTermsOfUse")
    public void setAgreedToTermsOfUse(Boolean agreedToTermsOfUse) {
        this.agreedToTermsOfUse = agreedToTermsOfUse;
    }

    @JsonProperty("comments")
    public String getComments() {
        return comments;
    }

    @JsonProperty("comments")
    public void setComments(String comments) {
        this.comments = comments;
    }

    @JsonProperty("companyId")
    public Integer getCompanyId() {
        return companyId;
    }

    @JsonProperty("companyId")
    public void setCompanyId(Integer companyId) {
        this.companyId = companyId;
    }

    @JsonProperty("contactId")
    public Integer getContactId() {
        return contactId;
    }

    @JsonProperty("contactId")
    public void setContactId(Integer contactId) {
        this.contactId = contactId;
    }

    @JsonProperty("createDate")
    public Integer getCreateDate() {
        return createDate;
    }

    @JsonProperty("createDate")
    public void setCreateDate(Integer createDate) {
        this.createDate = createDate;
    }

    @JsonProperty("defaultUser")
    public Boolean getDefaultUser() {
        return defaultUser;
    }

    @JsonProperty("defaultUser")
    public void setDefaultUser(Boolean defaultUser) {
        this.defaultUser = defaultUser;
    }

    @JsonProperty("emailAddress")
    public String getEmailAddress() {
        return emailAddress;
    }

    @JsonProperty("emailAddress")
    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    @JsonProperty("emailAddressVerified")
    public Boolean getEmailAddressVerified() {
        return emailAddressVerified;
    }

    @JsonProperty("emailAddressVerified")
    public void setEmailAddressVerified(Boolean emailAddressVerified) {
        this.emailAddressVerified = emailAddressVerified;
    }

    @JsonProperty("facebookId")
    public Integer getFacebookId() {
        return facebookId;
    }

    @JsonProperty("facebookId")
    public void setFacebookId(Integer facebookId) {
        this.facebookId = facebookId;
    }

    @JsonProperty("failedLoginAttempts")
    public Integer getFailedLoginAttempts() {
        return failedLoginAttempts;
    }

    @JsonProperty("failedLoginAttempts")
    public void setFailedLoginAttempts(Integer failedLoginAttempts) {
        this.failedLoginAttempts = failedLoginAttempts;
    }

    @JsonProperty("firstName")
    public String getFirstName() {
        return firstName;
    }

    @JsonProperty("firstName")
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    @JsonProperty("graceLoginCount")
    public Integer getGraceLoginCount() {
        return graceLoginCount;
    }

    @JsonProperty("graceLoginCount")
    public void setGraceLoginCount(Integer graceLoginCount) {
        this.graceLoginCount = graceLoginCount;
    }

    @JsonProperty("greeting")
    public String getGreeting() {
        return greeting;
    }

    @JsonProperty("greeting")
    public void setGreeting(String greeting) {
        this.greeting = greeting;
    }

    @JsonProperty("jobTitle")
    public String getJobTitle() {
        return jobTitle;
    }

    @JsonProperty("jobTitle")
    public void setJobTitle(String jobTitle) {
        this.jobTitle = jobTitle;
    }

    @JsonProperty("languageId")
    public String getLanguageId() {
        return languageId;
    }

    @JsonProperty("languageId")
    public void setLanguageId(String languageId) {
        this.languageId = languageId;
    }

    @JsonProperty("lastFailedLoginDate")
    public Object getLastFailedLoginDate() {
        return lastFailedLoginDate;
    }

    @JsonProperty("lastFailedLoginDate")
    public void setLastFailedLoginDate(Object lastFailedLoginDate) {
        this.lastFailedLoginDate = lastFailedLoginDate;
    }

    @JsonProperty("lastLoginDate")
    public Integer getLastLoginDate() {
        return lastLoginDate;
    }

    @JsonProperty("lastLoginDate")
    public void setLastLoginDate(Integer lastLoginDate) {
        this.lastLoginDate = lastLoginDate;
    }

    @JsonProperty("lastLoginIP")
    public String getLastLoginIP() {
        return lastLoginIP;
    }

    @JsonProperty("lastLoginIP")
    public void setLastLoginIP(String lastLoginIP) {
        this.lastLoginIP = lastLoginIP;
    }

    @JsonProperty("lastName")
    public String getLastName() {
        return lastName;
    }

    @JsonProperty("lastName")
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    @JsonProperty("ldapServerId")
    public Integer getLdapServerId() {
        return ldapServerId;
    }

    @JsonProperty("ldapServerId")
    public void setLdapServerId(Integer ldapServerId) {
        this.ldapServerId = ldapServerId;
    }

    @JsonProperty("lockout")
    public Boolean getLockout() {
        return lockout;
    }

    @JsonProperty("lockout")
    public void setLockout(Boolean lockout) {
        this.lockout = lockout;
    }

    @JsonProperty("lockoutDate")
    public Object getLockoutDate() {
        return lockoutDate;
    }

    @JsonProperty("lockoutDate")
    public void setLockoutDate(Object lockoutDate) {
        this.lockoutDate = lockoutDate;
    }

    @JsonProperty("loginDate")
    public Integer getLoginDate() {
        return loginDate;
    }

    @JsonProperty("loginDate")
    public void setLoginDate(Integer loginDate) {
        this.loginDate = loginDate;
    }

    @JsonProperty("loginIP")
    public String getLoginIP() {
        return loginIP;
    }

    @JsonProperty("loginIP")
    public void setLoginIP(String loginIP) {
        this.loginIP = loginIP;
    }

    @JsonProperty("middleName")
    public String getMiddleName() {
        return middleName;
    }

    @JsonProperty("middleName")
    public void setMiddleName(String middleName) {
        this.middleName = middleName;
    }

    @JsonProperty("modifiedDate")
    public Integer getModifiedDate() {
        return modifiedDate;
    }

    @JsonProperty("modifiedDate")
    public void setModifiedDate(Integer modifiedDate) {
        this.modifiedDate = modifiedDate;
    }

    @JsonProperty("openId")
    public String getOpenId() {
        return openId;
    }

    @JsonProperty("openId")
    public void setOpenId(String openId) {
        this.openId = openId;
    }

    @JsonProperty("portraitId")
    public Integer getPortraitId() {
        return portraitId;
    }

    @JsonProperty("portraitId")
    public void setPortraitId(Integer portraitId) {
        this.portraitId = portraitId;
    }

    @JsonProperty("reminderQueryAnswer")
    public String getReminderQueryAnswer() {
        return reminderQueryAnswer;
    }

    @JsonProperty("reminderQueryAnswer")
    public void setReminderQueryAnswer(String reminderQueryAnswer) {
        this.reminderQueryAnswer = reminderQueryAnswer;
    }

    @JsonProperty("reminderQueryQuestion")
    public String getReminderQueryQuestion() {
        return reminderQueryQuestion;
    }

    @JsonProperty("reminderQueryQuestion")
    public void setReminderQueryQuestion(String reminderQueryQuestion) {
        this.reminderQueryQuestion = reminderQueryQuestion;
    }

    @JsonProperty("status")
    public Integer getStatus() {
        return status;
    }

    @JsonProperty("status")
    public void setStatus(Integer status) {
        this.status = status;
    }

    @JsonProperty("timeZoneId")
    public String getTimeZoneId() {
        return timeZoneId;
    }

    @JsonProperty("timeZoneId")
    public void setTimeZoneId(String timeZoneId) {
        this.timeZoneId = timeZoneId;
    }

    @JsonProperty("userId")
    public Integer getUserId() {
        return userId;
    }

    @JsonProperty("userId")
    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    @JsonProperty("uuid")
    public String getUuid() {
        return uuid;
    }

    @JsonProperty("uuid")
    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    @JsonAnyGetter
    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    @JsonAnySetter
    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

}
