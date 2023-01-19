package com.var.pdfutil.util;

import com.google.auth.Credentials;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import com.google.common.collect.Lists;
import com.var.pdfutil.config.GCPConfig;
import lombok.extern.slf4j.Slf4j;
import com.google.cloud.storage.Blob;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
@Slf4j
@Component
public class GCPStorageOps {
  @Autowired
  private GCPConfig config;

  private static Credentials credentials = getCredentials();


  public void uploadObject(
      String objectName, String filePath) throws IOException {
    //  String projectId = "your-project-id";

    // The ID of your GCS bucket
    // String bucketName = "your-unique-bucket-name";

    // The ID of your GCS object
    // String objectName = "your-object-name";

    // The path to your file to upload
    // String filePath = "path/to/your/file"

    Storage storage = StorageOptions.newBuilder().setCredentials(credentials).setProjectId(config.getProjectId()).build().getService();
    BlobId blobId = BlobId.of(config.getBucketName(), objectName);
    BlobInfo blobInfo = BlobInfo.newBuilder(blobId).build();
    storage.create(blobInfo, Files.readAllBytes(Paths.get(filePath)));

    log.info(
        "File " + filePath + " uploaded to bucket " + config.getBucketName() + " as " + objectName);
  }

  public void uploadObjectFromMemory(
         String objectName, byte[] content) throws IOException {
    // The ID of your GCP project
    // String projectId = "your-project-id";

    // The ID of your GCS bucket
    // String bucketName = "your-unique-bucket-name";

    // The ID of your GCS object
    // String objectName = "your-object-name";

    // The string of contents you wish to upload
    // String contents = "Hello world!";

    Storage storage = StorageOptions.newBuilder().setCredentials(credentials).setProjectId(config.getProjectId()).build().getService();
    BlobId blobId = BlobId.of(config.getBucketName(), objectName);
    BlobInfo blobInfo = BlobInfo.newBuilder(blobId).build();
    storage.createFrom(blobInfo, new ByteArrayInputStream(content));

    log.info(
            "Object "
                    + objectName
                    + " uploaded to bucket "
                    + config.getBucketName()
                    + " with contents ");
  }

  public static Credentials getCredentials(){
    InputStream inputStream = GCPStorageOps.class.getClassLoader().getResourceAsStream("pdfutils-359816-8a13ea772e18.json");
    try {
      return GoogleCredentials.fromStream(inputStream)
              .createScoped(Lists.newArrayList("https://www.googleapis.com/auth/cloud-platform"));
    } catch (IOException e) {
      log.error(e.getMessage());
      throw new RuntimeException("Error while reading service key for GCP account");
    }
  }

  public void downloadObject(
         String objectName, String destFilePath) {
    // The ID of your GCP project
    // String projectId = "your-project-id";

    // The ID of your GCS bucket
    // String bucketName = "your-unique-bucket-name";

    // The ID of your GCS object
    // String objectName = "your-object-name";

    // The path to which the file should be downloaded
    // String destFilePath = "/local/path/to/file.txt";

    Storage storage = StorageOptions.newBuilder().setCredentials(credentials).setProjectId(config.getProjectId()).build().getService();

    Blob blob = storage.get(BlobId.of(config.getBucketName(), objectName));
    blob.downloadTo(Paths.get(destFilePath));

    log.info(
            "Downloaded object "
                    + objectName
                    + " from bucket name "
                    + config.getBucketName()
                    + " to "
                    + destFilePath);
  }

  public byte[] downloadObjectIntoMemory(
          String objectName) {
    // The ID of your GCP project
    // String projectId = "your-project-id";

    // The ID of your GCS bucket
    // String bucketName = "your-unique-bucket-name";

    // The ID of your GCS object
    // String objectName = "your-object-name";

    Storage storage = StorageOptions.newBuilder().setCredentials(credentials).setProjectId(config.getProjectId()).build().getService();
    byte[] content = storage.readAllBytes(config.getBucketName(), objectName);
    log.info(
            "The contents of "
                    + objectName
                    + " from bucket name "
                    + config.getBucketName());
    return content;
  }
}
