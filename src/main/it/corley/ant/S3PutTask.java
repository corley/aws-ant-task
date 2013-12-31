package it.corley.ant;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.StorageClass;
import com.amazonaws.services.s3.transfer.TransferManager;
import com.amazonaws.services.s3.transfer.Upload;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.DirectoryScanner;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.types.FileSet;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

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
     * Destination dir on the S3 to which files should be uploaded.
     */
    private String dest;

    /**
     * Content-Type to be set globally for each uploaded file.
     */
    private String contentType;
    
    /**
     * Cache-Control to be set globally for each uploaded file.
     */
    private String cacheControl;
    
    private String contentEncoding;
    
    private String endPoint = DEFAULT_END_POINT;

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
    private List<CacheControlMapping> cacheControlMappings = new LinkedList<CacheControlMapping>();
    private List<ContentEncodingMapping> contentEncodingMappings = new LinkedList<ContentEncodingMapping>();

    /**
     * Whether to use reduced redundancy storage.
     */
    private boolean reducedRedundancy;
    
    /**
     * Executes the task.
     *
     * @see org.apache.tools.ant.Task#execute()
     */
    public void execute() {
        validateConfiguration();
        AWSCredentials credential = getCredentials();
        final TransferManager transferManager = new TransferManager(credential);
        log(String.format("Region %s provided", getEndPoint()), Project.MSG_INFO);
        transferManager.getAmazonS3Client().setEndpoint(getEndPoint());

        String path;
        if (dest == null) {
            path = "";
        } else {
            path = dest.trim();
            if ((! path.isEmpty()) && (! path.endsWith("/"))) {
                path = path + "/";
            }
            if (path.startsWith("/")) {
                path = path.substring(1);
            }
        }

        for (FileSet fs : filesets) {
            try {
                DirectoryScanner ds = fs.getDirectoryScanner(getProject());
                String[] files = ds.getIncludedFiles();
                File d = fs.getDir(getProject());

                if (files.length > 0) {
                    log("Uploading " + files.length + " file(s) from " + d.getAbsolutePath());
                    for (String filePath : files) {
                        String cleanFilePath = filePath.replace('\\', '/');
                        File file = new File(d, cleanFilePath);
                        PutObjectRequest por = new PutObjectRequest(bucket, path + cleanFilePath, file);

                        applyMetadata(file, por);

                        Upload upload = transferManager.upload(por);
                        upload.waitForUploadResult();

                        log("File: " + cleanFilePath + " copied to bucket: " + bucket + " destination: " + path);
                    }
                }
            } catch (BuildException be) {
                // directory doesn't exist or is not readable
                log("Could not upload file(s) to Amazon S3PutTask");
                log(be.getMessage());
                throw be;
            } catch (InterruptedException e) {
                log("Upload interrupted");
                log(e.getMessage());
                throw new BuildException(e);
            }
        }
    }

    private void applyMetadata(File file, PutObjectRequest por) {
        ObjectMetadata metadata = new ObjectMetadata();
        if (isPublicRead()) {
            por.setCannedAcl(CannedAccessControlList.PublicRead);
        }
        if (isReducedRedundancy()) {
            por.setStorageClass(StorageClass.ReducedRedundancy);
        }
        boolean metadataSet = false;
        String fileName = file.getName();
        for (ContentTypeMapping mapping : contentTypeMappings) {
            if (fileName.endsWith(mapping.getExtension())) {
                metadata.setContentType(mapping.getContentType());
                metadataSet = true;
                break;
            }
        }
        if (contentType != null && !metadataSet) {
            metadata.setContentType(contentType);
        }
        boolean cacheControlMetadataSet = false;
        for (CacheControlMapping mapping : cacheControlMappings) {
            if (fileName.endsWith(mapping.getExtension())) {
                metadata.setCacheControl(mapping.getMaxAge());
                cacheControlMetadataSet = true;
                break;
            }
        }
        //TODO: add single file metadata cache-control
        if (cacheControl != null && !cacheControlMetadataSet) {
        	metadata.setCacheControl(cacheControl);
        }
        
        boolean contentEncodingMetadataSet = false;
        for (ContentEncodingMapping mapping : contentEncodingMappings) {
			if (fileName.endsWith(mapping.getExtension())) {
				metadata.setContentEncoding(mapping.getEncoding());
				contentEncodingMetadataSet = true;
				break;
			}
		}
        if (contentEncoding != null && !contentEncodingMetadataSet) {
        	metadata.setContentEncoding(contentEncoding);
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
    
    public void setContentEncoding(String contentEncoding) {
    	this.contentEncoding = contentEncoding;
    }

    /**
     * Set the cache control max-age=seconds
     * 
     * @param cacheControl
     */
    public void setCacheControl(String cacheControl) {
    	this.cacheControl = cacheControl;
    }

    public void addContentTypeMapping(ContentTypeMapping mapping) {
        contentTypeMappings.add(mapping);
    }
    
    public void addCacheControlMapping(CacheControlMapping mapping) {
    	cacheControlMappings.add(mapping);
    }
    public void addContentEncodingMapping(ContentEncodingMapping mapping) {
    	contentEncodingMappings.add(mapping);
    }

    public void addFileset(FileSet set) {
        filesets.add(set);
    }

    public boolean isReducedRedundancy() {
        return reducedRedundancy;
    }

    public void setReducedRedundancy(boolean reducedRedundancy) {
        this.reducedRedundancy = reducedRedundancy;
    }

	public String getEndPoint() {
		return endPoint;
	}

	public void setEndPoint(String endPoint) {
		this.endPoint = endPoint;
	}

}
