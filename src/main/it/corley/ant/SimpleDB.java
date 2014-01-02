package it.corley.ant;

import com.amazonaws.services.simpledb.AmazonSimpleDB;
import com.amazonaws.services.simpledb.model.PutAttributesRequest;
import com.amazonaws.services.simpledb.model.ReplaceableAttribute;
import org.apache.tools.ant.BuildException;

import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.Vector;

public class SimpleDB extends SimpleDBTask {

    boolean fail = false;

    Vector<Attribute> attributes = new Vector<Attribute>();

    public void execute() {
        if (fail) throw new BuildException("Fail requested.");

        AmazonSimpleDB simpledb = getAmazonSimpleDB();

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
        request.setDomainName(getDomain());
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
