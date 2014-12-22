package com.threemin.database;

import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class TableUser {
    
    public static final String tag =                        "TableUser";
    public static final String TBL_NAME =                   "User";
    public static final String COL_00_ID =                  "_id";
    public static final String COL_01_USER_ID =             "user_id";
    public static final String COL_02_EMAIL =               "email";
    public static final String COL_03_CREATE_AT =           "create_at";
    public static final String COL_04_UPDATE_AT =           "update_at";
    public static final String COL_05_FACEBOOK_ID =         "facebook_id";
    public static final String COL_06_USERNAME =            "username";
    public static final String COL_07_FIRST_NAME =          "first_name";
    public static final String COL_08_LAST_NAME =           "last_name";
    public static final String COL_09_MIDDLE_NAME =         "middle_name";
    public static final String COL_10_FULL_NAME =           "full_name";
    public static final String COL_11_GENDER =              "gender";
    public static final String COL_12_BIRTHDAY =            "birthday";
    public static final String COL_13_UDID =                "udid";
    public static final String COL_14_ROLE =                "role";
    public static final String COL_15_AVATAR =              "avatar";
    public static final String COL_16_IS_FOLLOWED =         "is_followed";
    public static final String COL_17_COUNT_FOLLOWER =      "count_follower";
    public static final String COL_18_COUNT_FOLLOWING =     "count_following";
    public static final String COL_19_COUNT_POSITIVE =      "count_positive";
    public static final String COL_20_COUNT_NEGATIVE =      "count_negative";
    public static final String COL_21_COUNT_NORMAL =        "count_normal";
    public static final String COL_22_COUNT_ACTIVITIES =    "count_activities";
    public static final String COL_23_PERCENT_POSITIVE =    "percent_positive";
    public static final String COL_24_PERCENT_NEGATIVE =    "percent_negative";
    public static final String COL_25_PERCENT_NORMAL =      "percent_normal";
    
    private static String CREATE_TABLE = 
            "create table " + TBL_NAME
            + " ("
            + COL_00_ID +               " integer primary key autoincrement,\n"
            + COL_01_USER_ID +          " integer,\n"
            + COL_02_EMAIL +            " text,\n"
            + COL_03_CREATE_AT +        " text,\n"
            + COL_04_UPDATE_AT +        " text,\n"
            + COL_05_FACEBOOK_ID +      " text,\n"
            + COL_06_USERNAME +         " text,\n"
            + COL_07_FIRST_NAME +       " text,\n"
            + COL_08_LAST_NAME +        " text,\n"
            + COL_09_MIDDLE_NAME +      " text,\n"
            + COL_10_FULL_NAME +        " text,\n"
            + COL_11_GENDER +           " text,\n"
            + COL_12_BIRTHDAY +         " text,\n"
            + COL_13_UDID +             " text,\n"
            + COL_14_ROLE +             " text,\n"
            + COL_15_AVATAR +           " text,\n"
            + COL_16_IS_FOLLOWED +      " boolean,\n"
            + COL_17_COUNT_FOLLOWER +   " integer,\n"
            + COL_18_COUNT_FOLLOWING +  " integer,\n"
            + COL_19_COUNT_POSITIVE +   " integer,\n"
            + COL_20_COUNT_NEGATIVE +   " integer,\n"
            + COL_21_COUNT_NORMAL +     " integer,\n"
            + COL_22_COUNT_ACTIVITIES + " integer,\n"
            + COL_23_PERCENT_POSITIVE + " integer,\n"
            + COL_24_PERCENT_NEGATIVE + " integer,\n"
            + COL_25_PERCENT_NORMAL +   " integer\n"
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
