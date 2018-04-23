package net.spacive.apps.ejazdybackend.config;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.cognitoidp.AWSCognitoIdentityProvider;
import com.amazonaws.services.cognitoidp.AWSCognitoIdentityProviderClientBuilder;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@ConfigurationProperties(prefix = "cognito")
public class CognitoConfiguration {

    private String poolId;
    private String issuer;
    private String keyStorePath;
    private Map<String, String> groupRole;
    private String accessKey;
    private String secretKey;
    private String region;

    public String getPoolId() {
        return poolId;
    }

    public void setPoolId(String poolId) {
        this.poolId = poolId;
    }

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

    public Map<String, String> getGroupRoleMap() {
        return groupRole;
    }

    public void setGroupRole(Map<String, String> groupRole) {
        this.groupRole = groupRole;
    }

    public String getAccessKey() {
        return accessKey;
    }

    public void setAccessKey(String accessKey) {
        this.accessKey = accessKey;
    }

    public String getSecretKey() {
        return secretKey;
    }

    public void setSecretKey(String secretKey) {
        this.secretKey = secretKey;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    @Bean
    public AWSCognitoIdentityProvider awsCognitoIdentityProvider() {

        final AWSCredentials credentials = new BasicAWSCredentials(
                accessKey,
                secretKey
        );

        return AWSCognitoIdentityProviderClientBuilder.standard()
                .withCredentials(
                        new AWSStaticCredentialsProvider(credentials)
                )
                .withRegion(region)
                .build();
    }
}
