package services

import java.io.File

import com.amazonaws.auth.AWSStaticCredentialsProvider
import com.amazonaws.regions.Regions
import com.amazonaws.services.s3.AmazonS3ClientBuilder
import utils.Constants._

trait S3Upload extends AWSCredentials {

  //N.B. region MUST be EU_WEST_1 as bucket must be in same region as Rekog
  val s3Client = AmazonS3ClientBuilder
    .standard()
    .withRegion(Regions.EU_WEST_1)
    .withCredentials(new AWSStaticCredentialsProvider(yourAWSCredentials))
    .build()

  def uploadFile(fileName: String) = {
    val fileToUpload = new File(s"tmp/$fileName")

    s3Client.putObject(BUCKET, fileName, fileToUpload)
  }

  //name must be in the form firstname_secondname{numbers} < all lower case
  def getFile(name: String) = {
    val pictures = s3Client.listObjects(BUCKET, name)
    //gets first image which has that name
    val imageKey = pictures.getObjectSummaries.iterator.next().getKey
    s3Client.getObject(BUCKET, imageKey)
  }
}
