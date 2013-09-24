package it.corley.ant;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.simpledb.AmazonSimpleDB;
import com.amazonaws.services.simpledb.AmazonSimpleDBClient;
import com.amazonaws.services.simpledb.model.PutAttributesRequest;
import com.amazonaws.services.simpledb.model.ReplaceableAttribute;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;

import java.text.SimpleDateFormat;
import java.util.*;

public class SimpleDB extends AWSTask {

    private static final Map<String, String> REGION_2_ENDPOINT = new HashMap<String, String>();

    static {
        REGION_2_ENDPOINT.put("EU", "sdb.eu-west-1.amazonaws.com");
        REGION_2_ENDPOINT.put("us-west-1", "sdb.us-west-1.amazonaws.com");
        REGION_2_ENDPOINT.put("us-west-2", "sdb.us-west-2.amazonaws.com");
        REGION_2_ENDPOINT.put("ap-southeast-1", "sdb.ap-southeast-1.amazonaws.com");
        REGION_2_ENDPOINT.put("ap-northeast-1", "sdb.ap-northeast-1.amazonaws.com");
        REGION_2_ENDPOINT.put("sa-east-1", "sdb.sa-east-1.amazonaws.com");
    }

    private String domain;

    boolean fail = false;

    Vector<Attribute> attributes = new Vector<Attribute>();

    public void execute() {
        if (fail) throw new BuildException("Fail requested.");

        AmazonSimpleDB simpledb = new AmazonSimpleDBClient(getCredentials());
        if (region != null) {
            if (REGION_2_ENDPOINT.containsKey(region)) {
                simpledb.setEndpoint(REGION_2_ENDPOINT.get(region));
            } else {
                log("Region " + region + " given but not found in the region to endpoint map. Will use it as an endpoint",
                        Project.MSG_WARN);
                simpledb.setEndpoint(region);
            }
        }

        Collection<ReplaceableAttribute> attrs = new Vector<ReplaceableAttribute>();

        for (Attribute attribute : attributes) {
            if (!attribute.isItemName()) {
                ReplaceableAttribute at = new ReplaceableAttribute();
                at.setName(attribute.getName());
                at.setValue(attribute.getValue());
                at.setReplace(!attribute.getAppend());
                attrs.add(at);
            }
        }

        PutAttributesRequest request = new PutAttributesRequest();
        request.setDomainName(domain);
        request.setAttributes(attrs);

        final String itemName = getItemName(attributes);
        request.setItemName(itemName);

        simpledb.putAttributes(request);
    }

    private String getItemName(Vector<Attribute> attributes) {
        for (Attribute attribute : attributes) {
            if (attribute.isItemName())
                return attribute.getValue();
        }

        Date date = new Date();
        SimpleDateFormat dateformat = new SimpleDateFormat("yyyyMMddkkmmss");
        return dateformat.format(date);
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
        private boolean append;

        public Attribute() {
          append = false;
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

        public boolean getAppend() {
            return append;
        }

        public void setAppend(boolean append) {
            this.append = append;
        }

        public boolean isItemName() {
            return "itemName()".equals(name);
        }
    }
}
