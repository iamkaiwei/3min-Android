package com.threemin.database;

import com.google.gson.Gson;
import com.threemin.model.ActivityModel;

import android.content.ContentValues;
import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

public class DatabaseAccessHelper {
    
    private SQLiteDatabase mDB;
    private MySQLiteHelper mDBHelper;
//    private String[] allActivitiesColumns = {MySQLiteHelper.COL_ACT_ID, MySQLiteHelper.COL_ACT_SUBJECT_ID,
//            MySQLiteHelper.COL_ACT_SUBJECT_TYPE, MySQLiteHelper.COL_ACT_UPDATE_TIME};
    private String[] allActivitiesColumns = {MySQLiteHelper.COL_ACT_ID, MySQLiteHelper.COL_ACT_JSON};
    
    public DatabaseAccessHelper(Context context) {
        mDBHelper = new MySQLiteHelper(context);
    }
        
    public void openDatabase() throws SQLException {
        mDB = mDBHelper.getWritableDatabase();
    }
    
    public void closeDatabase() {
        mDBHelper.close();
    }
    
    public long insertActivity(ActivityModel act) {
        if (mDB == null) {
            mDB = mDBHelper.getWritableDatabase();
        }
        
        ContentValues cv = new ContentValues();
        cv.put(MySQLiteHelper.COL_ACT_ID, act.getId());
        String json = new Gson().toJson(act);
        cv.put(MySQLiteHelper.COL_ACT_JSON, json);
        
        return mDB.insert(MySQLiteHelper.TBL_ACTIVITIES, null, cv);
    }
}
