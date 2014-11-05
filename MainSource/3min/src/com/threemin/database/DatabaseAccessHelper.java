package com.threemin.database;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;
import com.threemin.model.ActivityModel;
import com.threemin.model.UserModel;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

public class DatabaseAccessHelper {
    
    private SQLiteDatabase mDB;
    private MySQLiteHelper mDBHelper;
    
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
        cv.put(MySQLiteHelper.COL_ACT_SUBJECT_ID, act.getSubjectID());
        cv.put(MySQLiteHelper.COL_ACT_SUBJECT_TYPE, act.getSubjectType());
        cv.put(MySQLiteHelper.COL_ACT_UPDATE_TIME, act.getUpdateTime());
        cv.put(MySQLiteHelper.COL_ACT_CONTENT, act.getContent());
        cv.put(MySQLiteHelper.COL_ACT_DISPLAY_IMG_URL, act.getDisplayImageUrl());
        cv.put(MySQLiteHelper.COL_ACT_USER_JSON, new Gson().toJson(act.getUser()));
        
        
        return mDB.insert(MySQLiteHelper.TBL_ACTIVITIES, null, cv);
    }
    
    public void insertListActivities(List<ActivityModel> list) {
        for (ActivityModel act : list) {
            insertActivity(act);
        }
    }
    
    public Cursor getActivitiesCursor() {
        String sortOrder = MySQLiteHelper.COL_ACT_UPDATE_TIME + " DESC";
        return mDB.query(MySQLiteHelper.TBL_ACTIVITIES, //table name
                                null, //columns
                                null, //selection
                                null, //selection agrs
                                null, //group by
                                null, //having
                                sortOrder); //order by
    }
    
    public List<ActivityModel> getListActivities() {
        ArrayList<ActivityModel> list = new ArrayList<ActivityModel>();
        Cursor cursor = getActivitiesCursor();
        cursor.moveToFirst();
        while (cursor.isAfterLast() == false) {
            ActivityModel model = cursorToActivityModel(cursor);
            list.add(model);
            cursor.moveToNext();
        }
        cursor.close();
        return list;
    }
    
    public ActivityModel cursorToActivityModel(Cursor c) {
        ActivityModel model = new ActivityModel();
        model.setId(c.getInt(c.getColumnIndex(MySQLiteHelper.COL_ACT_ID)));
        model.setContent(c.getString(c.getColumnIndex(MySQLiteHelper.COL_ACT_CONTENT)));
        model.setSubjectID(c.getInt(c.getColumnIndex(MySQLiteHelper.COL_ACT_SUBJECT_ID)));
        model.setSubjectType(c.getString(c.getColumnIndex(MySQLiteHelper.COL_ACT_SUBJECT_TYPE)));
        model.setUpdateTime(c.getLong(c.getColumnIndex(MySQLiteHelper.COL_ACT_UPDATE_TIME)));
        model.setDisplayImageUrl(c.getString(c.getColumnIndex(MySQLiteHelper.COL_ACT_DISPLAY_IMG_URL)));
        
        String userJson = c.getString(c.getColumnIndex(MySQLiteHelper.COL_ACT_USER_JSON));
        UserModel userModel = new Gson().fromJson(userJson, UserModel.class);
        model.setUser(userModel);
         return model;
    }
    
    public void deleteAllActivities() {
        mDB.delete(MySQLiteHelper.TBL_ACTIVITIES, null, null);
    }
}
