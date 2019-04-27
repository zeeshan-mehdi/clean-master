package com.alim.cleanmaster.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "APPDATA2.db";
    public static final String TABLE_NAME2 = "app_table_";
    public static final String COL_1 = "ID";
    public static final String COL_2 = "APP_NAME";
    public static final String COL_2__2 = "APP_TITLE";
    public static final String COL_3 = "DESCRIPTION";
    public static final String COL_4 = "SIZE";
    public static final String COL_5 = "IS_SELECTED";
    public static final String COL_6 = "PACKAGE_NAME";

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, 2);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + TABLE_NAME2 +" (ID INTEGER PRIMARY KEY AUTOINCREMENT,APP_TITLE TEXT,DESCRIPTION TEXT,SIZE LONG,IS_SELECTED BOOLEAN,PACKAGE_NAME TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS "+TABLE_NAME2);
        onCreate(db);
    }

    public boolean insertData(String appTitle, String description, long size, boolean isSelected, String packageName) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_2__2,appTitle);
        contentValues.put(COL_3,description);
        contentValues.put(COL_4,size);
        contentValues.put(COL_5,isSelected);
        contentValues.put(COL_6,packageName);
        long result;
        result = db.insert(TABLE_NAME2,null ,contentValues);
        if(result == -1)
            return false;
        else
            return true;
    }

    public Cursor getAllData() {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res;
        res = db.rawQuery("select * from "+TABLE_NAME2,null);
        return res;
    }

    public boolean updateData(String id,String appName, String description, long size, boolean isSelected, String packageName) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_1,id);
        contentValues.put(COL_2,appName);
        contentValues.put(COL_3,description);
        contentValues.put(COL_4,size);
        contentValues.put(COL_5,isSelected);
        contentValues.put(COL_6,packageName);
        db.update(TABLE_NAME2, contentValues, "ID = ?",new String[] { id });
        return true;
    }

    public Integer deleteData (String id,int tableNO) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TABLE_NAME2, "ID = ?",new String[] {id});
    }
    public void eraseData(){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("drop table if exists "+TABLE_NAME2+';');
    }
    public void truncateTable(){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM "+TABLE_NAME2+';');
    }

}
