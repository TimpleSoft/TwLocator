package io.keepcoding.twlocator.models.db;

public class DBConstants {

    public static final String DROP_DATABASE = "";

    public static final String TABLE_TWEET = "TWEET";
    public static final String TABLE_TWEET_INFO_URL = "TWEET_URLENTITY";
    public static final String TABLE_SEARCH = "SEARCH";

    // Table field constants TWEET
    public static final String KEY_TWEET_ID = "_id";
    public static final String KEY_TWEET_SEARCH = "search";
    public static final String KEY_TWEET_USERNAME = "username";
    public static final String KEY_TWEET_TEXT = "text";
    public static final String KEY_TWEET_LATITUDE = "latitude";
    public static final String KEY_TWEET_LONGITUDE = "longitude";
    public static final String KEY_TWEET_PHOTO_PROFILE_URL = "photoProfileUrl";

    // Table field constants TWEET_URLENTITY
    public static final String KEY_TWEET_INFO_URL_ID = "_id";
    public static final String KEY_TWEET_INFO_URL_URL = "url";
    public static final String KEY_TWEET_INFO_URL_TWEET = "tweet";

    // Table field constants SEARCH
    public static final String KEY_SEARCH_ID = "_id";
    public static final String KEY_SEARCH_LATITUDE = "latitude";
    public static final String KEY_SEARCH_LONGITUDE = "longitude";
    public static final String KEY_SEARCH_TEXT = "text";

    // scripts creación

    public static final String SQL_CREATE_SEARCH_TABLE =
            "create table "
                    + TABLE_SEARCH + "( "
                    + KEY_SEARCH_ID + " INTEGER primary key autoincrement, "
                    + KEY_SEARCH_TEXT + " TEXT not null, "
                    + KEY_SEARCH_LATITUDE + " REAL not null, "
                    + KEY_SEARCH_LONGITUDE + " REAL not null "
                    + ");";

    public static final String SQL_CREATE_TWEET_TABLE =
            "create table "
                    + TABLE_TWEET + "( "
                    + KEY_TWEET_ID + " INTEGER primary key autoincrement, "
                    + KEY_TWEET_USERNAME + " TEXT not null,"
                    + KEY_TWEET_TEXT + " TEXT not null,"
                    + KEY_TWEET_LATITUDE + " REAL not null,"
                    + KEY_TWEET_LONGITUDE + " REAL not null,"
                    + KEY_TWEET_PHOTO_PROFILE_URL + " TEXT not null, "
                    + KEY_TWEET_SEARCH + " INTEGER,"
                    + "FOREIGN KEY(" + KEY_TWEET_SEARCH + ") REFERENCES " + TABLE_SEARCH + "(" + KEY_SEARCH_ID + ") ON DELETE CASCADE"
                    + ");";


    public static final String SQL_CREATE_TWEET_INFO_URL_TABLE =
            "create table "
                    + TABLE_TWEET_INFO_URL + "( "
                    + KEY_TWEET_INFO_URL_ID + " INTEGER primary key autoincrement, "
                    + KEY_TWEET_INFO_URL_URL + " TEXT not null,"
                    + KEY_TWEET_INFO_URL_TWEET + " INTEGER,"
                    + "FOREIGN KEY(" + KEY_TWEET_INFO_URL_TWEET + ") REFERENCES " + TABLE_TWEET + "(" + KEY_TWEET_ID + ") ON DELETE CASCADE"
                    + ");";

    public static final String[] CREATE_DATABASE = {
            SQL_CREATE_SEARCH_TABLE,
            SQL_CREATE_TWEET_TABLE,
            SQL_CREATE_TWEET_INFO_URL_TABLE
    };

}
