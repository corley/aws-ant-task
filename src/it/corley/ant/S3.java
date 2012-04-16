package it.corley.ant;

import java.io.File;
import java.util.Vector;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.PutObjectResult;

public class S3 extends Task {

	private String key;
	private String secret;
	private String bucket;
	private String dest;
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
	
	public void setDest(String dest)
	{
		this.dest = dest;
	}
	
    public void setFail(boolean b) {
        fail = b;
    }
	
	public void execute()
	{
		if (fail) throw new BuildException("Fail requested.");
		
		AWSCredentials credential = new BasicAWSCredentials(key, secret);
		AmazonS3 s3 = new AmazonS3Client();
		
		File file = new File(bundleUpload);
		PutObjectRequest por = new PutObjectRequest(bucket, dest, file);

		PutObjectResult result = s3.putObject(por);

		log("File uploaded: " + bundleUpload);
	}
}
