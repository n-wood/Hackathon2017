package controllers

import java.io.File

import play.api.mvc._
import services.{LocalSaveFile, Rekog, S3Upload}

class uploadPicture extends Controller with S3Upload with Rekog with LocalSaveFile {

  def getPicture() = Action {
    //TODO: hardcoded to go to localhost:9000
    Ok(views.html.uploadPicture("http://localhost:9000/postPicture"))
  }

  def uploadPicture() = Action(parse.multipartFormData) { request =>
    val tempFile = "tmp/tempFileFileFile.txt"
    request.body.file("picture").map { picture =>
      val filename = picture.filename
      val contentType = picture.contentType
      picture.ref.moveTo(new File(tempFile), true)
    }.getOrElse {
      InternalServerError("its broken")
    }

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
}
