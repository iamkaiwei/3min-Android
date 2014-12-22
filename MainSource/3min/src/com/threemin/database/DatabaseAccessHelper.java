package com.threemin.database;

import java.util.ArrayList;
import java.util.List;

import com.google.android.gms.drive.internal.CreateContentsRequest;
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
        
        ContentValues cv = createActivityContentValues(act);
        
        UserModel user = act.getUser();
        
        String query = TableUser.COL_01_USER_ID + " = " + user.getId();
        Cursor cursor = mDB.query(TableUser.TBL_NAME, null, query , null, null, null, null);
        if (cursor.getCount() == 0) {
            ContentValues cvUser = createUserContentValues(user);
            mDB.insert(TableUser.TBL_NAME, null, cvUser);
        }
        
        return mDB.insert(TableActivity.TBL_NAME, null, cv);
    }
    
    public ContentValues createActivityContentValues(ActivityModel act) {
        ContentValues cv = new ContentValues();
        cv.put(TableActivity.COL_01_ACTIVITY_ID,        act.getId());
        cv.put(TableActivity.COL_02_SUBJECT_ID,         act.getSubjectID());
        cv.put(TableActivity.COL_03_SUBJECT_TYPE,       act.getSubjectType());
        cv.put(TableActivity.COL_04_DISPLAY_IMG_URL,    act.getDisplayImageUrl());
        cv.put(TableActivity.COL_05_UPDATE_TIME,        act.getUpdateTime());
        cv.put(TableActivity.COL_06_CONTENT,            act.getContent());
        cv.put(TableActivity.COL_07_USER_ID,            act.getUser().getId());
        cv.put(TableActivity.COL_08_CATEGORY,           act.getCategory());
        
        return cv;
    }
    
    public ContentValues createUserContentValues(UserModel u) {
        ContentValues cv = new ContentValues();
        cv.put(TableUser.COL_01_USER_ID,            u.getId());
        cv.put(TableUser.COL_02_EMAIL,              u.getEmail());
        cv.put(TableUser.COL_03_CREATE_AT,          u.getCreateAt());
        cv.put(TableUser.COL_04_UPDATE_AT,          u.getUpdateAt());
        cv.put(TableUser.COL_05_FACEBOOK_ID,        u.getFacebookId());
        cv.put(TableUser.COL_06_USERNAME,           u.getUsername());
        cv.put(TableUser.COL_07_FIRST_NAME,         u.getFirstName());
        cv.put(TableUser.COL_08_LAST_NAME,          u.getLastName());
        cv.put(TableUser.COL_09_MIDDLE_NAME,        u.getMiddleName());
        cv.put(TableUser.COL_10_FULL_NAME,          u.getFullName());
        cv.put(TableUser.COL_11_GENDER,             u.getGender());
        cv.put(TableUser.COL_12_BIRTHDAY,           u.getBirthday());
        cv.put(TableUser.COL_13_UDID,               u.getUdid());
        cv.put(TableUser.COL_14_ROLE,               u.getRole());
        cv.put(TableUser.COL_15_AVATAR,             u.getFacebook_avatar());
        cv.put(TableUser.COL_16_IS_FOLLOWED,        u.isFollowed());
        cv.put(TableUser.COL_17_COUNT_FOLLOWER,     u.getCountFollowers());
        cv.put(TableUser.COL_18_COUNT_FOLLOWING,    u.getCountFollowing());
        cv.put(TableUser.COL_19_COUNT_POSITIVE,     u.getCountPositive());
        cv.put(TableUser.COL_20_COUNT_NEGATIVE,     u.getCountNegative());
        cv.put(TableUser.COL_21_COUNT_NORMAL,       u.getCountNormal());
        cv.put(TableUser.COL_22_COUNT_ACTIVITIES,   u.getCountActivities());
        cv.put(TableUser.COL_23_PERCENT_POSITIVE,   u.getPercentPositive());
        cv.put(TableUser.COL_24_PERCENT_NEGATIVE,   u.getPercentNegative());
        cv.put(TableUser.COL_25_PERCENT_NORMAL,     u.getPercentNormal());
        
        return cv;
    }
    
    public void insertListActivities(List<ActivityModel> list) {
        for (ActivityModel act : list) {
            insertActivity(act);
        }
    }
    
    public Cursor getActivitiesCursor() {
        String sortOrder = TableActivity.COL_05_UPDATE_TIME + " DESC";
        return mDB.query(TableActivity.TBL_NAME, //table name
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
        model.setId                 (c.getInt       (c.getColumnIndex(TableActivity.COL_01_ACTIVITY_ID)));
        model.setSubjectID          (c.getInt       (c.getColumnIndex(TableActivity.COL_02_SUBJECT_ID)));
        model.setSubjectType        (c.getString    (c.getColumnIndex(TableActivity.COL_03_SUBJECT_TYPE)));
        model.setDisplayImageUrl    (c.getString    (c.getColumnIndex(TableActivity.COL_04_DISPLAY_IMG_URL)));
        model.setUpdateTime         (c.getLong      (c.getColumnIndex(TableActivity.COL_05_UPDATE_TIME)));
        model.setContent            (c.getString    (c.getColumnIndex(TableActivity.COL_06_CONTENT)));
        model.setCategory           (c.getInt       (c.getColumnIndex(TableActivity.COL_08_CATEGORY)));
        
        int userID = c.getInt(c.getColumnIndex(TableActivity.COL_07_USER_ID));
        Cursor cursor = mDB.query(
                TableUser.TBL_NAME, 
                null, 
                TableUser.COL_01_USER_ID + " = " + userID, 
                null, 
                null,
                null, 
                null);
        if (cursor.getCount() != 0) {
            cursor.moveToFirst();
            UserModel user = cursorToUserModel(cursor);
            model.setUser(user);
        }
        
        return model;
    }
    
    public UserModel cursorToUserModel(Cursor c) {
        UserModel model = new UserModel();
        model.setId                 (c.getInt       (c.getColumnIndex(TableUser.COL_01_USER_ID)));
        model.setEmail              (c.getString    (c.getColumnIndex(TableUser.COL_02_EMAIL)));
        model.setCreateAt           (c.getString    (c.getColumnIndex(TableUser.COL_03_CREATE_AT)));
        model.setUpdateAt           (c.getString    (c.getColumnIndex(TableUser.COL_04_UPDATE_AT)));
        model.setFacebookId         (c.getString    (c.getColumnIndex(TableUser.COL_05_FACEBOOK_ID)));
        model.setUsername           (c.getString    (c.getColumnIndex(TableUser.COL_06_USERNAME)));
        model.setFirstName          (c.getString    (c.getColumnIndex(TableUser.COL_07_FIRST_NAME)));
        model.setLastName           (c.getString    (c.getColumnIndex(TableUser.COL_08_LAST_NAME)));
        model.setMiddleName         (c.getString    (c.getColumnIndex(TableUser.COL_09_MIDDLE_NAME)));
        model.setFullName           (c.getString    (c.getColumnIndex(TableUser.COL_10_FULL_NAME)));
        model.setGender             (c.getString    (c.getColumnIndex(TableUser.COL_11_GENDER)));
        model.setBirthday           (c.getString    (c.getColumnIndex(TableUser.COL_12_BIRTHDAY)));
        model.setUdid               (c.getString    (c.getColumnIndex(TableUser.COL_13_UDID)));
        model.setRole               (c.getString    (c.getColumnIndex(TableUser.COL_14_ROLE)));
        model.setFacebook_avatar    (c.getString    (c.getColumnIndex(TableUser.COL_15_AVATAR)));
        model.setFollowed           (c.getInt       (c.getColumnIndex(TableUser.COL_16_IS_FOLLOWED)) == 1);
        model.setCountFollowers     (c.getInt       (c.getColumnIndex(TableUser.COL_17_COUNT_FOLLOWER)));
        model.setCountFollowing     (c.getInt       (c.getColumnIndex(TableUser.COL_18_COUNT_FOLLOWING)));
        model.setCountPositive      (c.getInt       (c.getColumnIndex(TableUser.COL_19_COUNT_POSITIVE)));
        model.setCountNegative      (c.getInt       (c.getColumnIndex(TableUser.COL_20_COUNT_NEGATIVE)));
        model.setCountNormal        (c.getInt       (c.getColumnIndex(TableUser.COL_21_COUNT_NORMAL)));
        model.setCountActivities    (c.getInt       (c.getColumnIndex(TableUser.COL_22_COUNT_ACTIVITIES)));
        model.setPercentPositive    (c.getInt       (c.getColumnIndex(TableUser.COL_23_PERCENT_POSITIVE)));
        model.setPercentNegative    (c.getInt       (c.getColumnIndex(TableUser.COL_24_PERCENT_NEGATIVE)));
        model.setPercentNormal      (c.getInt       (c.getColumnIndex(TableUser.COL_25_PERCENT_NORMAL)));
        
        return model;
    }
    
    public void deleteAllActivities() {
        mDB.delete(TableActivity.TBL_NAME, null, null);
    }
}
