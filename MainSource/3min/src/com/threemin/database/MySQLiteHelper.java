package com.threemin.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class MySQLiteHelper extends SQLiteOpenHelper {
    
    public static final String tag = "MySQLiteHelper";
    
    private static final String DATABASE_NAME = "ThreeMins.db";
    private static final int DATABASE_VERSION = 1;
    
    public static final String TBL_ACTIVITIES = "activities";
    public static final String COL_ACT_ID = "_id";
    public static final String COL_ACT_SUBJECT_ID = "subjectID";
    public static final String COL_ACT_SUBJECT_TYPE = "subjectType";
    public static final String COL_ACT_DISPLAY_IMG_URL = "displayImgUrl";
    public static final String COL_ACT_UPDATE_TIME = "updateTime";
    public static final String COL_ACT_CONTENT = "content";
    public static final String COL_ACT_USER_JSON = "userJson";
    
    private static final String DATABASE_CREATE = 
            "create table " + TBL_ACTIVITIES
            + "(" + COL_ACT_ID + " integer primary key,\n"
            + COL_ACT_SUBJECT_ID + " integer,\n" 
            + COL_ACT_SUBJECT_TYPE + " text,\n"
            + COL_ACT_CONTENT + " text,\n"
            + COL_ACT_DISPLAY_IMG_URL + " text,\n" 
            + COL_ACT_USER_JSON + " text,\n"
            + COL_ACT_UPDATE_TIME + " long);";

    public MySQLiteHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
      }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.i(tag, "SQL create table: " + DATABASE_CREATE);
        db.execSQL(DATABASE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(tag, "Upgrading database from version " + oldVersion + " to " + newVersion
                + ", which will destroy all old data");
        db.execSQL("DROP TABLE IF EXISTS " + TBL_ACTIVITIES);
        onCreate(db);
    }

}
