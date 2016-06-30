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

    /*In order to make sure that there are never two instances
    * of this class, only the reference of this instance is handed
    * to all activities. This instance is initialized only if it never
    * has been.*/
    private static DBOpenHelper instance;

    public static final int VERSION = 1;
    public static final String DATABASE_NAME = "contact.db";

    public static final String TABLE_NAME = "ContactsList";
    public static final String ID_COLUMN = "ID";
    public static final String NAME_COLUMN = "Name";
    public static final String NUMBER_COLUMN = "PhoneNo";
    public static final String EMAIL_COLUMN = "emailID";
    public static final String ADDRESS_COLUMN = "Address";
    public static final String BIRTHDAY_COLUMN = "Birthday";
    public static final String RELATIONSHIP_COLUMN = "Relationship";


    public static final String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + " ( " +
            ID_COLUMN + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            NUMBER_COLUMN + " TEXT, " +
            NAME_COLUMN + " TEXT, " +
            EMAIL_COLUMN + " TEXT, " +
            ADDRESS_COLUMN + " TEXT, " +
            BIRTHDAY_COLUMN + " TEXT, " +
            RELATIONSHIP_COLUMN + " TEXT " + ")";

    /*This is private because it is dangerous to use outside
    * It is called only by getInstance when instance has not been
    * initialized*/
    private DBOpenHelper(Context context){
        super(context, DATABASE_NAME, null, VERSION);
    }

    /*This is the method that is used outside, in order to get a handle
     * of instance. */
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
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
//        super.onDowngrade(db, oldVersion, newVersion);
        drop(db);
        onCreate(db);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        drop(db);
        onCreate(db);
    }
}
