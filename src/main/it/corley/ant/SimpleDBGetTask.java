package it.corley.ant;

import com.amazonaws.services.simpledb.AmazonSimpleDB;
import com.amazonaws.services.simpledb.model.Attribute;
import com.amazonaws.services.simpledb.model.GetAttributesRequest;
import com.amazonaws.services.simpledb.model.GetAttributesResult;
import org.apache.tools.ant.BuildException;

import java.util.*;

public class SimpleDBGetTask extends SimpleDBTask {

  private String itemName;
  private String attributes;
  private String delimiter = ",";
  private String propertyPrefix;

  @Override
  public void execute() throws BuildException {
    validateConfiguration();

    AmazonSimpleDB simpleDB = getAmazonSimpleDB();
    GetAttributesRequest request = getAttributesRequest(itemName, getAttributeNames());
    GetAttributesResult attributesResult = simpleDB.getAttributes(request);
    List<Attribute> attributes = getAttributes(attributesResult, request.getAttributeNames());
    addProperties(attributes);
  }

  private void validateConfiguration() {
    if (itemName == null || itemName.length() == 0) {
      throw new BuildException("Missing value for parameter 'itemName'");
    }
    if (attributes == null || attributes.length() == 0) {
      throw new BuildException("Missing value for parameter 'attributes'");
    }
    if (propertyPrefix == null || propertyPrefix.length() == 0) {
      throw new BuildException("Missing value for parameter 'propertyPrefix'");
    }
  }

  private GetAttributesRequest getAttributesRequest(String itemName, Collection<String> attributeNames) {
    GetAttributesRequest request = new GetAttributesRequest();
    request.setDomainName(getDomain());
    request.setItemName(itemName);
    request.setAttributeNames(attributeNames);
    return request;
  }

  private List<Attribute> getAttributes(GetAttributesResult result, Collection<String> attributeNames) {
    List<Attribute> values = new LinkedList<Attribute>();
    for(String attributeName: attributeNames) {
      Attribute attribute = getAttribute(result, attributeName);
      values.add(attribute);
    }
    return values;
  }

  private Attribute getAttribute(GetAttributesResult result, String name) {
    for (Attribute attribute : result.getAttributes()) {
      if (attribute.getName().equals(name)) {
        return attribute;
      }
    }

    throw new BuildException(String.format("Cannot find attribute '%s' for item '%s'", name, itemName));
  }

  private void addProperties(List<Attribute> attributes) {
    for (Attribute attribute: attributes) {
      addProperty(propertyPrefix + "." + attribute.getName(), attribute.getValue());
    }
  }

  public String getItemName() {
    return itemName;
  }

  public void setItemName(String itemName) {
    this.itemName = itemName;
  }

  public String getAttributes() {
    return attributes;
  }

  public void setAttributes(String attributes) {
    this.attributes = attributes;
  }

  private List<String> getAttributeNames() {
    Collection<String> attrs = Arrays.asList(attributes.split(delimiter));
    return new LinkedList<String>(attrs);
  }

  public String getDelimiter() {
    return delimiter;
  }

  public void setDelimiter(String delimiter) {
    this.delimiter = delimiter;
  }

  public String getPropertyPrefix() {
    return propertyPrefix;
  }

  public void setPropertyPrefix(String propertyPrefix) {
    this.propertyPrefix = propertyPrefix;
  }
}
