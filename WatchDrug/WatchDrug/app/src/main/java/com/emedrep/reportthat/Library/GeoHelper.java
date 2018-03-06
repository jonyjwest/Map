package com.emedrep.reportthat.Library;

/**
 * Created by eMedrep Nigeria LTD on 10/31/2017.
 */

public class GeoHelper {
    private static final String DIRECTION_API = "https://maps.googleapis.com/maps/api/distancematrix/json?origins=";
    public static final String API_KEY = "AIzaSyAMl7qgaBaIIXQC0z470xG1iyQsjB5lcyM";

    public static String getUrl(String originLat, String originLon, String destinationLat, String destinationLon,String mode){
        return GeoHelper.DIRECTION_API + originLat+","+originLon+"&destinations="+destinationLat+","+destinationLon+"&mode="+mode+"&language=en-EN&key="+API_KEY;

    }



}