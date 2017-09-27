package controllers

import java.io.File

import play.api.mvc._
import services.{LocalSaveFile, Rekog, S3Upload}
import utils.Constants._

import scala.util.Random


class getPicture extends Controller with S3Upload with Rekog with LocalSaveFile {

  def getPicture() = Action {
    //TODO: hardcoded to go to localhost:9000
    Ok(views.html.getPicture("/postPicture"))
  }

  def getNewPicture() = Action {
    Ok(views.html.newGetPicture("http://localhost:9000/postPicture"))
  }

  def postPicture() = Action(parse.temporaryFile) { request =>
    val tempFile = "tmp/tempFileFile.txt"
    request.body.moveTo(new File(tempFile), true)

    val (fileName, name) = saveFile(tempFile)

    val faces = searchByFace(fileName)
    if (!faces.isEmpty) {
      val filteredFaces = filterFaces(faces)
      val facesWithPictures = filteredFaces.map { face =>
        val name = face.getFace.getExternalImageId.replace("_", " ")
        (s"Name: '$name' with confidence: ${face.getFace.getConfidence.toString}", getImg(name))
      }
      val spokenName = Random.shuffle(greetings.toList).head.replace("XXX", filteredFaces(0).getFace.getExternalImageId.replace("_", " "))

      Ok(views.html.searchByPicture(facesWithPictures, spokenName))
    } else {
      uploadFile(fileName)
      indexFace(fileName, name)
      Ok(s"File uploaded to S3 and indexed in Rekognition \n fileName: $fileName")
    }
  }
}
