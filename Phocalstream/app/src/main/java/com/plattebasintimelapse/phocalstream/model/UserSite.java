package com.plattebasintimelapse.phocalstream.model;

public class UserSite {

    private long CollectionID;
    private String Name;
    private int PhotoCount;
    private String From;
    private String To;
    private long CoverPhotoID;


    public UserSite() {
    }

    public long getCollectionID() {
        return CollectionID;
    }

    public void setCollectionID(long collectionID) {
        CollectionID = collectionID;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public int getPhotoCount() {
        return PhotoCount;
    }

    public void setPhotoCount(int photoCount) {
        PhotoCount = photoCount;
    }

    public String getFrom() {
        return From;
    }

    public void setFrom(String from) {
        From = from;
    }

    public String getTo() {
        return To;
    }

    public void setTo(String to) {
        To = to;
    }

    public long getCoverPhotoID() {
        return CoverPhotoID;
    }

    public void setCoverPhotoID(long coverPhotoID) {
        CoverPhotoID = coverPhotoID;
    }
}
