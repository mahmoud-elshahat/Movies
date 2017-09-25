package com.example.pc.movies;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import java.util.HashMap;


public class ContactProvider extends ContentProvider {

    static final String PROVIDER_NAME = "com.example.pc.movies.ContactProvider";

    static final String URL = "content://" + PROVIDER_NAME + "/movies";

    static final Uri CONTENT_URL = Uri.parse(URL);

    private static HashMap<String, String> MOVIES_PROJECTION_MAP;

    public static final String ID = "id";
    public static final String RELEASE_DATE = "date";
    public static final String OVERVIEW = "overview";
    public static final String TITLE = "title";
    public static final String VOTE = "vote";
    public static final String POSTER_PATH = "poster";

    static final UriMatcher uriMatcher;


    static final int MOVIES = 1;
    static final int MOVIES_ID = 2;

    static {
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(PROVIDER_NAME, "movies", MOVIES);
        uriMatcher.addURI(PROVIDER_NAME, "movies/#", MOVIES_ID);
    }

    private SQLiteDatabase sql;

    static final String DATABASE_NAME = "udacity";
    static final String TABLE_NAME = "movies";
    static final int DATABASE_VERSION = 1;
    static final String QUERY =
            "CREATE TABLE " + TABLE_NAME +

                    "(id INTEGER PRIMARY KEY , " +
                    "title TEXT NOT NULL ," +
                    "date TEXT NOT NULL ," +
                    "poster TEXT NOT NULL ," +
                    "overview TEXT NOT NULL ," +
                    "vote REAL NOT NULL )";


    @Override
    public boolean onCreate() {
        DatabaseHelper dbHelper = new DatabaseHelper(getContext());

        sql = dbHelper.getWritableDatabase();
        if (sql != null) {
            return true;
        }

        return false;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {


        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
        qb.setTables(TABLE_NAME);

        switch (uriMatcher.match(uri)) {
            case MOVIES:
                qb.setProjectionMap(MOVIES_PROJECTION_MAP);
                break;

            case MOVIES_ID:
                qb.appendWhere(ID + "=" + uri.getPathSegments().get(1));
                break;

            default:
        }


        Cursor c = qb.query(sql, projection, selection,
                selectionArgs, null, null, sortOrder);
        /**
         * register to watch a content URI for changes
         */
        c.setNotificationUri(getContext().getContentResolver(), uri);
        return c;

    }

    @Nullable
    @Override
    public String getType(Uri uri) {


        switch (uriMatcher.match(uri)){
            /**
             * Get all student records
             */
            case MOVIES:
                return "vnd.android.cursor.dir/vnd.example.movies";
            /**
             * Get a particular student
             */
            case MOVIES_ID:
                return "vnd.android.cursor.item/vnd.example.movies";
            default:
                throw new IllegalArgumentException("Unsupported URI: " + uri);
        }
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        long rowID = sql.insert(TABLE_NAME, "", values);

        /**
         * If record is added successfully
         */
        if (rowID > 0) {
            Uri _uri = ContentUris.withAppendedId(CONTENT_URL, rowID);
            getContext().getContentResolver().notifyChange(_uri, null);
            return _uri;
        }

        throw new SQLException("Failed to add a record into " + uri+"  ");
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {

        int count = 0;
        switch (uriMatcher.match(uri)){
            case MOVIES:
                count = sql.delete(TABLE_NAME, selection, selectionArgs);
                break;

            case MOVIES_ID:
                String id = uri.getPathSegments().get(1);
                count = sql.delete( TABLE_NAME, ID +  " = " + id +
                                (!TextUtils.isEmpty(selection) ?
                       " AND (" + selection + ')' : ""), selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        return 0;
    }


    private static class DatabaseHelper extends SQLiteOpenHelper {

        public DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(QUERY);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

            db.execSQL("DROP TABLE IF EXIST" + TABLE_NAME);
            onCreate(db);

        }
    }
}
