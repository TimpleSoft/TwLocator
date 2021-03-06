package io.keepcoding.twlocator.models.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import io.keepcoding.twlocator.models.Search;
import io.keepcoding.twlocator.models.Tweet;
import io.keepcoding.twlocator.models.db.DBHelper;

import static io.keepcoding.twlocator.models.db.DBConstants.*;


public class TweetDAO implements DAOPersistable<Tweet> {

    private static WeakReference<Context> context = null;
    public static final String[] allColumns = {
            KEY_TWEET_ID,
            KEY_TWEET_USERNAME,
            KEY_TWEET_TEXT,
            KEY_TWEET_LATITUDE,
            KEY_TWEET_LONGITUDE,
            KEY_TWEET_PHOTO_PROFILE_URL,
            KEY_TWEET_SEARCH
    };

    public TweetDAO(@NonNull Context context) {
        this.context = new WeakReference<>(context);
    }

    @Override
    public long insert(@NonNull Tweet data) {
        if (data == null) {
            return DBHelper.INVALID_ID;
        }

        final DBHelper dbHelper = DBHelper.getInstance(context.get());
        SQLiteDatabase db = DBHelper.getDb(dbHelper);

        db.beginTransaction();
        long id = DBHelper.INVALID_ID;

        try {
            id = db.insert(TABLE_TWEET, null, getContentValues(data));
            // data.setId(id);
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
            dbHelper.close();
        }

        return id;
    }


    public static ContentValues getContentValues(Tweet tweet) {

        ContentValues content = new ContentValues();
        content.put(KEY_TWEET_USERNAME, tweet.getUserName());
        content.put(KEY_TWEET_PHOTO_PROFILE_URL, tweet.getURLUserPhotoProfile());
        content.put(KEY_TWEET_TEXT, tweet.getText());
        content.put(KEY_TWEET_LATITUDE, tweet.getLatitude());
        content.put(KEY_TWEET_LONGITUDE, tweet.getLongitude());
        content.put(KEY_TWEET_SEARCH, tweet.getSearch().getId());

        return content;
    }

    @Override
    public void update(long id, @NonNull Tweet data) {
        if (data == null) {
            return;
        }

        final DBHelper dbHelper = DBHelper.getInstance(context.get());
        SQLiteDatabase db = DBHelper.getDb(dbHelper);

        db.beginTransaction();

        try {
            db.update(TABLE_TWEET, getContentValues(data), KEY_TWEET_ID + "=" + id, null);
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
            dbHelper.close();
        }
    }

    @Override
    public void delete(long id) {
        final DBHelper dbHelper = DBHelper.getInstance(context.get());
        SQLiteDatabase db = DBHelper.getDb(dbHelper);

        if (id == DBHelper.INVALID_ID) {
            db.delete(TABLE_TWEET, null, null);
        } else {
            db.delete(TABLE_TWEET, KEY_TWEET_ID + "=?", new String[]{"" + id});
        }

        db.close();
    }

    @Override
    public void delete(@NonNull Tweet data) {
        if (data != null) {
            delete(data.getId());
        }
    }

    @Override
    public void deleteAll() {
        delete(DBHelper.INVALID_ID);
    }

    @Nullable
    @Override
    public Cursor queryCursor() {
        final DBHelper dbHelper = DBHelper.getInstance(context.get());
        SQLiteDatabase db = DBHelper.getDb(dbHelper);

        Cursor cursor = db.query(TABLE_TWEET, allColumns, null, null, null, null, KEY_TWEET_ID);
        return cursor;
    }

    @Override
    public Tweet query(long id) {
        Tweet tweet = null;

        final DBHelper dbHelper = DBHelper.getInstance(context.get());
        SQLiteDatabase db = DBHelper.getDb(dbHelper);

        final String whereClause = KEY_TWEET_ID + "=" + id;
        Cursor cursor = db.query(TABLE_TWEET, allColumns, whereClause, null, null, null, KEY_TWEET_ID);

        if (cursor != null) {
            if (cursor.getCount() > 0) {
                cursor.moveToFirst();

                tweet = tweetFromCursor(cursor);
            }
        }

        cursor.close();
        db.close();

        return tweet;
    }

    public static List<Tweet> query(Search search) {
        List<Tweet> tweets = new ArrayList<>();
        Tweet tweet = null;

        final DBHelper dbHelper = DBHelper.getInstance(context.get());
        SQLiteDatabase db = DBHelper.getDb(dbHelper);

        final String whereClause = KEY_TWEET_SEARCH + "=" + search.getId();
        Cursor cursor = db.query(TABLE_TWEET, allColumns, whereClause, null, null, null, KEY_TWEET_ID);

        if (cursor != null) {
            if (cursor.getCount() > 0) {
                cursor.moveToFirst();
                while (cursor.moveToNext()) {
                    tweet = tweetFromCursor(cursor);
                    tweets.add(tweet);
                }
            }
        }

        cursor.close();
        db.close();

        return tweets;
    }

    @NonNull
    public static Tweet tweetFromCursor(Cursor cursor) {
        Tweet tweet;
        SearchDAO searchDAO = new SearchDAO(context.get());
        Search search = searchDAO.query(cursor.getLong(cursor.getColumnIndex(KEY_TWEET_SEARCH)));
        tweet = new Tweet(cursor.getString(cursor.getColumnIndex(KEY_TWEET_USERNAME)),
                          cursor.getString(cursor.getColumnIndex(KEY_TWEET_PHOTO_PROFILE_URL)),
                          cursor.getString(cursor.getColumnIndex(KEY_TWEET_TEXT)),
                          search,
                          cursor.getDouble(cursor.getColumnIndex(KEY_TWEET_LATITUDE)),
                          cursor.getDouble(cursor.getColumnIndex(KEY_TWEET_LONGITUDE)));
        tweet.setId(cursor.getLong(cursor.getColumnIndex(KEY_TWEET_ID)));
        return tweet;
    }
}
