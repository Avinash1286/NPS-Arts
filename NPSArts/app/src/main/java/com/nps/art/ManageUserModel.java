package com.nps.art;

public class ManageUserModel {
    public ManageUserModel() {
    }
    private String profileLink,fullname,verified,AccountStatus;
    public String getProfileLink() {
        return profileLink;
    }
    public void setProfileLink(String profileLink) {
        this.profileLink = profileLink;
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public String getVerified() {
        return verified;
    }

    public void setVerified(String verified) {
        this.verified = verified;
    }

    public String getAccountStatus() {
        return AccountStatus;
    }

    public void setAccountStatus(String accountStatus) {
        AccountStatus = accountStatus;
    }
}
