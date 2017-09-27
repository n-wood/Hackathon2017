package services

import com.amazonaws.auth.BasicAWSCredentials

import scala.io.Source

trait AWSCredentials {

  private lazy val (accessKey, secretKey) = getAccessKeys

  lazy val yourAWSCredentials = new BasicAWSCredentials(accessKey, secretKey)

  private def getAccessKeys: (String, String) = {
    try {
      val file = Source.fromFile("credentials.csv")
      val line = file.getLines().toStream(1)
      val creds = line.split(",").slice(2, 4)
      file.close()
      (creds.head, creds.last)
    } catch {
      case e => throw new Exception("credentials file not found - see readme")
    }
  }

}
