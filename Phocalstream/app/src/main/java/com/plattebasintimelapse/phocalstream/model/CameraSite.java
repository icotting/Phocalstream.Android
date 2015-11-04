package com.plattebasintimelapse.phocalstream.model;

public class CameraSite {

    private Details Details;
    private double Latitude;
    private double Longitude;

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
