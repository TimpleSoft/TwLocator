package io.keepcoding.twlocator.models;

import java.lang.ref.WeakReference;

import io.keepcoding.twlocator.models.db.DBConstants;
import io.keepcoding.twlocator.models.db.DBHelper;

public class TweetInfoURL {

    private long mId;
    private String mText;
    private WeakReference<Tweet> mTweet;

    public TweetInfoURL(long id, String text, WeakReference<Tweet> tweet) {
        mId = id;
        mText = text;
        mTweet = tweet;
    }

    public TweetInfoURL(String text, WeakReference<Tweet> tweet) {
        this(DBHelper.INVALID_ID, text, tweet);
    }

    public String getText() {
        return mText;
    }

    public void setText(String text) {
        mText = text;
    }

    public WeakReference<Tweet> getTweet() {
        return mTweet;
    }

    public void setTweet(WeakReference<Tweet> tweet) {
        mTweet = tweet;
    }

    public long getId() {
        return mId;
    }

    public void setId(long id) {
        mId = id;
    }



}
