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

    /*for finding all the elements in the database using findAll method*/
    public static final String all [] = {
            DBOpenHelper.ID_COLUMN,
            DBOpenHelper.NAME_COLUMN,
            DBOpenHelper.NUMBER_COLUMN,
            DBOpenHelper.EMAIL_COLUMN,
            DBOpenHelper.ADDRESS_COLUMN,
            DBOpenHelper.BIRTHDAY_COLUMN,
            DBOpenHelper.RELATIONSHIP_COLUMN
    };

    /*initialize dbhelper*/
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
    }

    /*This function is called in order to create a new row*/
    public Contact create(Contact contact){

        ContentValues values = new ContentValues();

        values.put(DBOpenHelper.NAME_COLUMN, contact.getName());
        values.put(DBOpenHelper.NUMBER_COLUMN, contact.getPhoneNo());
        values.put(DBOpenHelper.EMAIL_COLUMN, contact.getEmailID());
        values.put(DBOpenHelper.ADDRESS_COLUMN, contact.getAddress());
        values.put(DBOpenHelper.BIRTHDAY_COLUMN, contact.getBirthday());
        values.put(DBOpenHelper.RELATIONSHIP_COLUMN, contact.getRelationship());

        /*ID is primary key, autoincrement. insertID now gets the value of ID Column*/
        long insertID = database.insert(DBOpenHelper.TABLE_NAME, null, values);

        contact.setId(insertID);

        //error returns -1
        return contact;
    }

    /*This function is called to update a row in a database*/
    public int update(Contact contact){

        ContentValues values = new ContentValues();

        values.put(DBOpenHelper.ID_COLUMN, contact.getId());
        values.put(DBOpenHelper.NAME_COLUMN, contact.getName());
        values.put(DBOpenHelper.NUMBER_COLUMN, contact.getPhoneNo());
        values.put(DBOpenHelper.EMAIL_COLUMN, contact.getEmailID());
        values.put(DBOpenHelper.ADDRESS_COLUMN, contact.getAddress());
        values.put(DBOpenHelper.BIRTHDAY_COLUMN, contact.getBirthday());
        values.put(DBOpenHelper.RELATIONSHIP_COLUMN, contact.getRelationship());

        int updateID = database.update(
                DBOpenHelper.TABLE_NAME,
                values,
                DBOpenHelper.ID_COLUMN + "=?",
                new String[] {""+contact.getId()});

        //returns no of columns affected
        Log.d(LOG_TAG, "Columns affected: " + updateID +
                "\nID: " + contact.getId() +
        "\nNo of ID: " + database.rawQuery("SELECT count(*) FROM ContactsList " +
                "WHERE ID = " + contact.getId(), null));

        return updateID;
    }

    /*This function finds all entries*/
    public List<Contact> findAll(){

        Cursor cursor = database.query(DBOpenHelper.TABLE_NAME, all, null, null, null, null, null);

        List<Contact> contacts = getContacts(cursor);

        return contacts;

    }

    /*This function is used to search for a pattern occurring in entries.
    * This pattern is obtained from the search string entered by user*/
    public List<Contact> search(String searchString){
        String query = "SELECT * FROM " + DBOpenHelper.TABLE_NAME +
                " WHERE " + DBOpenHelper.NAME_COLUMN + " LIKE '%" + searchString +
                "%' OR " + DBOpenHelper.NUMBER_COLUMN + " LIKE '%" + searchString + "%'";
        Cursor cursor = database.rawQuery(query, null);

        List<Contact> contacts = getContacts(cursor);

        return contacts;
    }

    /*Function deletes the row, and then returns whether it was a successful delete or not*/
    public boolean deleteContact(long ID){

        //returns number of rows affected
        return database.delete(DBOpenHelper.TABLE_NAME, DBOpenHelper.ID_COLUMN + " = " + ID, null)>0;


    }

    /*Finds a single entry */
    public Contact findSpecific(long ID){

        String query = "SELECT * FROM " + DBOpenHelper.TABLE_NAME +
                " WHERE " + DBOpenHelper.ID_COLUMN + " = " + ID;

        Cursor cursor = database.rawQuery(query, null);

        List<Contact> contacts = getContacts(cursor);

        if(cursor.getCount()==0)
        {
            return new Contact(0,null, null, null, null, null, null);
        }

        return contacts.get(0);
    }

    /*Converts a cursor containing the results of a query into
    * a list of contacts.*/
    public static List<Contact> getContacts(Cursor cursor){

        List<Contact> contacts = new ArrayList<>();

        if(cursor.getCount()>0)
            while(cursor.moveToNext())
            {
                contacts.add(new Contact(
                                cursor.getInt(cursor.getColumnIndex(DBOpenHelper.ID_COLUMN)),
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

    //Checks if the table is empty
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

    //Checks if phone number is unique
    public static boolean isNumberUnique(String number){
        String query = "SELECT * FROM " + DBOpenHelper.TABLE_NAME +
                " WHERE " + DBOpenHelper.NUMBER_COLUMN + " = " + number;

        Cursor cursor = database.rawQuery(query, null);

        if(cursor.getCount()>0)
        {
            cursor.close();
            return false;
        }

        cursor.close();
        return true;
    }

    public static boolean isNumberUniqueUpdate(String number){
        String query = "SELECT * FROM " + DBOpenHelper.TABLE_NAME +
                " WHERE " + DBOpenHelper.NUMBER_COLUMN + " = " + number;

        Cursor cursor = database.rawQuery(query, null);

        if(cursor.getCount()>1)
        {
            cursor.close();
            return false;
        }

        cursor.close();
        return true;
    }
}
