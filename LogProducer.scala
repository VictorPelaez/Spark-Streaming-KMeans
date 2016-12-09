package clickstream

import java.io.FileWriter

import config.Settings
import org.apache.commons.io.FileUtils
import scala.util.Random
import models.Geolocation.generateRandomLatitudeLongitudeWithinRadius

/**
  * Created by Victor Pelaez on 08/12/2016.
  */
object LogProducer extends App {
  // WebLog config
  val wlc = Settings.WebLogGen

  val Products = scala.io.Source.fromInputStream(getClass.getResourceAsStream("/products.csv")).getLines().toArray
  val Referrers = scala.io.Source.fromInputStream(getClass.getResourceAsStream("/referrers.csv")).getLines().toArray
  val Visitors = (0 to wlc.visitors).map("Visitor-" + _)
  val Pages = (0 to wlc.pages).map("Page-" + _)

  val rnd = new Random()
  val filePath = wlc.filePath
  val destPath = wlc.destPath

  var init_timestamp = System.currentTimeMillis()/1000

  for (fileCount <- 1 to wlc.numberOfFiles) {

    val fw = new FileWriter(filePath, true)

    // introduce some randomness to time increments for demo purposes
    val incrementTimeEvery = rnd.nextInt(wlc.records - 1) + 1

    var timestamp = System.currentTimeMillis()
    var adjustedTimestamp = timestamp

    for (iteration <- 1 to wlc.records) {
      adjustedTimestamp = adjustedTimestamp + ((System.currentTimeMillis() - timestamp) * wlc.timeMultiplier)
      timestamp = System.currentTimeMillis() // move all this to a function
      val action = iteration % (rnd.nextInt(200) + 1) match {
          case 0 => "purchase"
          case 1 => "add_to_cart"
          case _ => "page_view"
        }
      val referrer = Referrers(rnd.nextInt(Referrers.length - 1))
      val prevPage = referrer match {
        case "Internal" => Pages(rnd.nextInt(Pages.length - 1))
        case _ => ""
      }
      val visitor = Visitors(rnd.nextInt(Visitors.length - 1))
      val page = Pages(rnd.nextInt(Pages.length - 1))
      val product = Products(rnd.nextInt(Products.length - 1))

      val change_timestamp = System.currentTimeMillis()/1000
      println(change_timestamp - init_timestamp)
      val (lat, lon) = rnd.nextInt(3) match {
        case 0 => generateRandomLatitudeLongitudeWithinRadius(40.415, -3.707, 50)
        case 1 => generateRandomLatitudeLongitudeWithinRadius (37.604, -0.934, 50)
        case 2 if (change_timestamp - init_timestamp)<30 => generateRandomLatitudeLongitudeWithinRadius(38.994, -1.859, 50)
        case 2 if (change_timestamp - init_timestamp)>=30 => generateRandomLatitudeLongitudeWithinRadius (39.010, -3.500, 50)
      }


      val line = s"$adjustedTimestamp\t$referrer\t$action\t$prevPage\t$visitor\t$page\t$product\t$lat\t$lon\n"
      fw.write(line)

      if (iteration % incrementTimeEvery == 0) {
        println(s"Sent $iteration messages!")
        val sleeping = rnd.nextInt(incrementTimeEvery * 60)
        println(s"Sleeping for $sleeping ms")
        Thread sleep sleeping
      }

    }
    fw.close()

    val outputFile = FileUtils.getFile(s"${destPath}data_$timestamp")
    println(s"Moving produced data to $outputFile")
    FileUtils.moveFile(FileUtils.getFile(filePath), outputFile)
    val sleeping = 5000
    println(s"Sleeping for $sleeping ms")
  }
}