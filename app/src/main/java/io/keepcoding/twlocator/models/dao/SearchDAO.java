package io.keepcoding.twlocator.models.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.lang.ref.WeakReference;

import io.keepcoding.twlocator.models.Search;
import io.keepcoding.twlocator.models.db.DBHelper;

import static io.keepcoding.twlocator.models.db.DBConstants.KEY_SEARCH_ID;
import static io.keepcoding.twlocator.models.db.DBConstants.KEY_SEARCH_LATITUDE;
import static io.keepcoding.twlocator.models.db.DBConstants.KEY_SEARCH_LONGITUDE;
import static io.keepcoding.twlocator.models.db.DBConstants.KEY_SEARCH_TEXT;
import static io.keepcoding.twlocator.models.db.DBConstants.TABLE_SEARCH;


public class SearchDAO implements DAOPersistable<Search> {

    private final WeakReference<Context> context;
    public static final String[] allColumns = {
            KEY_SEARCH_ID,
            KEY_SEARCH_TEXT,
            KEY_SEARCH_LATITUDE,
            KEY_SEARCH_LONGITUDE
    };

    public SearchDAO(@NonNull Context context) {
        this.context = new WeakReference<>(context);
    }

    @Override
    public long insert(@NonNull Search data) {
        if (data == null) {
            return DBHelper.INVALID_ID;
        }

        final DBHelper dbHelper = DBHelper.getInstance(context.get());
        SQLiteDatabase db = DBHelper.getDb(dbHelper);

        db.beginTransaction();
        long id = DBHelper.INVALID_ID;

        try {
            id = db.insert(TABLE_SEARCH, null, getContentValues(data));
            db.setTransactionSuccessful();

        } finally {
            db.endTransaction();
            dbHelper.close();
        }

        return id;
    }


    public static ContentValues getContentValues(Search search) {

        ContentValues content = new ContentValues();
        content.put(KEY_SEARCH_TEXT, search.getText());
        content.put(KEY_SEARCH_LATITUDE, search.getLatitude());
        content.put(KEY_SEARCH_LONGITUDE, search.getLongitude());

        return content;
    }

    @Override
    public void update(long id, @NonNull Search data) {
        if (data == null) {
            return;
        }

        final DBHelper dbHelper = DBHelper.getInstance(context.get());
        SQLiteDatabase db = DBHelper.getDb(dbHelper);

        db.beginTransaction();

        try {
            db.update(TABLE_SEARCH, getContentValues(data), KEY_SEARCH_ID + "=" + id, null);
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
            db.delete(TABLE_SEARCH, null, null);
        } else {
            db.delete(TABLE_SEARCH, KEY_SEARCH_ID + "=?", new String[]{"" + id});
        }

        db.close();
    }


    public void delete(long currentId, long lastId) {
        final DBHelper dbHelper = DBHelper.getInstance(context.get());
        SQLiteDatabase db = DBHelper.getDb(dbHelper);

        db.delete(TABLE_SEARCH,
                  KEY_SEARCH_ID + "!=? AND " + KEY_SEARCH_ID + "!=?",
                  new String[]{"" + currentId, "" + lastId});

        db.close();
    }

    @Override
    public void delete(@NonNull Search data) {
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

        Cursor cursor = db.query(TABLE_SEARCH, allColumns, null, null, null, null, KEY_SEARCH_ID);
        return cursor;
    }

    @Override
    public Search query(long id) {
        Search search = null;

        final DBHelper dbHelper = DBHelper.getInstance(context.get());
        SQLiteDatabase db = DBHelper.getDb(dbHelper);

        final String whereClause = KEY_SEARCH_ID + "=" + id;
        Cursor cursor = db.query(TABLE_SEARCH, allColumns, whereClause, null, null, null, KEY_SEARCH_ID);

        if (cursor != null) {
            if (cursor.getCount() > 0) {
                cursor.moveToFirst();

                search = searchFromCursor(cursor);
            }
        }

        cursor.close();
        db.close();

        return search;
    }

    public long getIdLastSearch() {
        long idLastSearch = DBHelper.INVALID_ID;

        final DBHelper dbHelper = DBHelper.getInstance(context.get());
        SQLiteDatabase db = DBHelper.getDb(dbHelper);

        String query = "SELECT " + KEY_SEARCH_ID + " " +
                       "FROM " + TABLE_SEARCH + " " +
                       "ORDER BY " + KEY_SEARCH_ID + " DESC " +
                       "LIMIT 1";
        Cursor cursor = db.rawQuery(query, null);

        if (cursor != null) {
            if (cursor.getCount() > 0) {
                cursor.moveToFirst();

                idLastSearch = cursor.getLong(cursor.getColumnIndex(KEY_SEARCH_ID));
            }
        }

        cursor.close();
        db.close();

        return idLastSearch;
    }

    @NonNull
    public static Search searchFromCursor(Cursor cursor) {
        Search search;
        search = new Search(cursor.getDouble(cursor.getColumnIndex(KEY_SEARCH_LATITUDE)),
                          cursor.getDouble(cursor.getColumnIndex(KEY_SEARCH_LONGITUDE)),
                          cursor.getString(cursor.getColumnIndex(KEY_SEARCH_TEXT)));
        search.setId(cursor.getLong(cursor.getColumnIndex(KEY_SEARCH_ID)));
        return search;
    }

}
