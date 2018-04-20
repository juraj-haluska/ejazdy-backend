package net.spacive.apps.ejazdybackend.security;

import java.util.UUID;

public class UserToken {

    private UserToken() { }

    private UUID uuid;

    public UUID getUuid() {
        return uuid;
    }

    public static final class Builder {
        private UUID uuid;

        public Builder() { }

        public Builder withUuid(UUID uuid) {
            this.uuid = uuid;
            return this;
        }

        public UserToken build() {
            UserToken userToken = new UserToken();
            userToken.uuid = this.uuid;
            return userToken;
        }
    }
}
