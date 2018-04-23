package net.spacive.apps.ejazdybackend.model;

public class CognitoUser {

    private String id;
    private String phone;
    private String email;
    private String userGroup;

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

    public static final class Builder {
        private String id;
        private String phone;
        private String email;
        private String userGroup;

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

        public CognitoUser build() {
            CognitoUser cognitoUser = new CognitoUser();
            cognitoUser.phone = this.phone;
            cognitoUser.id = this.id;
            cognitoUser.userGroup = this.userGroup;
            cognitoUser.email = this.email;
            return cognitoUser;
        }
    }
}
