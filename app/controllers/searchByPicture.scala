package controllers

import java.io.File
import scala.util.Random

import play.api.mvc.{Action, Controller}
import services.{LocalSaveFile, Rekog}

class searchByPicture extends Controller with Rekog with LocalSaveFile {
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
      .map(face => s"Name: '${face.getFace.getExternalImageId.replace("_", " ")}' with confidence: ${face.getFace.getConfidence.toString}")
    val rekogname = Random.shuffle(greetings.toList).head.replace("XXX", filteredFaces(0).split("'")(1))
    Ok(views.html.searchByPicture(filteredFaces, rekogname))
  }
}