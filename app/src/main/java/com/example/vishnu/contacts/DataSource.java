package com.example.vishnu.contacts;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by vishnu on 28/6/16.
 */
public class DataSource {

    private static final String LOG_TAG = "DataSource";

    SQLiteOpenHelper dbHelper;
    static SQLiteDatabase database;

    public static final String all [] = {
            DBOpenHelper.NAME_COLUMN,
            DBOpenHelper.NUMBER_COLUMN,
            DBOpenHelper.EMAIL_COLUMN,
            DBOpenHelper.ADDRESS_COLUMN,
            DBOpenHelper.BIRTHDAY_COLUMN,
            DBOpenHelper.RELATIONSHIP_COLUMN
    };

    public DataSource(Context context)
    {
        dbHelper = DBOpenHelper.getInstance(context);
    }

    public void open(){
        Log.d(LOG_TAG, "Database opened");
        database = dbHelper.getWritableDatabase();
    }

    public void close(){
        Log.d(LOG_TAG, "Database closed");
        //dbHelper.close();
    }

    public long create(Contact contact){

        ContentValues values = new ContentValues();

        values.put(DBOpenHelper.NAME_COLUMN, contact.getName());
        values.put(DBOpenHelper.NUMBER_COLUMN, contact.getPhoneNo());
        values.put(DBOpenHelper.EMAIL_COLUMN, contact.getEmailID());
        values.put(DBOpenHelper.ADDRESS_COLUMN, contact.getAddress());
        values.put(DBOpenHelper.BIRTHDAY_COLUMN, contact.getBirthday());
        values.put(DBOpenHelper.RELATIONSHIP_COLUMN, contact.getRelationship());

        long insertID = database.insert(DBOpenHelper.TABLE_NAME, null, values);

        //error returns -1
        return insertID;
    }

    public int update(Contact contact){

        ContentValues values = new ContentValues();

        values.put(DBOpenHelper.NAME_COLUMN, contact.getName());
        values.put(DBOpenHelper.NUMBER_COLUMN, contact.getPhoneNo());
        values.put(DBOpenHelper.EMAIL_COLUMN, contact.getEmailID());
        values.put(DBOpenHelper.ADDRESS_COLUMN, contact.getAddress());
        values.put(DBOpenHelper.BIRTHDAY_COLUMN, contact.getBirthday());
        values.put(DBOpenHelper.RELATIONSHIP_COLUMN, contact.getRelationship());

        int updateID = database.update(
                DBOpenHelper.TABLE_NAME,
                values,
                DBOpenHelper.NUMBER_COLUMN + " = " + contact.getPhoneNo(),
                null);

        return updateID;
    }

    public List<Contact> findAll(){

        Cursor cursor = database.query(DBOpenHelper.TABLE_NAME, all, null, null, null, null, null);

        List<Contact> contacts = getContacts(cursor);

        return contacts;

    }

    public List<Contact> search(String searchString){
        String query = "SELECT * FROM " + DBOpenHelper.TABLE_NAME +
                " WHERE " + DBOpenHelper.NAME_COLUMN + " LIKE '%" + searchString +
                "%' OR " + DBOpenHelper.NUMBER_COLUMN + " LIKE '%" + searchString + "%'";
        Cursor cursor = database.rawQuery(query, null);

        List<Contact> contacts = getContacts(cursor);

        return contacts;
    }

    public boolean deleteContact(String phoneNo){

        database.delete(DBOpenHelper.TABLE_NAME, DBOpenHelper.NUMBER_COLUMN + " = " + phoneNo, null);

        boolean flag = isNumberUnique(phoneNo);

        Log.d(LOG_TAG, "delete " + flag);

        return flag;
    }

    public Contact findSpecific(String phoneNo){

        String query = "SELECT * FROM " + DBOpenHelper.TABLE_NAME +
                " WHERE " + DBOpenHelper.NUMBER_COLUMN + " = " +phoneNo;

        Cursor cursor = database.rawQuery(query, null);

        List<Contact> contacts = getContacts(cursor);

        return contacts.get(0);
    }

    public static List<Contact> getContacts(Cursor cursor){

        List<Contact> contacts = new ArrayList<>();

        if(cursor.getCount()>0)
            while(cursor.moveToNext())
            {
                contacts.add(new Contact(
                                cursor.getString(cursor.getColumnIndex(DBOpenHelper.NAME_COLUMN)),
                                cursor.getString(cursor.getColumnIndex(DBOpenHelper.NUMBER_COLUMN)),
                                cursor.getString(cursor.getColumnIndex(DBOpenHelper.EMAIL_COLUMN)),
                                cursor.getString(cursor.getColumnIndex(DBOpenHelper.ADDRESS_COLUMN)),
                                cursor.getString(cursor.getColumnIndex(DBOpenHelper.BIRTHDAY_COLUMN)),
                                cursor.getString(cursor.getColumnIndex(DBOpenHelper.RELATIONSHIP_COLUMN))
                        )
                );
            }

        return contacts;
    }

    public boolean isEmpty(){

        String count = "SELECT count(*) FROM " + DBOpenHelper.TABLE_NAME;

        Cursor cursor = database.rawQuery(count, null);
        cursor.moveToFirst();

        int c = cursor.getInt(0);

        if(c>0)
            return false;

        else
            return true;

    }

    public static boolean isNumberUnique(String number){
        String query = "SELECT * FROM " + DBOpenHelper.TABLE_NAME +
                " WHERE " + DBOpenHelper.NUMBER_COLUMN + " = " + number;

        Cursor cursor = database.rawQuery(query, null);

        if(cursor.getCount()>0)
        {
            Log.d(LOG_TAG, "" + getContacts(cursor).get(0).toString());
            cursor.close();
            return false;
        }

        cursor.close();
        return true;
    }
}
