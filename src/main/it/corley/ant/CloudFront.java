package it.corley.ant;

import java.util.Collection;
import java.util.Vector;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.cloudfront.AmazonCloudFront;
import com.amazonaws.services.cloudfront.AmazonCloudFrontClient;
import com.amazonaws.services.cloudfront.model.CreateInvalidationRequest;
import com.amazonaws.services.cloudfront.model.CreateInvalidationResult;
import com.amazonaws.services.cloudfront.model.InvalidationBatch;

public class CloudFront extends AWSTask 
{
	private String distibutionId;
	boolean fail = false;
	
	Vector<Delete> delete = new Vector<Delete>();
	
	public void setDistributionId(String distributionId)
	{
		this.distibutionId = distributionId;
	}
	
    public void setFail(boolean b) {
        fail = b;
    }
    
    public void execute() {
        if (fail) throw new BuildException("Fail requested.");

        log("Executing invalidation for key : " + this.getKey() + " on distribution id: " + this.distibutionId);
        
    	AWSCredentials credential = new BasicAWSCredentials(getKey(), getSecret());
    	AmazonCloudFront front = new AmazonCloudFrontClient(credential);
    	
    	CreateInvalidationRequest invalidationRequest = new CreateInvalidationRequest();
    	invalidationRequest.setDistributionId(distibutionId);
    	
    	InvalidationBatch invalidationBatch = new  InvalidationBatch();
        
    	Collection<String> paths = new Vector<String>();
    	for (int i=0; i<this.delete.size(); i++) {
    		String path = this.delete.get(i).getPath();
    		log("Invalidation for path: " + this.delete.get(i).getPath());
    		paths.add(path);
    	}
        	
    	invalidationBatch.setPaths(paths);
    	invalidationBatch.setCallerReference(distibutionId + String.valueOf((int)System.currentTimeMillis()/1000));
    	invalidationRequest.setInvalidationBatch(invalidationBatch);
    	
    	CreateInvalidationResult result = front.createInvalidation(invalidationRequest);
    	
    	log("Invalidation result: " + result.getInvalidation().getStatus());
    }
    
    public Delete createDelete()
    {
    	Delete file = new Delete();
    	this.delete.add(file);
    	return file;
    }
    
    public class Delete
    {
    	String path;
    	
    	public Delete() {
    		
		}
    	
    	public void setPath(String path)
    	{
    		this.path = path;
    	}
    	
    	public String getPath()
    	{
    		return path;
    	}
    }
}
