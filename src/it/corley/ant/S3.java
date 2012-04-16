package it.corley.ant;

import java.io.File;
import java.util.Vector;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.model.PutObjectRequest;

public class S3 extends Task {

	private String key;
	private String secret;
	private String bucket;
	private String bundleUpload;
	boolean fail = false;
	
	public void setKey(String key)
	{
		this.key = key;
	}
	
	public void setSecret(String secret)
	{
		this.secret = secret;
	}
	
	public void setBucket(String bucket)
	{
		this.bucket = bucket;
	}
	
	public void setBundleUpload(String bundleUpload)
	{
		this.bundleUpload = bundleUpload;
	}
	
    public void setFail(boolean b) {
        fail = b;
    }
	
	public void execute()
	{
		if (fail) throw new BuildException("Fail requested.");
		
		//AWSCredentials credential = new BasicAWSCredentials(key, secret);
		log(bundleUpload);
	}
}
