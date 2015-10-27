package io.keepcoding.twlocator.models;

import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import twitter4j.URLEntity;

public class Tweet {

    private long mId;
    private String mUserName;
    private String mURLUserPhotoProfile;
    private String mText;
    private double mLatitude;
    private double mLongitude;
    private List<URLEntity> mURLEntityList;

    public Tweet(String userName, String URLUserPhotoProfile, String text) {
        mUserName = userName;
        mURLUserPhotoProfile = URLUserPhotoProfile;
        mText = text;
        mURLEntityList = new ArrayList<>();
    }

    public Tweet(String userName, String URLUserPhotoProfile, String text, double latitude, double longitude) {
        this(userName, URLUserPhotoProfile, text);
        mLatitude = latitude;
        mLongitude = longitude;
    }

    // lazy getter
    public List<URLEntity> allURLEntities() {
        if (mURLEntityList == null) {
            mURLEntityList = new ArrayList<>();
        }

        return mURLEntityList;
    }

    /**
     * This method adds a non null urlEntity
     * @param urlEntity the URLEntity to add
     */
    public void addURLEntity(@NonNull final URLEntity urlEntity) {
        if (urlEntity != null) {
            allURLEntities().add(urlEntity);
        }
    }

    public long getId() {
        return mId;
    }

    public void setId(long id) {
        mId = id;
    }

    public String getUserName() {
        return mUserName;
    }

    public void setUserName(String userName) {
        mUserName = userName;
    }

    public String getURLUserPhotoProfile() {
        return mURLUserPhotoProfile;
    }

    public void setURLUserPhotoProfile(String URLUserPhotoProfile) {
        mURLUserPhotoProfile = URLUserPhotoProfile;
    }

    public String getText() {
        return mText;
    }

    public void setText(String text) {
        mText = text;
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
}
