package controllers

import java.io.File

import scala.util.Random
import play.api.mvc.{Action, Controller}
import services.{LocalSaveFile, Rekog, S3Upload}
import utils.Constants._

class searchByPicture extends Controller with Rekog with S3Upload with LocalSaveFile {
  val greetings = Array("Hello XXX", "Greetings XXX", "Howdy XXX", "Good Afternoon XXX", "Hey XXX", "Looking Good XXX", "Hello Again XXX")

  def getPicture() = Action {
    //TODO: hardcoded to go to localhost:9000
    Ok(views.html.getPicture("/searchByPicture"))
  }

  def getNewPicture() = Action {
    //TODO: hardcoded to go to localhost:9000
    Ok(views.html.searchNewPicture("/searchByPicture"))
  }

  def searchByPicture = Action(parse.temporaryFile) { request =>
    val tempFile = "tmp/tempFileFile.txt"
    request.body.moveTo(new File(tempFile), true)

    val (fileName, name) = saveFile(tempFile)
    val faces = searchByFace(fileName)
    val filteredFaces = filterFaces(faces)
    val facesWithPictures = filteredFaces.map { face =>
      val name = face.getFace.getExternalImageId.replace("_", " ")
      (s"Name: '$name' with confidence: ${face.getFace.getConfidence.toString}", getImg(name))
    }
    val spokenName = Random.shuffle(greetings.toList).head.replace("XXX", filteredFaces(0).getFace.getExternalImageId.replace("_", " "))


    Ok(views.html.searchByPicture(facesWithPictures, spokenName))
  }
}