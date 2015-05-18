package com.plattebasintimelapse.phocalstream.model;

/**
 * Created by ZachChristensen on 5/15/15.
 */
public class CameraSite {

    Details Details;
    double Latitude;
    double Longitude;

    public CameraSite() {
    }

    public Details getDetails() {
        return Details;
    }

    public void setDetails(Details details) {
        this.Details = details;
    }

    public double getLatitude() {
        return Latitude;
    }

    public void setLatitude(double latitude) {
        Latitude = latitude;
    }

    public double getLongitude() {
        return Longitude;
    }

    public void setLongitude(double longitude) {
        Longitude = longitude;
    }
}
