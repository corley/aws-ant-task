package it.corley.ant;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.cloudfront.AmazonCloudFront;
import com.amazonaws.services.cloudfront.AmazonCloudFrontClient;
import com.amazonaws.services.cloudfront.model.CreateInvalidationRequest;
import com.amazonaws.services.cloudfront.model.CreateInvalidationResult;
import com.amazonaws.services.cloudfront.model.InvalidationBatch;
import com.amazonaws.services.cloudfront.model.Paths;
import org.apache.tools.ant.BuildException;

import java.util.Collection;
import java.util.Vector;

public class CloudFront extends AWSTask {
    private String distibutionId;
    private String pathsString = "\0";
    boolean fail = false;

    Vector<Delete> delete = new Vector<Delete>();

    public void setDistributionId(String distributionId) {
        this.distibutionId = distributionId;
    }

    public void setPathsString(String pathsString) {
        this.pathsString = pathsString;
    }

    public void setFail(boolean b) {
        fail = b;
    }

    public void execute() {
        if (fail) throw new BuildException("Fail requested.");

        log("Executing invalidation for key : " + this.getKey() + " on distribution id: " + this.distibutionId);

        AmazonCloudFront front = new AmazonCloudFrontClient(getCredentials());

        CreateInvalidationRequest invalidationRequest = new CreateInvalidationRequest();
        invalidationRequest.setDistributionId(distibutionId);

        InvalidationBatch invalidationBatch = new InvalidationBatch();

        Collection<String> paths = new Vector<String>();
        int pathsSize;
        int i;
        String path;

        if (pathsString != "\0") {
            String[] parts = pathsString.split(",");
            pathsSize = parts.length;
            for (i = 0; i < pathsSize; i++) {
                path = parts[i];
                log("Invalidation for path: " + path);
                paths.add(path);
            }
        } else {
            pathsSize = this.delete.size();
            for (i = 0; i < pathsSize; i++) {
                path = this.delete.get(i).getPath();
                log("Invalidation for path: " + this.delete.get(i).getPath());
                paths.add(path);
            }
        }

        log("complete creating paths list total item to invalidate: " + pathsSize);

        Paths pathsList = new Paths().withItems(paths);
        pathsList.setQuantity(pathsSize);

        invalidationBatch.setPaths(pathsList);
        invalidationBatch.setCallerReference(distibutionId + String.valueOf((int) System.currentTimeMillis() / 1000));
        invalidationRequest.setInvalidationBatch(invalidationBatch);

        CreateInvalidationResult result = front.createInvalidation(invalidationRequest);

        log("Invalidation result: " + result.getInvalidation().getStatus());
    }

    public Delete createDelete() {
        Delete file = new Delete();
        this.delete.add(file);
        return file;
    }

    public class Delete {
        String path;

        public Delete() {

        }

        public void setPath(String path) {
            this.path = path;
        }

        public String getPath() {
            return path;
        }
    }
}
