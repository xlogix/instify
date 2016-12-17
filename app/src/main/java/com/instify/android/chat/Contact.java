package com.instify.android.chat;

/**
 * Created by Abhish3k on 09/02/16.
 */

public class Contact {
    int _id;
    String _name;
    String _phone_number;

    // Empty constructor
    public Contact() {

    }

    // constructor
    public Contact(int id, String name, String _phone_number) {
        this._id = id;
        this._name = name;
        this._phone_number = _phone_number;
    }

    // constructor
    public Contact(String name, String _phone_number) {
        this._name = name;
        this._phone_number = _phone_number;
    }

    /* Separate functions to get and put objects in an object. */
    // getting ID
    public int getID() {
        return this._id;
    }

    // setting id
    public void setID(int id) {
        this._id = id;
    }

    // getting name
    public String getName() {
        return this._name;
    }

    // setting name
    public void setName(String name) {
        this._name = name;
    }

    // getting phone number
    public String getPhoneNumber() {
        return this._phone_number;
    }

    // setting phone number
    public void setPhoneNumber(String phone_number) {
        this._phone_number = phone_number;
    }
}
