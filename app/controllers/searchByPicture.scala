package controllers

import java.io.File

import play.api.mvc.{Action, Controller}
import services.{LocalSaveFile, Rekog}

class searchByPicture extends Controller with Rekog with LocalSaveFile {

  def getPicture() = Action {
    //TODO: hardcoded to go to localhost:9000
    Ok(views.html.getPicture("http://localhost:9000/searchByPicture"))
  }

  def searchByPicture = Action(parse.temporaryFile) { request =>
    val tempFile = "tmp/tempFileFile.txt"
    request.body.moveTo(new File(tempFile), true)

    val (fileName, name) = saveFile(tempFile)
    val faces = searchByFace(fileName)
    val filteredFaces = filterFaces(faces)
      .map(face => s"Name: '${face.getFace.getExternalImageId.replace("_", " ")}' with confidence: ${face.getFace.getConfidence.toString}")
    Ok(views.html.searchByPicture(filteredFaces))
  }
}
