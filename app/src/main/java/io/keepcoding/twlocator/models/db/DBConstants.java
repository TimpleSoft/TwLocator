package io.keepcoding.twlocator.models.db;

public class DBConstants {

    public static final String DROP_DATABASE = "";

    public static final String TABLE_TWEET = "TWEET";
    public static final String TABLE_TWEET_INFO_URL = "TWEET_URLENTITY";

    // Table field constants TWEET
    public static final String KEY_TWEET_ID = "_id";
    public static final String KEY_TWEET_USERNAME = "username";
    public static final String KEY_TWEET_TEXT = "text";
    public static final String KEY_TWEET_PHOTO_PROFILE_URL = "photoProfileUrl";

    // Table field constants TWEET_URLENTITY
    public static final String KEY_TWEET_INFO_URL_ID = "_id";
    public static final String KEY_TWEET_INFO_URL_URL = "url";
    public static final String KEY_TWEET_INFO_URL_TWEET = "tweet";

    // scripts creación

    public static final String SQL_CREATE_TWEET_TABLE =
            "create table "
                    + TABLE_TWEET + "( " + KEY_TWEET_ID
                    + " integer primary key autoincrement, "
                    + KEY_TWEET_USERNAME + " text not null,"
                    + KEY_TWEET_TEXT + " text not null,"
                    + KEY_TWEET_PHOTO_PROFILE_URL + " text not null "
                    + ");";


    public static final String SQL_CREATE_TWEET_INFO_URL_TABLE =
            "create table "
                    + TABLE_TWEET_INFO_URL + "( " + KEY_TWEET_INFO_URL_ID + " integer primary key autoincrement, "
                    + KEY_TWEET_INFO_URL_URL + " text not null,"
                    + KEY_TWEET_INFO_URL_TWEET + " INTEGER,"
                    + "FOREIGN KEY(" + KEY_TWEET_INFO_URL_TWEET + ") REFERENCES " + TABLE_TWEET + "(" + KEY_TWEET_ID + ") ON DELETE CASCADE"
                    + ");";

    public static final String[] CREATE_DATABASE = {
            SQL_CREATE_TWEET_TABLE,
            SQL_CREATE_TWEET_INFO_URL_TABLE
    };

}
