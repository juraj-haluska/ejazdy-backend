package net.spacive.apps.ejazdybackend.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import java.util.Map;

@ConfigurationProperties(prefix = "cognito")
public class CognitoConfiguration {

    private String issuer;
    private String keyStorePath;
    private Map<String, String> groupRole;

    public String getIssuer() {
        return issuer;
    }

    public void setIssuer(String issuer) {
        this.issuer = issuer;
    }

    public String getKeyStorePath() {
        return keyStorePath;
    }

    public void setKeyStorePath(String keyStorePath) {
        this.keyStorePath = keyStorePath;
    }

    public Map<String, String> getGroupRole() {
        return groupRole;
    }

    public void setGroupRole(Map<String, String> groupRole) {
        this.groupRole = groupRole;
    }
}
