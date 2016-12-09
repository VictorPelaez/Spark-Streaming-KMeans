/**
  * Created by victor on 07/12/2016.
  */
package object models {

  object Geolocation {

    // =================================================================================
    //	                     calculateNewLocationFromDistanceAndBearing
    //
    //    Calculate a new position form the distance and bearing from the
    //        latitude and longitude passed to this function
    def calculateNewLocationFromDistanceAndBearing(latitude:Double, longitude:Double, distanceKm:Double,
                                                   bearing:Double): (Double, Double) = {

      var radius = 6371.0  // Radius of the earth 6371.0

      var lat1 = latitude.toRadians
      var lon1 = longitude.toRadians

      var lat2 = Math.asin( Math.sin(lat1)*Math.cos(distanceKm/radius) +
        Math.cos(lat1)*Math.sin(distanceKm/radius)*Math.cos(bearing) );
      var lon2 = lon1 + Math.atan2(Math.sin(bearing)*Math.sin(distanceKm/radius)*Math.cos(lat1),
        Math.cos(distanceKm/radius)-Math.sin(lat1)*Math.sin(lat2));

      return (lat2.toDegrees, lon2.toDegrees)

    } // calculateNewLocationFromDistanceAndBearing


    // =================================================================================
    //                         generateRandomLatitudeLongitudeWithinRadius
    //
    def generateRandomLatitudeLongitudeWithinRadius (latitude:Double, longitude:Double, radiusKm:Double): (Double, Double) = {
      var bearing = Random.nextInt(360).toDouble * Random.nextDouble
      var distanceKm = radiusKm * Random.nextDouble

      var(lat2, lon2) =  models.Geolocation.calculateNewLocationFromDistanceAndBearing(latitude, longitude, distanceKm, bearing)
      return (lat2, lon2)
    } // End of generateRandomLatitudeLongitudeWithinRadius


  } // End of object Geolocation

}
