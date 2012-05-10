package it.corley.ant;

import java.io.File;
import java.util.*;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.DirectoryScanner;
import org.apache.tools.ant.types.FileSet;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.PutObjectRequest;

public class S3PutTask extends AWSTask {


    /**
     * Boolean flag defining whether the put operation should set the ACL to publicly readable for each uploaded item.
     */
    private boolean publicRead = false;

    /**
     * Target bucket, to which files should be uploaded.
     */
    private String bucket;

    /**
     *
     */
    private String dest;

    /**
     * Content-Type to be set globally for each uploaded file.
     */
    private String contentType;

    /**
     * Filesets containing content to be uploaded
     */
    protected Vector<FileSet> filesets = new Vector<FileSet>();

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
        AWSCredentials credential = new BasicAWSCredentials(getKey(), getSecret());
        AmazonS3 s3 = new AmazonS3Client(credential);

        for (FileSet fs : filesets) {
            try {
                DirectoryScanner ds = fs.getDirectoryScanner(getProject());
                String[] files = ds.getIncludedFiles();
                File d = fs.getDir(getProject());

                if (files.length > 0) {
                    log("copying " + files.length + " files from " + d.getAbsolutePath());
                    for (String file1 : files) {
                        String cleanFilePath = file1.replace('\\', '/');
                        File file = new File(d, cleanFilePath);
                        PutObjectRequest por = new PutObjectRequest(bucket, dest + "/" + cleanFilePath, file);

                        if (this.isPublicRead()) {
                            por.setCannedAcl(CannedAccessControlList.PublicRead);
                        }
                        applyContentType(file, por);


                        s3.putObject(por);
                        log("File: " + cleanFilePath + " copied to bucket: " + bucket + " destination: " + dest);
                    }
                }
            } catch (BuildException be) {
                // directory doesn't exist or is not readable
                log("Could not copy file(s) to Amazon S3PutTask");
                log(be.getMessage());
            }
        }
    }

    private void applyContentType(File file, PutObjectRequest por) {
        if (contentType != null) {
            por.getMetadata().setContentType(contentType);
        } else {
            String fileName = file.getName();
            for (ContentTypeMapping mapping : contentTypeMappings) {
                if (fileName.endsWith(mapping.getExtension())) {
                    por.getMetadata().setContentType(mapping.getContentType());
                }
            }
        }
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
        filesets.addElement(set);
    }

}
