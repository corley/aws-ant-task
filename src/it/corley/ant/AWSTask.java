package it.corley.ant;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;

public class AWSTask extends Task {
	private String key;
	private String secret;
	
	public void setKey(String key) {
		this.key = key;
	}
	
	public String getKey()
	{
		return this.key;
	}
	
	public String getSecret()
	{
		return this.secret;
	}

	public void setSecret(String secret) {
		this.secret = secret;
	}
}
