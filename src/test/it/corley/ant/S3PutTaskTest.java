package it.corley.ant;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.*;
import junit.framework.Assert;
import junit.framework.TestCase;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.types.FileSet;
import org.apache.tools.ant.types.selectors.FileSelector;
import org.apache.tools.ant.types.selectors.FilenameSelector;

import java.io.*;
import java.util.Properties;

/**
 * Unit test class for the S3PutTask class
 * Created at 11:26 10.05.12
 *
 * @author Tadeusz Kozak
 * @see it.corley.ant.S3PutTask
 */
public class S3PutTaskTest extends TestCase
{
	public void testEasy()
	{
		TestCase.assertTrue(true);
	}
}
