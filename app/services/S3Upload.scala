package services

import java.io.File

import com.amazonaws.auth.AWSStaticCredentialsProvider
import com.amazonaws.regions.Regions
import com.amazonaws.services.s3.AmazonS3ClientBuilder
import org.apache.commons.codec.binary.Base64
import org.apache.commons.io.IOUtils
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
  def getImg(name: String) = {
    val transformedName = name.replace(" ", "_")
    val pictures = s3Client.listObjects(BUCKET, transformedName)
    //gets first image which has that name
    val imageKey = pictures.getObjectSummaries.iterator.next().getKey
    val s3Object = s3Client.getObject(BUCKET, imageKey)
    val bytes = IOUtils.toByteArray(s3Object.getObjectContent)
    val bytes64 = Base64.encodeBase64(bytes)
    val content = new String(bytes64, "UTF-8")
    "data:image/png;base64," + content
  }
}
