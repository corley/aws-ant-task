package it.corley.ant;

import com.amazonaws.services.ec2.AmazonEC2Client;
import com.amazonaws.services.ec2.model.*;
import org.apache.tools.ant.Project;

import java.util.*;

public class EC2DescribeInstances extends AWSTask {

    private static final Map<String, String> REGION_2_ENDPOINT = new HashMap<String, String>();

    static {
        REGION_2_ENDPOINT.put("EU", "ec2.eu-west-1.amazonaws.com");
        REGION_2_ENDPOINT.put("us-west-1", "ec2.us-west-1.amazonaws.com");
        REGION_2_ENDPOINT.put("us-west-2", "ec2.us-west-2.amazonaws.com");
        REGION_2_ENDPOINT.put("ap-southeast-1", "ec2.ap-southeast-1.amazonaws.com");
        REGION_2_ENDPOINT.put("ap-northeast-1", "ec2.ap-northeast-1.amazonaws.com");
        REGION_2_ENDPOINT.put("sa-east-1", "ec2.sa-east-1.amazonaws.com");
    }


    private List<Filter> filters = new LinkedList<Filter>();

    private String property;

    private String select;
    private List<String> resultList;

    /**
     * Executes the task.
     *
     * @see org.apache.tools.ant.Task#execute()
     */
    public void execute() {
        validateConfiguration();

        AmazonEC2Client ec2Client = new AmazonEC2Client(getCredentials());

        if (region != null) {
            String endpoint = getEndpoint();
            log(String.format("Getting ec2 instances from %s", endpoint), Project.MSG_INFO);
            ec2Client.setEndpoint(endpoint);
        } else {
            log(String.format("Getting ec2 instances from default endpoint (task doesn't use environment)"), Project.MSG_WARN);
        }

        DescribeInstancesRequest request = new DescribeInstancesRequest();

        for (Filter filter : filters) {
            request.getFilters().add(convertFilter(filter));
        }

        DescribeInstancesResult dinResult = ec2Client.describeInstances(request);

        resultList = getResults(dinResult);

        if (property != null)
            addProperty(property, resultList);
    }

    private com.amazonaws.services.ec2.model.Filter convertFilter(Filter filter) {
        return new com.amazonaws.services.ec2.model.Filter(filter.getName(), filter.getValues());
    }

    private List<String> getResults(DescribeInstancesResult dinResult) {
        List<String> results = new LinkedList<String>();
        for (Reservation reservation : dinResult.getReservations()) {
            for (Instance instance : reservation.getInstances()) {
                InstanceState state = instance.getState();
                // get the low byte as per http://docs.aws.amazon.com/AWSJavaSDK/latest/javadoc/com/amazonaws/services/ec2/model/InstanceState.html
                byte code = state.getCode().byteValue();

                //http://docs.aws.amazon.com/AWSEC2/latest/CommandLineReference/ApiReference-cmd-DescribeInstances.html
                // Valid codes: 0 (pending) | 16 (running) | 32 (shutting-down) | 48 (terminated) | 64 (stopping) | 80 (stopped)
                if (code != 16) {
                    log(String.format("Ignoring instance '%s', because its state is '%s'", instance.getInstanceId(), state.getName()), Project.MSG_INFO);
                    continue;
                }
                results.add(getResult(instance));
            }
        }
        return results;
    }

    private String getEndpoint() {
        String endpoint;
        if (REGION_2_ENDPOINT.containsKey(region)) {
            endpoint = REGION_2_ENDPOINT.get(region);
        } else {
            log(String.format("Region %s given but not found in the region to endpoint map. Will use it as an endpoint", region),
                    Project.MSG_WARN);
            endpoint = region;
        }
        return endpoint;
    }

    private String getResult(Instance instance) {
        String select = this.select;
        if ("ip-address".equalsIgnoreCase(select)) {
            return instance.getPublicIpAddress();
        } else if ("dns-name".equalsIgnoreCase(select)) {
            String result = instance.getPublicDnsName();
            if (result == null || result.length() == 0)
                result = instance.getPublicIpAddress();
            return result;
        } else {
            return instance.getInstanceId();
        }
    }

    private void validateConfiguration() {
        // we don't really need to validate anything...
    }

    /**
     * @return iterater which can be used by tasks that can iterate, namely for from ant-contrib
     */
    public Iterator iterator() {
        if (resultList == null)
            execute();
        return resultList.iterator();
    }

    public class Filter {

        private String name;
        private List<String> values;

        public Filter() {
            values = new LinkedList<String>();
        }

        public Filter(String name, List<String> values) {
            this.name = name;
            this.values = values;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public List<String> getValues() {
            return values;
        }

        /**
         * Sets a single value for the filter.
         * @param value value of the filter's attribute
         */
        public void setValue(String value) {
            this.values.clear();
            this.values.add(value);
        }

        public class ValueWrapper {
            public void addText(String text) {
                values.add(text);
            }
        }

        /**
         * Adds a value element
         */
        public ValueWrapper createValue() {
            return new ValueWrapper();
        }
    }

    /**
     * filter element to add a filter to the describe-instances call.
     */
    public Filter createFilter() {
        Filter filter = new Filter();
        filters.add(filter);
        return filter;
    }

    public void setProperty(String property) {
        this.property = property;
    }

    /**
     * @param select "dns-name", "ip-address" or "instance-id", defaults to "instance-id"
     */
    public void setSelect(String select) {
        this.select = select;
    }
}
