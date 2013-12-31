package it.corley.ant;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import org.apache.tools.ant.Task;


/**
 * Base class for all AWS tasks implemented. Defines common properties.
 *
 * @author TODO
 */
public class AWSTask extends Task {

    protected final String DEFAULT_END_POINT = "s3-eu-west-1.amazonaws.com";

    /**
     * AWS access key.
     */
    protected String key;

    /**
     * AWS secret key.
     */
    protected String secret;

    /**
     * AWS Region to be used. US by default.
     */
    protected String region = "US";

    public void setKey(String key) {
        this.key = key;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    protected String getKey() {
        return this.key;
    }

    protected String getSecret() {
        return this.secret;
    }

    protected AWSCredentials getCredentials() {
        if (this.key == null || this.key.length() == 0 ||
                this.secret == null || this.secret.length() == 0) {
            log("Using default AWS credentials provider chain (ignoring credentials from ant build file)");
            return new DefaultAWSCredentialsProviderChain().getCredentials();
        } else {
            log("Using credentials from ant build file");
            return new BasicAWSCredentials(this.key, this.secret);
        }
    }
}
