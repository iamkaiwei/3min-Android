package com.threemin.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class MySQLiteHelper extends SQLiteOpenHelper {
    
    public static final String tag = "MySQLiteHelper";
    
    private static final String DATABASE_NAME = "ThreeMins.db";
    private static final int DATABASE_VERSION = 1;
    
    public MySQLiteHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
      }

    @Override
    public void onCreate(SQLiteDatabase db) {
        TableActivity.onCreate(db);
        TableUser.onCreate(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        TableActivity.onUpgrade(db, oldVersion, newVersion);
        TableUser.onUpgrade(db, oldVersion, newVersion);
    }

}
