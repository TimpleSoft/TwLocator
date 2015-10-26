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
    private List<URLEntity> mURLEntityList;

    public Tweet(String userName, String URLUserPhotoProfile, String text) {
        mUserName = userName;
        mURLUserPhotoProfile = URLUserPhotoProfile;
        mText = text;
        mURLEntityList = new ArrayList<>();
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

}
