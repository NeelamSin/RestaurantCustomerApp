package com.eosinfotech.restaurantcustomerui.DeliveryGmaps;

import com.google.android.gms.maps.model.LatLng;

public class FetchUrl {
    public static String getUrl(LatLng origin, LatLng dest) {
        String str_origin = "origin=" + origin.latitude + "," + origin.longitude;
        String str_dest = "destination=" + dest.latitude + "," + dest.longitude;
        String sensor = "sensor=false";
        String parameters = str_origin + "&" + str_dest + "&" + sensor;
        String output = "json";
        return "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters + "&key=" + "AIzaSyDL4jXQt-ckct-fwtQ8PGjgk1AeSV3tcLI"+ "&sensor=true";
    }
}
