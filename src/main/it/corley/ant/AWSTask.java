package it.corley.ant;

import org.apache.tools.ant.Task;


/**
 * Base class for all AWS tasks implemented. Defines common properties.
 *
 * @author TODO
 */
public class AWSTask extends Task {

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
}
