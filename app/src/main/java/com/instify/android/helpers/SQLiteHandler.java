package com.instify.android.helpers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.HashMap;

public class SQLiteHandler extends SQLiteOpenHelper {

    private static final String TAG = SQLiteHandler.class.getSimpleName();

    // All Static variables
    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "srmerp";

    // Login table name
    private static final String TABLE_USER = "user";

    // Time Table
    public static final String TABLE_TIME_TABLE = "time_table";

    // Login Table Columns names
    private static final String KEY_ID = "id";
    private static final String KEY_NAME = "name";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_UID = "uid";
    private static final String KEY_CREATED_AT = "created_at";
    private static final String KEY_TOKEN = "token";
    private static final String KEY_REGNO = "regno";
    private static final String KEY_DEPT = "dept";

    // Time Table Columns names
    private static final String COL_0 = "id";
    private static final String COL_1 = "day";
    private static final String COL_2 = "h1";
    private static final String COL_3 = "h2";
    private static final String COL_4 = "h3";
    private static final String COL_5 = "h4";
    private static final String COL_6 = "h5";
    private static final String COL_7 = "h6";
    private static final String COL_8 = "h7";

    public SQLiteHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_LOGIN_TABLE = "CREATE TABLE " + TABLE_USER + "("
                + KEY_ID + " INTEGER PRIMARY KEY," + KEY_NAME + " TEXT,"
                + KEY_EMAIL + " TEXT UNIQUE," + KEY_UID + " TEXT," + KEY_TOKEN + " TEXT," + KEY_REGNO + " TEXT," + KEY_DEPT + " TEXT,"
                + KEY_CREATED_AT + " TEXT" + ")";
        db.execSQL(CREATE_LOGIN_TABLE);

        String CREATE_TIME_TABLE = "CREATE TABLE " + TABLE_TIME_TABLE + " (" + COL_0 + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COL_1 + " TEXT, " + COL_2 + " TEXT, " + COL_3 + " TEXT, " + COL_4 + " TEXT, "
                + COL_5 + " TEXT, " + COL_6 + " TEXT, " + COL_7 + " TEXT, " + COL_8 + " TEXT)";
        db.execSQL(CREATE_TIME_TABLE);

        Log.d(TAG, "Database tables created");
    }

    /**
     * Storing user details in database
     */
    public void addUser(String name, String email, String uid, String created_at, String token, String regno, String dept) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_NAME, name); // Name
        values.put(KEY_EMAIL, email); // Email
        values.put(KEY_UID, uid); // Email
        values.put(KEY_CREATED_AT, created_at);
        values.put(KEY_TOKEN, token);
        values.put(KEY_REGNO, regno);
        values.put(KEY_DEPT, dept);// Created At

        // Inserting Row
        long id = db.insert(TABLE_USER, null, values);
        db.close(); // Closing database connection

        Log.d(TAG, "New user inserted into sqlite: " + id);
    }

    /**
     * Getting user data from database
     */
    public HashMap<String, String> getUserDetails() {
        HashMap<String, String> user = new HashMap<>();
        String selectQuery = "SELECT  * FROM " + TABLE_USER;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        // Move to first row
        cursor.moveToFirst();
        if (cursor.getCount() > 0) {
            user.put("name", cursor.getString(1));
            user.put("email", cursor.getString(2));
            user.put("uid", cursor.getString(3));
            user.put("created_at", cursor.getString(4));
            user.put("token", cursor.getString(5));
            user.put("regno", cursor.getString(6));
            user.put("dept", cursor.getString(7));
        }
        cursor.close();
        db.close();
        // return user
        Log.d(TAG, "Fetching user from Sqlite: " + user.toString());

        return user;
    }

    /**
     * Storing user details in database
     */
    public boolean create_tt(String day, String h1, String h2, String h3, String h4, String h5, String h6, String h7) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COL_1, day);
        cv.put(COL_2, h1);
        cv.put(COL_3, h2);
        cv.put(COL_4, h3);
        cv.put(COL_5, h4);
        cv.put(COL_6, h5);
        cv.put(COL_7, h6);
        cv.put(COL_8, h7);
        long result = db.insert(TABLE_TIME_TABLE, null, cv);

        if (result == -1) {
            return false;
        } else {
            return true;
        }
    }

    /**
     * Getting user data from database
     */
    public Cursor gettt() {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("SELECT * FROM " + TABLE_TIME_TABLE, null);

        return res;
    }

    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USER);

        // Create tables again
        onCreate(db);
    }

    /**
     * Re crate database Delete all tables and create them again
     */
    public void deleteUsers() {
        SQLiteDatabase db = this.getWritableDatabase();
        // Delete All Rows
        db.delete(TABLE_USER, null, null);
        db.close();

        Log.d(TAG, "Deleted all user info from sqlite");
    }

}