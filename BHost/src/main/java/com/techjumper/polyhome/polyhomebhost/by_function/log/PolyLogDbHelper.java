package com.techjumper.polyhome.polyhomebhost.by_function.log;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.techjumper.polyhome.polyhomebhost.entity.sql.PolyLog;

/**
 * * * * * * * * * * * * * * * * * * * * * * *
 * Created by zhaoyiding
 * Date: 16/7/22
 * * * * * * * * * * * * * * * * * * * * * * *
 **/
public class PolyLogDbHelper extends SQLiteOpenHelper {

    public static final String DB_NAME = "poly_log";
    public static final int VERSION = 1;

    public static PolyLogDbHelper create(Context context) {
        return new PolyLogDbHelper(context);
    }

    private PolyLogDbHelper(Context context) {
        super(context, DB_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(PolyLog.CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
