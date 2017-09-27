package services

import java.io.{BufferedOutputStream, FileOutputStream}
import java.util.Base64

import org.joda.time.DateTime

import scala.io.Source

trait LocalSaveFile {

  def saveFile(tempFile: String): (String, String) = {
    val uniqueDate = new DateTime().getMillis

    val file = Source.fromFile(tempFile)
    val lines = file.getLines.toStream
    val name = lines(3).toLowerCase.replace(" ", "_")
    val fileName = name + uniqueDate.toString + ".png"
    val img = lines(7)
      .replace("data:image/png;base64,", "")
      .replace(" ", "+")

    val decodedImg = Base64.getDecoder().decode(img)

    val bos = new BufferedOutputStream(new FileOutputStream(s"tmp/$fileName"))
    bos.write(decodedImg)
    file.close()
    bos.close()
    (fileName, name)
  }
}
