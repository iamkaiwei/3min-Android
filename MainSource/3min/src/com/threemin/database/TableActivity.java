package com.threemin.database;

import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class TableActivity {
    
    public static final String tag = "TableActivity";

    public static final String TBL_NAME = "Activity";
    
    public static final String COL_00_ID =              "_id";
    public static final String COL_01_ACTIVITY_ID =     "activity_id";
    public static final String COL_02_SUBJECT_ID =      "subject_id";
    public static final String COL_03_SUBJECT_TYPE =    "subject_type";
    public static final String COL_04_DISPLAY_IMG_URL = "display_img_url";
    public static final String COL_05_UPDATE_TIME =     "update_time";
    public static final String COL_06_CONTENT =         "content";
    public static final String COL_07_USER_ID =         "user_id";
    public static final String COL_08_CATEGORY =        "category";
    
    private static final String CREATE_TABLE = 
            "create table " + TBL_NAME
            + "(" 
            + COL_00_ID +               " integer primary key autoincrement,\n"
            + COL_01_ACTIVITY_ID +      " integer,\n"
            + COL_02_SUBJECT_ID +       " integer,\n" 
            + COL_03_SUBJECT_TYPE +     " text,\n"
            + COL_04_DISPLAY_IMG_URL +  " text,\n" 
            + COL_05_UPDATE_TIME +      " long,\n"
            + COL_06_CONTENT +          " text,\n"
            + COL_07_USER_ID +          " integer,\n"
            + COL_08_CATEGORY +         " integer\n"
            + ");";
    
    public static void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE);
    }
    
    public static void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(tag, "Upgrading database from version " + oldVersion + " to " + newVersion
                + ", which will destroy all old data");
        db.execSQL("DROP TABLE IF EXISTS " + TBL_NAME);
        onCreate(db);
    }
}
