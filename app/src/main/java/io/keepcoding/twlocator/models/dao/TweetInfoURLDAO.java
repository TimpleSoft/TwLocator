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

import io.keepcoding.twlocator.models.Tweet;
import io.keepcoding.twlocator.models.TweetInfoURL;
import io.keepcoding.twlocator.models.db.DBHelper;

import static io.keepcoding.twlocator.models.db.DBConstants.KEY_TWEET_ID;
import static io.keepcoding.twlocator.models.db.DBConstants.KEY_TWEET_INFO_URL_ID;
import static io.keepcoding.twlocator.models.db.DBConstants.KEY_TWEET_INFO_URL_TWEET;
import static io.keepcoding.twlocator.models.db.DBConstants.KEY_TWEET_INFO_URL_URL;
import static io.keepcoding.twlocator.models.db.DBConstants.KEY_TWEET_PHOTO_PROFILE_URL;
import static io.keepcoding.twlocator.models.db.DBConstants.KEY_TWEET_TEXT;
import static io.keepcoding.twlocator.models.db.DBConstants.KEY_TWEET_USERNAME;
import static io.keepcoding.twlocator.models.db.DBConstants.TABLE_TWEET;
import static io.keepcoding.twlocator.models.db.DBConstants.TABLE_TWEET_INFO_URL;


public class TweetInfoURLDAO implements DAOPersistable<TweetInfoURL> {

    private static WeakReference<Context> context = null;
    public static final String[] allColumns = {
            KEY_TWEET_INFO_URL_ID,
            KEY_TWEET_INFO_URL_URL,
            KEY_TWEET_INFO_URL_TWEET
    };

    public TweetInfoURLDAO(@NonNull Context context) {
        this.context = new WeakReference<>(context);
    }

    @Override
    public long insert(@NonNull TweetInfoURL data) {
        if (data == null) {
            return DBHelper.INVALID_ID;
        }

        final DBHelper dbHelper = DBHelper.getInstance(context.get());
        SQLiteDatabase db = DBHelper.getDb(dbHelper);

        db.beginTransaction();
        long id = DBHelper.INVALID_ID;

        try {
            id = db.insert(TABLE_TWEET_INFO_URL, null, getContentValues(data));
            // data.setId(id);
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
            dbHelper.close();
        }

        return id;
    }


    public static ContentValues getContentValues(TweetInfoURL tweetInfoURL) {

        ContentValues content = new ContentValues();
        content.put(KEY_TWEET_INFO_URL_URL, tweetInfoURL.getText());
        content.put(KEY_TWEET_INFO_URL_TWEET, tweetInfoURL.getTweet().get().getId());

        return content;
    }

    @Override
    public void update(long id, @NonNull TweetInfoURL data) {
        if (data == null) {
            return;
        }

        final DBHelper dbHelper = DBHelper.getInstance(context.get());
        SQLiteDatabase db = DBHelper.getDb(dbHelper);

        db.beginTransaction();

        try {
            db.update(TABLE_TWEET_INFO_URL, getContentValues(data), KEY_TWEET_INFO_URL_ID + "=" + id, null);
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
            db.delete(TABLE_TWEET_INFO_URL, null, null);
        } else {
            db.delete(TABLE_TWEET_INFO_URL, KEY_TWEET_INFO_URL_ID + "=?", new String[]{"" + id});
        }

        db.close();
    }

    @Override
    public void delete(@NonNull TweetInfoURL data) {
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

        Cursor cursor = db.query(TABLE_TWEET_INFO_URL, allColumns, null, null, null, null, KEY_TWEET_INFO_URL_ID);
        return cursor;
    }

    @Override
    public TweetInfoURL query(long id) {
        TweetInfoURL tweetInfoURL = null;

        final DBHelper dbHelper = DBHelper.getInstance(context.get());
        SQLiteDatabase db = DBHelper.getDb(dbHelper);

        final String whereClause = KEY_TWEET_INFO_URL_ID + "=" + id;
        Cursor cursor = db.query(TABLE_TWEET_INFO_URL, allColumns, whereClause, null, null, null, KEY_TWEET_INFO_URL_ID);

        if (cursor != null) {
            if (cursor.getCount() > 0) {
                cursor.moveToFirst();

                tweetInfoURL = tweetFromCursor(cursor);
            }
        }

        cursor.close();
        db.close();

        return tweetInfoURL;
    }

    public ArrayList<TweetInfoURL> query(Tweet tweet) {
        ArrayList<TweetInfoURL> tweetInfoURLArrayList = new ArrayList<>();

        final DBHelper dbHelper = DBHelper.getInstance(context.get());
        SQLiteDatabase db = DBHelper.getDb(dbHelper);

        final String whereClause = KEY_TWEET_INFO_URL_TWEET + "=" + tweet.getId();
        Cursor cursor = db.query(TABLE_TWEET_INFO_URL, allColumns, whereClause, null, null, null, KEY_TWEET_INFO_URL_ID);

        if (cursor != null) {
            if (cursor.getCount() > 0) {
                cursor.moveToFirst();

                TweetInfoURL tweetInfoURL = tweetFromCursor(cursor);
                tweetInfoURLArrayList.add(tweetInfoURL);
            }
        }

        cursor.close();
        db.close();

        return tweetInfoURLArrayList;
    }

    @NonNull
    public static TweetInfoURL tweetFromCursor(Cursor cursor) {
        TweetInfoURL tweetInfoURL;
        TweetDAO tweetDAO = new TweetDAO(context.get());
        Tweet tweet = tweetDAO.query(cursor.getLong(cursor.getColumnIndex(KEY_TWEET_INFO_URL_ID)));
        tweetInfoURL = new TweetInfoURL(
                cursor.getString(cursor.getColumnIndex(KEY_TWEET_INFO_URL_URL)),
                new WeakReference<>(tweet));
        tweetInfoURL.setId(cursor.getLong(cursor.getColumnIndex(KEY_TWEET_INFO_URL_ID)));
        return tweetInfoURL;
    }
}
