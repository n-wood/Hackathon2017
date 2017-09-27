package controllers

import java.io.File

import play.api.mvc._
import services.{LocalSaveFile, Rekog, S3Upload}

class getPicture extends Controller with S3Upload with Rekog with LocalSaveFile {

  def getPicture() = Action {
    //TODO: hardcoded to go to localhost:9000
    Ok(views.html.getPicture("http://localhost:9000/postPicture"))
  }

  def postPicture() = Action(parse.temporaryFile) { request =>
    //val tempFile = "tmp/tempFileFile.txt"
    //request.body.moveTo(new File(tempFile), true)

    val image:(Array[Byte],String) = saveFile(request.body.toString())

   
    val faces = searchByFace(image._1)
    if (!faces.isEmpty) {
      val filteredFaces = filterFaces(faces)
        .map(face => s" File already uploaded Name: '${face.getFace.getExternalImageId.replace("_", " ")}' with confidence: ${face.getFace.getConfidence.toString}")
      val rekogname = filteredFaces(0).split("'")(1)
      Ok(views.html.searchByPicture(filteredFaces, rekogname))
    } else {
      uploadFile(image._2, image._1)
//      indexFace(image._2, image._1)
      Ok(s"File uploaded to S3 and indexed in Rekognition \n fileName: $image._1")
    }
  }
}
