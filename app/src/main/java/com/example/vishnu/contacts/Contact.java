package com.example.vishnu.contacts;

import android.database.Cursor;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by vishnu on 28/6/16.
 */
public class Contact {

    private String name;
    private String phoneNo = null;
    private String emailID = null;
    private String address = null;
    private String birthday = null;
    private String relationship = null;

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

    public Contact(String name, String phoneNo, String emailID,
                   String address, String birthday, String relationship){
        this.name = name;
        this.phoneNo = phoneNo;
        this.emailID = emailID;
        this.address = address;
        this.birthday = birthday;
        this.relationship = relationship;
    }

    public boolean hasPhoneNo(){
        return (phoneNo==null)?false:true;
    }

    public boolean hasEmailID(){
        return (emailID==null)?false:true;
    }

    public boolean hasAddress(){
        return (address==null)?false:true;
    }

    public boolean hasBirthday(){
        return  (birthday==null)?false:true;
    }

    public boolean hasRelationship(){
        return (relationship==null)?false:true;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhoneNo() {
        return phoneNo;
    }

    public void setPhoneNo(String phoneNo) {
        this.phoneNo = phoneNo;
    }

    public String getEmailID() {
        return emailID;
    }

    public void setEmailID(String emailID) {
        this.emailID = emailID;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getBirthday() {
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
}
