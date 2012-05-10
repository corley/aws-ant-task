package it.corley.ant;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.DirectoryScanner;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.types.FileSet;

import java.io.File;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class S3PutTask extends AWSTask {

    private static final Map<String, String> REGION_2_ENDPOINT = new HashMap<String, String>();

    static {
        REGION_2_ENDPOINT.put("EU", "s3-eu-west-1.amazonaws.com");
        REGION_2_ENDPOINT.put("us-west-1", "s3-us-west-1.amazonaws.com");
        REGION_2_ENDPOINT.put("us-west-2", "s3-us-west-2.amazonaws.com");
        REGION_2_ENDPOINT.put("ap-southeast-1", "s3-ap-southeast-1.amazonaws.com");
        REGION_2_ENDPOINT.put("ap-northeast-1", "s3-ap-northeast-1.amazonaws.com");
        REGION_2_ENDPOINT.put("sa-east-1", "sa-east-1.amazonaws.com");
    }

    /**
     * Boolean flag defining whether the put operation should set the ACL to publicly readable for each uploaded item.
     */
    private boolean publicRead = false;

    /**
     * Target bucket, to which files should be uploaded.
     */
    private String bucket;

    /**
     * Destination dir on the S3 to which files should be uploaded.
     */
    private String dest;

    /**
     * Content-Type to be set globally for each uploaded file.
     */
    private String contentType;

    /**
     * Filesets containing content to be uploaded
     */
    protected List<FileSet> filesets = new LinkedList<FileSet>();

    /**
     * List of Content-Type mappings - allowing fine tune configuration useful, when uploading multiple files
     * with different content types.
     *
     * @see ContentTypeMapping
     */
    private List<ContentTypeMapping> contentTypeMappings = new LinkedList<ContentTypeMapping>();

    /**
     * Executes the task.
     *
     * @see org.apache.tools.ant.Task#execute()
     */
    public void execute() {
        validateConfiguration();
        AWSCredentials credential = new BasicAWSCredentials(getKey(), getSecret());
        AmazonS3 s3 = new AmazonS3Client(credential);

        if (region != null) {
            if (REGION_2_ENDPOINT.containsKey(region)) {
                s3.setEndpoint(REGION_2_ENDPOINT.get(region));
            } else {
                log("Region " + region + " given but not found in the region to endpoint map. Will use it as an endpoint",
                        Project.MSG_WARN);
                s3.setEndpoint(region);
            }
        }

        for (FileSet fs : filesets) {
            try {
                DirectoryScanner ds = fs.getDirectoryScanner(getProject());
                String[] files = ds.getIncludedFiles();
                File d = fs.getDir(getProject());

                if (files.length > 0) {
                    log("Uploading " + files.length + " files from " + d.getAbsolutePath());
                    for (String filePath : files) {
                        String cleanFilePath = filePath.replace('\\', '/');
                        File file = new File(d, cleanFilePath);
                        PutObjectRequest por = new PutObjectRequest(bucket, dest + "/" + cleanFilePath, file);

                        applyMetadata(file, por);
                        s3.putObject(por);
                        log("File: " + cleanFilePath + " copied to bucket: " + bucket + " destination: " + dest);
                    }
                }
            } catch (BuildException be) {
                // directory doesn't exist or is not readable
                log("Could not upload file(s) to Amazon S3PutTask");
                log(be.getMessage());
            }
        }
    }

    private void applyMetadata(File file, PutObjectRequest por) {
        ObjectMetadata metadata = new ObjectMetadata();
        if (isPublicRead()) {
            por.setCannedAcl(CannedAccessControlList.PublicRead);
        }
        if (contentType != null) {
            metadata.setContentType(contentType);
        } else {
            String fileName = file.getName();
            for (ContentTypeMapping mapping : contentTypeMappings) {
                if (fileName.endsWith(mapping.getExtension())) {
                    metadata.setContentType(mapping.getContentType());
                    break;
                }
            }
        }
        por.setMetadata(metadata);
    }

    private void validateConfiguration() {
        if (bucket == null) {
            throw new BuildException("Target bucket not given. Cannot upload");
        }
//        TODO add other properties
    }

    /**
     * ===============================================
     * Getters and setters
     * ===============================================
     */

    public boolean isPublicRead() {
        return publicRead;
    }

    public void setPublicRead(boolean publicRead) {
        this.publicRead = publicRead;
    }

    public void setBucket(String bucket) {
        this.bucket = bucket;
    }

    public void setDest(String dest) {
        this.dest = dest;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public void addContentTypeMapping(ContentTypeMapping mapping) {
        contentTypeMappings.add(mapping);
    }

    public void addFileset(FileSet set) {
        filesets.add(set);
    }

}
