package it.corley.ant;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.services.simpledb.AmazonSimpleDB;
import com.amazonaws.services.simpledb.AmazonSimpleDBClient;
import org.apache.tools.ant.Project;

import java.util.HashMap;
import java.util.Map;

public class SimpleDBTask extends AWSTask {

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

  protected AmazonSimpleDB getAmazonSimpleDB() {
    AWSCredentials credentials = getCredentials();
    AmazonSimpleDB simpleDB = new AmazonSimpleDBClient(credentials);
    if (region != null) {
      String endpoint = getRegionEndpoint(region);
      simpleDB.setEndpoint(endpoint);
    }
    return simpleDB;
  }

  private String getRegionEndpoint(String region) {
    if (REGION_2_ENDPOINT.containsKey(region)) {
      return REGION_2_ENDPOINT.get(region);
    } else {
      log("Region " + region + " given but not found in the region to endpoint map. Will use it as an endpoint", Project.MSG_WARN);
      return region;
    }
  }

  public String getDomain() {
    return domain;
  }

  public void setDomain(String domain) {
    this.domain = domain;
  }
}
