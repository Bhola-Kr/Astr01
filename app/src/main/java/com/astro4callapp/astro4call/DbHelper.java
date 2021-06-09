package com.astro4callapp.astro4call;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


public class DbHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME="student.db";
    private static final String TABLE_NAME="student";
    private static final String COL_1="ID";
    private static final String COL_2="NAME";
    private static final String COL_3="LANG";
    private static final String COL_4="DEVICEID";
    private static final String COL_5="ADDRESS";
    private static final String COL_6="DATE";


    public DbHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);
    }



    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " +TABLE_NAME+" "+"(ID INTEGER PRIMARY KEY AUTOINCREMENT,NAME TEXT,LANG TEXT,DEVICEID TEXT,ADDRESS TEXT,DATE TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS "+TABLE_NAME);
    }

    public boolean isInsert(String date,String hours,String minute,String second) {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_2,date);
        contentValues.put(COL_4,hours);
        contentValues.put(COL_5,minute);
        contentValues.put(COL_6,second);

        long res = sqLiteDatabase.insert(TABLE_NAME, null, contentValues);
        //retutn -1 when error or an exception generate
        if (res == -1) {
            return false;
        } else {
            return true;
        }
    }

    public Cursor readData(){
        SQLiteDatabase database=this.getReadableDatabase();
        Cursor result= database.rawQuery("select * from "+ TABLE_NAME,null);
        return result;
        //Cusor it is a class which is used for randomly read and write in data in android sqlite database
    }
}