package com.plattebasintimelapse.phocalstream.model;

/**
 * Created by ZachChristensen on 5/15/15.
 */
public class UserSite {

    long CollectionID;
    String Name;
    int PhotoCount;
    String From;
    String To;
    long CoverPhotoID;


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
