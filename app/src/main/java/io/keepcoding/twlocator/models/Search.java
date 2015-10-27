package io.keepcoding.twlocator.models;

public class Search {

    private long mId;
    private double mLatitude;
    private double mLongitude;
    private String mText;

    public Search(double latitude, double longitude, String text) {
        mLatitude = latitude;
        mLongitude = longitude;
        mText = text;
    }

    public long getId() {
        return mId;
    }

    public void setId(long id) {
        mId = id;
    }

    public double getLatitude() {
        return mLatitude;
    }

    public void setLatitude(double latitude) {
        mLatitude = latitude;
    }

    public double getLongitude() {
        return mLongitude;
    }

    public void setLongitude(double longitude) {
        mLongitude = longitude;
    }

    public String getText() {
        return mText;
    }

    public void setText(String text) {
        mText = text;
    }
}
