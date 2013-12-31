package it.corley.ant;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.ProgressEvent;
import com.amazonaws.services.s3.model.ProgressListener;
import com.amazonaws.services.s3.transfer.Download;
import com.amazonaws.services.s3.transfer.TransferManager;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;

import java.io.File;

public class S3GetTask extends AWSTask {

  private String endPoint = DEFAULT_END_POINT;

  /**
   * The name of the bucket containing the file.
   */
  private String bucket;

  /**
   * The path of the file within the specified bucket.
   */
  private String src;

  /**
   * The path where to store the retrieved file.
   */
  private String dest;

  @Override
  public void execute() throws BuildException {
    validateConfiguration();

    File outputFile = getOutputFile();
    GetObjectRequest request = new GetObjectRequest(bucket, src);
    downloadFile(request, outputFile);
  }

  private void validateConfiguration() {
    if (endPoint == null || endPoint.length() == 0) {
      throw new BuildException("Missing value for parameter 'endPoint'");
    }
    if (bucket == null || bucket.length() == 0) {
      throw new BuildException("Missing value for parameter 'bucket'");
    }
    if (src == null || src.length() == 0) {
      throw new BuildException("Missing value for parameter 'src'");
    }
  }

  private File getOutputFile() {
    if (dest == null || dest.length() == 0)
      return new File(src);
    return new File(dest);
  }

  private void downloadFile(GetObjectRequest request, File outputFile) {
    log(String.format("Downloading '%s/%s' to '%s'", request.getBucketName(), request.getKey(), outputFile.getName()), Project.MSG_INFO);
    try {
      TransferManager transferManager = getTransferManager();
      Download download = transferManager.download(request, outputFile);
      download.addProgressListener(getProgressListener());
      download.waitForCompletion();
    } catch (InterruptedException e) {
      log("Download interrupted");
      log(e.getMessage());
      throw new BuildException(e);
    }
  }

  private TransferManager getTransferManager() {
    AWSCredentials credentials = getCredentials();
    TransferManager transferManager = new TransferManager(credentials);
    transferManager.getAmazonS3Client().setEndpoint(getEndPoint());
    log(String.format("Region %s provided", getEndPoint()), Project.MSG_INFO);
    return transferManager;
  }

  private ProgressListener getProgressListener() {
    return new ProgressListener() {
      @Override
      public void progressChanged(ProgressEvent progressEvent) {
        if (progressEvent.getEventCode() == ProgressEvent.COMPLETED_EVENT_CODE) {
          log("Download complete", Project.MSG_INFO);
        }
      }
    };
  }

  /**
   * ===============================================
   * Getters and setters
   * ===============================================
   */

  public String getEndPoint() {
    return endPoint;
  }

  public void setEndPoint(String endPoint) {
    this.endPoint = endPoint;
  }

  public String getBucket() {
    return bucket;
  }

  public void setBucket(String bucket) {
    this.bucket = bucket;
  }

  public String getSrc() {
    return src;
  }

  public void setSrc(String src) {
    this.src = src;
  }

  public String getDest() {
    return dest;
  }

  public void setDest(String dest) {
    this.dest = dest;
  }
}
