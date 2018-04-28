package net.spacive.apps.ejazdybackend.model;

import java.util.Date;

public class CognitoUser {

    private String id;
    private String phone;
    private String email;
    private String userGroup;
    private String status;
    private String firstName;
    private String lastName;
    private Date createDate;
    private Date lastModifiedDate;

    private CognitoUser() {
    }

    public String getId() {
        return id;
    }

    public String getPhone() {
        return phone;
    }

    public String getEmail() {
        return email;
    }

    public String getUserGroup() {
        return userGroup;
    }

    public String getStatus() {
        return status;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public Date getLastModifiedDate() {
        return lastModifiedDate;
    }

    public static final class Builder {
        private String id;
        private String phone;
        private String email;
        private String userGroup;
        private String status;
        private String firstName;
        private String lastName;
        private Date createDate;
        private Date lastModifiedDate;

        public Builder() {
        }

        public Builder withId(String id) {
            this.id = id;
            return this;
        }

        public Builder withPhone(String phone) {
            this.phone = phone;
            return this;
        }

        public Builder withEmail(String email) {
            this.email = email;
            return this;
        }

        public Builder withUserGroup(String userGroup) {
            this.userGroup = userGroup;
            return this;
        }

        public Builder withStatus(String status) {
            this.status = status;
            return this;
        }

        public Builder withFirstName(String firstName) {
            this.firstName = firstName;
            return this;
        }

        public Builder withLastName(String lastName) {
            this.lastName = lastName;
            return this;
        }

        public Builder withCreateDate(Date createDate) {
            this.createDate = createDate;
            return this;
        }

        public Builder withLastModifiedDate(Date lastModifiedDate) {
            this.lastModifiedDate = lastModifiedDate;
            return this;
        }

        public CognitoUser build() {
            CognitoUser cognitoUser = new CognitoUser();
            cognitoUser.lastName = this.lastName;
            cognitoUser.createDate = this.createDate;
            cognitoUser.phone = this.phone;
            cognitoUser.lastModifiedDate = this.lastModifiedDate;
            cognitoUser.status = this.status;
            cognitoUser.firstName = this.firstName;
            cognitoUser.userGroup = this.userGroup;
            cognitoUser.id = this.id;
            cognitoUser.email = this.email;
            return cognitoUser;
        }
    }
}
