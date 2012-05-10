package it.corley.ant;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.*;
import junit.framework.Assert;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.types.FileSet;
import org.apache.tools.ant.types.selectors.FileSelector;
import org.apache.tools.ant.types.selectors.FilenameSelector;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.*;
import java.util.Properties;

/**
 * Unit test class for the S3PutTask class
 * Created at 11:26 10.05.12
 *
 * @author Tadeusz Kozak
 * @see it.corley.ant.S3PutTask
 */
public class S3PutTaskTest {

    private String bucket;
    S3PutTask task;
    AmazonS3 s3;

    @BeforeMethod
    public void setUp() throws IOException {
        Properties props = new Properties();
        props.load(new FileInputStream("test.properties"));
        String accessKey = props.getProperty("test.aws.access_key");
        String secretKey = props.getProperty("test.aws.secret_key");
        String region = props.getProperty("test.aws.region");
        bucket = props.getProperty("test.aws.bucket");
        task = new S3PutTask();
        task.setKey(accessKey);
        task.setSecret(secretKey);
        task.setRegion(region);
        task.setBucket(bucket);
        Project project = new Project();
        project.setBaseDir(new File("."));
        task.setProject(project);
        AWSCredentials credential = new BasicAWSCredentials(accessKey, secretKey);
        s3 = new AmazonS3Client(credential);
        s3.setEndpoint(region);
    }

    @Test
    public void testSimplePut() throws IOException {
        File tempFile = File.createTempFile("aws-test", ".txt");
        FileWriter str = new FileWriter(tempFile);
        str.write("Super test content of super test file");
        str.close();
        FileSet set = new FileSet();
        set.setDir(tempFile.getParentFile());
        FilenameSelector selector = new FilenameSelector();
        selector.setName(tempFile.getName());
        set.addFilename(selector);
        task.setDest("aws-ant-tasks-test");
        task.addFileset(set);
        task.execute();
        S3Object obj = s3.getObject(bucket, "aws-ant-tasks-test/" + tempFile.getName());
        s3.deleteObject(bucket, "aws-ant-tasks-test/" + tempFile.getName());
    }

    @Test
    public void testPutPublic() throws IOException {
        File tempFile = File.createTempFile("aws-test", ".txt");
        FileWriter str = new FileWriter(tempFile);
        str.write("Super test content of super test file");
        str.close();
        FileSet set = new FileSet();
        set.setDir(tempFile.getParentFile());
        FilenameSelector selector = new FilenameSelector();
        selector.setName(tempFile.getName());
        set.addFilename(selector);
        task.setDest("aws-ant-tasks-test");
        task.addFileset(set);
        task.setPublicRead(true);
        task.execute();
        String objectKey = "aws-ant-tasks-test/" + tempFile.getName();
        S3Object obj = s3.getObject(bucket, objectKey);
        AccessControlList acl = s3.getObjectAcl(bucket, objectKey);
        Assert.assertEquals(2, acl.getGrants().size());
        for(Grant grant : acl.getGrants()) {
            if(grant.getGrantee() instanceof GroupGrantee) {
                Assert.assertTrue(grant.getGrantee().getIdentifier().contains("AllUsers"));
                Assert.assertEquals(grant.getPermission(), Permission.Read);
            }
        }
        s3.deleteObject(bucket, objectKey);
    }

    @Test
    public void testPutGlobalContentType() throws IOException {
        File tempFile = File.createTempFile("aws-test", ".txt");
        FileWriter str = new FileWriter(tempFile);
        str.write("Super test content of super test file");
        str.close();
        FileSet set = new FileSet();
        set.setDir(tempFile.getParentFile());
        FilenameSelector selector = new FilenameSelector();
        selector.setName(tempFile.getName());
        set.addFilename(selector);
        task.setDest("aws-ant-tasks-test");
        task.addFileset(set);
        String contentType = "application/x-custom-test-mime-type";
        task.setContentType(contentType);
        task.execute();
        String objectKey = "aws-ant-tasks-test/" + tempFile.getName();
        S3Object obj = s3.getObject(bucket, objectKey);
        Assert.assertEquals(contentType, obj.getObjectMetadata().getContentType());
        s3.deleteObject(bucket, objectKey);
    }


    @Test
    public void testPutContentTypeMapping() throws IOException {
        File tempFile = File.createTempFile("aws-test", ".txt");
        FileWriter str = new FileWriter(tempFile);
        str.write("Super test content of super test file");
        str.close();
        FileSet set = new FileSet();
        set.setDir(tempFile.getParentFile());
        FilenameSelector selector = new FilenameSelector();
        selector.setName(tempFile.getName());
        set.addFilename(selector);
        task.setDest("aws-ant-tasks-test");
        task.addFileset(set);
        String contentType = "application/x-custom-test-mime-type";
        task.addContentTypeMapping(new ContentTypeMapping(".txt", contentType));
        task.execute();
        String objectKey = "aws-ant-tasks-test/" + tempFile.getName();
        S3Object obj = s3.getObject(bucket, objectKey);
        Assert.assertEquals(contentType, obj.getObjectMetadata().getContentType());
        s3.deleteObject(bucket, objectKey);
    }


}
