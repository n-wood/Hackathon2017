package services

import java.io.File

import com.amazonaws.auth.AWSStaticCredentialsProvider
import com.amazonaws.regions.Regions
import com.amazonaws.services.s3.AmazonS3ClientBuilder
import com.amazonaws.services.s3.model.ObjectMetadata
import utils.Constants._

trait S3Upload extends AWSCredentials {

  //N.B. region MUST be EU_WEST_1 as bucket must be in same region as Rekog
  val s3Client = AmazonS3ClientBuilder
    .standard()
    .withRegion(Regions.EU_WEST_1)
    .withCredentials(new AWSStaticCredentialsProvider(yourAWSCredentials))
    .build()

  def uploadFile(fileName: String, fileBytes: Array[Byte]) = {
    val metadata: ObjectMetadata = new ObjectMetadata()
    metadata.setContentType("image/png")
    metadata.setContentLength(fileBytes.length-1)
    s3Client.putObject(BUCKET, fileName, fileBytes)
    s3Client.put
  }
}
