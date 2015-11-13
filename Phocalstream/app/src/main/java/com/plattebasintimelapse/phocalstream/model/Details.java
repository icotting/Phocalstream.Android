package com.plattebasintimelapse.phocalstream.model;

public class Details {

    private String SiteName;
    private String First;
    private String Last;
    private int PhotoCount;
    private long SiteID;
    private int CoverPhotoID;
    private int LastPhotoID;
    private String LastPhotoURL;

    public Details() {

    }

    public String getSiteName() {
        return SiteName;
    }

    public void setSiteName(String siteName) {
        SiteName = siteName;
    }

    public String getFirst() {
        return First;
    }

    public void setFirst(String first) {
        First = first;
    }

    public String getLast() {
        return Last;
    }

    public void setLast(String last) {
        Last = last;
    }

    public int getPhotoCount() {
        return PhotoCount;
    }

    public void setPhotoCount(int photoCount) {
        PhotoCount = photoCount;
    }

    public long getSiteID() {
        return SiteID;
    }

    public void setSiteID(long siteID) {
        SiteID = siteID;
    }

    public int getCoverPhotoID() {
        return CoverPhotoID;
    }

    public void setCoverPhotoID(int coverPhotoID) {
        CoverPhotoID = coverPhotoID;
    }

    public int getLastPhotoID() {
        return LastPhotoID;
    }

    public void setLastPhotoID(int lastPhotoID) {
        LastPhotoID = lastPhotoID;
    }

    public String getLastPhotoURL() {
        return LastPhotoURL;
    }

    public void setLastPhotoURL(String lastPhotoURL) {
        LastPhotoURL = lastPhotoURL;
    }
}
