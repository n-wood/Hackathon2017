package services

import java.io.FileInputStream
import java.nio.ByteBuffer
import java.util

import com.amazonaws.auth.AWSStaticCredentialsProvider
import com.amazonaws.regions.Regions
import com.amazonaws.services.rekognition.AmazonRekognitionClientBuilder
import com.amazonaws.services.rekognition.model._
import utils.Constants._

import scala.collection.JavaConversions

trait Rekog extends AWSCredentials {

  val rekognitionClient = AmazonRekognitionClientBuilder
    .standard()
    .withRegion(Regions.EU_WEST_1)
    .withCredentials(new AWSStaticCredentialsProvider(yourAWSCredentials))
    .build()

  def indexFace(fileName: String, name: String) = {
    val request = new IndexFacesRequest()
      .withCollectionId(COLLECTION)
      .withImage(new Image()
        .withS3Object(new S3Object()
          .withName(fileName)
          .withBucket(BUCKET)))
      .withExternalImageId(name)

    rekognitionClient.indexFaces(request).getFaceRecords
  }

  def searchByFace(fileName: String): List[FaceMatch] = {
    val file = new FileInputStream(s"tmp/$fileName")
    val fChan = file.getChannel()
    val fSize = fChan.size()
    val mBuf = ByteBuffer.allocate(fSize.toInt)
    fChan.read(mBuf)
    mBuf.rewind()
    fChan.close()
    file.close()

    val request = new SearchFacesByImageRequest()
      .withCollectionId(COLLECTION)
      .withImage(new Image()
        .withBytes(mBuf))

    val result = rekognitionClient.searchFacesByImage(request).getFaceMatches
    //Convert java list to scala list
    JavaConversions.asScalaBuffer(result).toList
  }

  /**
    * Filters out duplicate names and only keeps those with highest confidence. Return List is ordered by highest confidence first.
    */
  //N.B. untested... but seems to work so far
  def filterFaces(faces: List[FaceMatch]): List[FaceMatch] = {
    val groupedByName = faces.groupBy(face => face.getFace.getExternalImageId)
    val highestConfidence = groupedByName.map{tup =>
      (tup._1, tup._2.reduce((a,b) => if(a.getFace.getConfidence >= b.getFace.getConfidence) a else b))
    }
    highestConfidence.map(_._2).toList.sortWith(_.getFace.getConfidence > _.getFace.getConfidence)
  }

}
