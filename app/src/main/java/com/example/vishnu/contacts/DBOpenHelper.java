package com.example.vishnu.contacts;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by vishnu on 28/6/16.
 */
public class DBOpenHelper extends SQLiteOpenHelper{

    private static final String LOG_TAG = "DBOpenHelper";

    private static DBOpenHelper instance;

    public static final int VERSION = 3;
    public static final String DATABASE_NAME = "contact_2.db";

    public static final String TABLE_NAME = "ContactsList";
    public static final String NAME_COLUMN = "Name";
    public static final String NUMBER_COLUMN = "PhoneNo";
    public static final String EMAIL_COLUMN = "emailID";
    public static final String ADDRESS_COLUMN = "Address";
    public static final String BIRTHDAY_COLUMN = "Birthday";
    public static final String RELATIONSHIP_COLUMN = "Relationship";


    public static final String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + " ( " +
            NUMBER_COLUMN + " TEXT PRIMARY KEY, " +
            NAME_COLUMN + " TEXT, " +
            EMAIL_COLUMN + " TEXT, " +
            ADDRESS_COLUMN + " TEXT, " +
            BIRTHDAY_COLUMN + " TEXT, " +
            RELATIONSHIP_COLUMN + " TEXT " + ")";


    private DBOpenHelper(Context context){
        super(context, DATABASE_NAME, null, VERSION);
    }

    public static synchronized DBOpenHelper getInstance(Context context){
        if(instance==null)
        {
            instance = new DBOpenHelper(context.getApplicationContext());
        }

        return instance;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE);
        Log.d(LOG_TAG, "Table Created");
    }

    public void drop(SQLiteDatabase db){
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        drop(db);
        onCreate(db);
    }
}
