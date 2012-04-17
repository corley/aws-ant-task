package it.corley.ant;

import java.io.File;
import java.util.Vector;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.DirectoryScanner;
import org.apache.tools.ant.types.FileSet;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.PutObjectRequest;

public class S3 extends AWSTask {

	private String bucket;
	private String dest;
	boolean fail = false;

	protected Vector<FileSet> filesets = new Vector<FileSet>();

	public void setBucket(String bucket) {
		this.bucket = bucket;
	}

	public void setDest(String dest) {
		this.dest = dest;
	}

	public void setFail(boolean b) {
		fail = b;
	}

	public void addFileset(FileSet set) {
		filesets.addElement(set);
	}

	public void execute() {
		if (fail) {
			throw new BuildException("Fail requested.");
		}
		
		AWSCredentials credential = new BasicAWSCredentials(getKey(), getSecret());
		AmazonS3 s3 = new AmazonS3Client(credential);
		
		for (FileSet fs : filesets) {
			try {
				DirectoryScanner ds = fs.getDirectoryScanner(getProject());
				String[] files = ds.getIncludedFiles();
				File d = fs.getDir(getProject());

				if (files.length > 0) {
					log("copying " + files.length + " files from " + d.getAbsolutePath());
					for (int j = 0; j < files.length; j++) {
						String cleanFilePath = files[j].replace('\\', '/');
						File file = new File(d, cleanFilePath);
						PutObjectRequest por = new PutObjectRequest(bucket, dest + "/" + cleanFilePath, file);
						s3.putObject(por);
						log("File: " + cleanFilePath + " copied to bucket: " + bucket + " destination: " + dest);
					}
				}
			} catch (BuildException be) {
				// directory doesn't exist or is not readable
				log("Could not copy file(s) to Amazon S3");
				log(be.getMessage());
			}
		}
	}
}
