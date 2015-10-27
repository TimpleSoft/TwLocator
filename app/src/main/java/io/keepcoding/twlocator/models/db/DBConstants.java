package io.keepcoding.twlocator.models.db;

public class DBConstants {

    public static final String DROP_DATABASE = "";

    public static final String TABLE_TWEET = "TWEET";
    public static final String TABLE_TWEET_INFO_URL = "TWEET_URLENTITY";

    // Table field constants TWEET
    public static final String KEY_TWEET_ID = "_id";
    public static final String KEY_TWEET_USERNAME = "username";
    public static final String KEY_TWEET_TEXT = "text";
    public static final String KEY_TWEET_LATITUDE = "latitude";
    public static final String KEY_TWEET_LONGITUDE = "longitude";
    public static final String KEY_TWEET_PHOTO_PROFILE_URL = "photoProfileUrl";
    //public static final String KEY_TWEET_LAST_SEARCH = "photoProfileUrl";

    // Table field constants TWEET_URLENTITY
    public static final String KEY_TWEET_INFO_URL_ID = "_id";
    public static final String KEY_TWEET_INFO_URL_URL = "url";
    public static final String KEY_TWEET_INFO_URL_TWEET = "tweet";

    // scripts creación

    public static final String SQL_CREATE_TWEET_TABLE =
            "create table "
                    + TABLE_TWEET + "( " + KEY_TWEET_ID
                    + " INTEGER primary key autoincrement, "
                    + KEY_TWEET_USERNAME + " TEXT not null,"
                    + KEY_TWEET_TEXT + " TEXT not null,"
                    + KEY_TWEET_LATITUDE + " REAL not null,"
                    + KEY_TWEET_LONGITUDE + " REAL not null,"
                    //+ KEY_TWEET_LAST_SEARCH + " BOOLEAN not null,"
                    + KEY_TWEET_PHOTO_PROFILE_URL + " TEXT not null "
                    + ");";


    public static final String SQL_CREATE_TWEET_INFO_URL_TABLE =
            "create table "
                    + TABLE_TWEET_INFO_URL + "( " + KEY_TWEET_INFO_URL_ID + " INTEGER primary key autoincrement, "
                    + KEY_TWEET_INFO_URL_URL + " TEXT not null,"
                    + KEY_TWEET_INFO_URL_TWEET + " INTEGER,"
                    + "FOREIGN KEY(" + KEY_TWEET_INFO_URL_TWEET + ") REFERENCES " + TABLE_TWEET + "(" + KEY_TWEET_ID + ") ON DELETE CASCADE"
                    + ");";

    public static final String[] CREATE_DATABASE = {
            SQL_CREATE_TWEET_TABLE,
            SQL_CREATE_TWEET_INFO_URL_TABLE
    };

}
