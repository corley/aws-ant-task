package it.corley.ant;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.simpledb.AmazonSimpleDB;
import com.amazonaws.services.simpledb.AmazonSimpleDBClient;
import com.amazonaws.services.simpledb.model.PutAttributesRequest;
import com.amazonaws.services.simpledb.model.ReplaceableAttribute;
import org.apache.tools.ant.BuildException;

import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.Vector;

public class SimpleDB extends AWSTask {
    private String domain;

    boolean fail = false;

    Vector<Attribute> attributes = new Vector<Attribute>();

    public void execute() {
        if (fail) throw new BuildException("Fail requested.");

        AWSCredentials credential = new BasicAWSCredentials(getKey(), getSecret());
        AmazonSimpleDB simpledb = new AmazonSimpleDBClient(credential);
        simpledb.setEndpoint(region);

        Collection<ReplaceableAttribute> attrs = new Vector<ReplaceableAttribute>();

        for (int i = 0; i < attributes.size(); i++) {
            ReplaceableAttribute at = new ReplaceableAttribute();
            at.setName(attributes.get(i).getName());
            at.setValue(attributes.get(i).getValue());

            attrs.add(at);
        }

        PutAttributesRequest request = new PutAttributesRequest();
        request.setDomainName(domain);
        request.setAttributes(attrs);

        Date date = new Date();
        SimpleDateFormat dateformat = new SimpleDateFormat("yyyyMMddkkmmss");
        request.setItemName(dateformat.format(date));

        simpledb.putAttributes(request);
    }

    public void setFail(boolean fail) {
        this.fail = fail;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public Attribute createAttribute() {
        Attribute attr = new Attribute();
        this.attributes.add(attr);

        return attr;
    }

    public class Attribute {
        private String value;
        private String name;

        public Attribute() {

        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }
}
