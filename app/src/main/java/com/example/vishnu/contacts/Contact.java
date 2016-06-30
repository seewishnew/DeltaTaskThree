package com.example.vishnu.contacts;

import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by vishnu on 28/6/16.
 */
public class Contact {

    public static final String LOG_TAG = "Contact";


    private String name = null;
    private String phoneNo = null;
    private String emailID = null;
    private String address = null;
    private String birthday = null;
    private String relationship = null;
    private long id;
    private Uri uri = null;

    public Contact(){}

    public Contact(String name, String phoneNo) {
        this.name = name;
        this.phoneNo = phoneNo;
    }

    public Contact(String name, String phoneNo, String emailID) {
        this.name = name;
        this.phoneNo = phoneNo;
        this.emailID = emailID;
    }

    public Contact(long id, String name, String phoneNo, String emailID,
                   String address, String birthday, String relationship){
        this.id = id;
        this.name = name;
        this.phoneNo = phoneNo;
        this.emailID = emailID;
        this.address = address;
        this.birthday = birthday;
        this.relationship = relationship;
    }

    public Contact(long id, String name, String phoneNo, String emailID,
                   String address, String birthday, String relationship, String uri){
        this.id = id;
        this.name = name;
        this.phoneNo = phoneNo;
        this.emailID = emailID;
        this.address = address;
        this.birthday = birthday;
        this.relationship = relationship;

        if(uri==null)
            this.uri=null;
        else
            this.uri = Uri.parse(uri);
    }

    public boolean hasPhoneNo(){
        return (phoneNo==null || phoneNo==""||phoneNo.length()==0)?false:true;
    }

    public boolean hasEmailID(){
        return (emailID==null || emailID == "" || emailID.length()==0)?false:true;
    }

    public boolean hasAddress(){
        return (address==null || address=="" ||address.length()==0)?false:true;
    }

    public boolean hasBirthday(){
        return  (birthday==null || birthday == ""||birthday.length()==0)?false:true;
    }

    public boolean hasRelationship(){
        return (relationship==null || relationship=="" ||relationship.length()==0)?false:true;
    }

    public boolean hasUri(){
        return (uri==null)?false:true;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhoneNo() {
        if(phoneNo == (""))
            phoneNo=null;

        return phoneNo;
    }

    public void setPhoneNo(String phoneNo) {
        this.phoneNo = phoneNo;
    }

    public String getEmailID() {

        if(emailID == (""))
            emailID=null;

        return emailID;
    }

    public void setEmailID(String emailID) {
        this.emailID = emailID;
    }

    public String getAddress() {

        if(address == (""))
            address=null;

        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getBirthday() {
        if(birthday == "")
            birthday=null;

        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public String getRelationship() {
        return relationship;
    }

    public void setRelationship(String relationship) {
        this.relationship = relationship;
    }


    @Override
    public String toString() {
        return (name + "\n" + phoneNo);
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setUri(String uri){
        if(uri==null)
            this.uri=null;
        else
            this.uri=Uri.parse(uri);
    }

    public String getUri(){

        return hasUri()?uri.toString():null;
    }
}
