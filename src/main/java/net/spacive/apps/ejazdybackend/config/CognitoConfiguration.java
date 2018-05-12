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

/**
 * Configs related to Cognito service.
 *
 * @author  Juraj Haluska
 */
@Component
@ConfigurationProperties(prefix = "cognito")
public class CognitoConfiguration {

    /**
     * Id of user pool in cognito.
     */
    private String poolId;

    /**
     * Issuer - name of the user pool.
     */
    private String issuer;

    /**
     * Path to key store for validating jwts.
     */
    private String keyStorePath;

    /**
     * Map of groups and corresponding roles.
     */
    private Map<String, String> groupRole;

    /**
     * AWS access key
     */
    private String accessKey;

    /**
     * AWS secret key.
     */
    private String secretKey;

    /**
     * AWS region
     */
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

    /**
     * AWSCognitoIdentityProvider bean definition - this will allow us to
     * use AWSCognitoIdentityProvider with DI.
     *
     * @return new identity provider client.
     */
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
