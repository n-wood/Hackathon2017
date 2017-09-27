package controllers

import java.io.{BufferedReader, File, InputStream, InputStreamReader}
import java.nio.charset.{Charset, StandardCharsets}
import org.apache.commons.codec.binary.Base64

import org.apache.commons.io.IOUtils
import org.apache.commons.io.input.ReaderInputStream
import play.api.mvc._
import services.{LocalSaveFile, Rekog, S3Upload}


class getPicture extends Controller with S3Upload with Rekog with LocalSaveFile {

  def getPicture() = Action {
    //TODO: hardcoded to go to localhost:9000
    Ok(views.html.getPicture("http://localhost:9000/postPicture"))
  }

  def postPicture() = Action(parse.temporaryFile) { request =>
    val tempFile = "tmp/tempFileFile.txt"
    request.body.moveTo(new File(tempFile), true)

    val (fileName, name) = saveFile(tempFile)

    val faces = searchByFace(fileName)
    if (!faces.isEmpty) {
      val filteredFaces = filterFaces(faces)
        .map(face => s" File already uploaded Name: '${face.getFace.getExternalImageId.replace("_", " ")}' with confidence: ${face.getFace.getConfidence.toString}")
      val rekogname = filteredFaces(0).split("'")(1)
      Ok(views.html.searchByPicture(filteredFaces, rekogname))
    } else {
      uploadFile(fileName)
      indexFace(fileName, name)
      Ok(s"File uploaded to S3 and indexed in Rekognition \n fileName: $fileName")
    }
  }

  def test() = Action {
    val img = getFile("charlie_anglin")
    val bytes = IOUtils.toByteArray(img.getObjectContent)
    val bytes64 = Base64.encodeBase64(bytes)
    val content = new String(bytes64, "UTF-8")
    Ok(views.html.test("data:image/png;base64," + content))
  }
}
