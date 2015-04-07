package org.heinze.brainerd.framework.util

import java.io._


/**
 * Thomson Reuters
 * Research & Development
 * User: U0069724
 * Date: 7/13/12
 * Time: 1:57 PM
 */


object ReadFiles {


  def getFiles(dirName: String): List[File] = {
    val files = addFiles(Nil, new File(dirName))
    files
  }


  private def addFiles(fileList: List[File], dir: File): List[File] = {

    var files = fileList
    if (!dir.isDirectory) {
      if (!dir.getName.contains(".DS_Store")) {
        files = dir :: files
      }

    } else
      for (file <- dir.listFiles)
        files = addFiles(files, file)
    files
  }
}



