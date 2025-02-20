package com.example.myapplication;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.Calendar;

public class ContactDataSource {

    private SQLiteDatabase database;
    private ContactDBHelper dbHelper;

    public ContactDataSource (Context context) {
        dbHelper = new ContactDBHelper(context);
    }

    public void open() throws SQLException{
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        database.close();
    }

    public  boolean insertContact (Contact contact) {
        boolean didSucceed = false;
        try {
            ContentValues initialValues = new ContentValues();

            initialValues.put("contactname",contact.getContactName());
            initialValues.put("streetaddress",contact.getStreetAddress());
            initialValues.put("city",contact.getCity());
            initialValues.put("state",contact.getState());
            initialValues.put("zipcode",contact.getZipCode());
            initialValues.put("phonenumber",contact.getPhoneNumber());
            initialValues.put("cellnumber",contact.getCellNumber());
            initialValues.put("email",contact.geteMail());
            initialValues.put("birthday",String.valueOf(contact.getBirthday().getTimeInMillis()));

            didSucceed = database.insert("contact", null, initialValues) >0;
        } catch (Exception e) {

        }
        return didSucceed;
    }
    public  boolean updateContact (Contact contact) {
        boolean didSucceed = false;
        try {
            Long rowID = (long) contact.getContactID();
            ContentValues updateValues = new ContentValues();

            updateValues.put("contactname",contact.getContactName());
            updateValues.put("streetaddress",contact.getStreetAddress());
            updateValues.put("city",contact.getCity());
            updateValues.put("state",contact.getState());
            updateValues.put("zipcode",contact.getZipCode());
            updateValues.put("phonenumber",contact.getPhoneNumber());
            updateValues.put("cellnumber",contact.getCellNumber());
            updateValues.put("email",contact.geteMail());
            updateValues.put("birthday",String.valueOf(contact.getBirthday().getTimeInMillis()));

            didSucceed = database.update("contact", updateValues, "_id = "+ rowID, null)>0;
        } catch (Exception e) {

        }
        return didSucceed;
    }

    public int getLastContactID(){
        int lastId;
        try {
            String query = "Select MAX(_id) from contact";
            Cursor cursor = database.rawQuery(query,null);

            cursor.moveToFirst();
            lastId = cursor.getInt(0);
            cursor.close();
        }
        catch (Exception e){
            lastId = -1;
        }
        return lastId;
    }

    public ArrayList<String> getContactName() {
        ArrayList<String> contactNames = new ArrayList<>();

        try {
            String query = "Select contactname from contact";
            Cursor cursor = database.rawQuery(query, null);

            cursor.moveToFirst();
            while (!cursor.isAfterLast()){
                contactNames.add(cursor.getString(0));
                cursor.moveToNext();
            }
            cursor.close();

        } catch (Exception e) {
            contactNames = new ArrayList<String>();
        } return contactNames;
    }

    public ArrayList<Contact> getContacts(String sortField, String sortOrder) {
        ArrayList<Contact> contacts = new ArrayList<Contact>();

        try {
            String query = "Select * from contact Order by " + sortField + " " + sortOrder;
            Cursor cursor = database.rawQuery(query, null);

            Contact newContact;
            cursor.moveToFirst();
            while (!cursor.isAfterLast()){
                newContact = new Contact();
                newContact.setContactID(cursor.getInt(0));
                newContact.setContactName(cursor.getString(1));
                newContact.setStreetAddress(cursor.getString(2));
                newContact.setCity(cursor.getString(3));
                newContact.setState(cursor.getString(4));
                newContact.setZipCode(cursor.getString(5));
                newContact.setPhoneNumber(cursor.getString(6));
                newContact.setCellNumber(cursor.getString(7));
                newContact.seteMail(cursor.getString(8));

                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(Long.valueOf(cursor.getString(9)));
                newContact.setBirthday(calendar);


                Log.d("DATABASE", "Loaded Contact: " + newContact.getContactName() +
                        ", Phone: " + newContact.getPhoneNumber() +
                        ", Cell: " + newContact.getCellNumber() +
                        ", Email: " + newContact.geteMail());

                contacts.add(newContact);
                cursor.moveToNext();
            }
            cursor.close();

        } catch (Exception e) {
            contacts = new ArrayList<Contact>();
        } return contacts;
    }

    public Contact getSpecificContact(int contactId){
        Contact contact = new Contact();
        String query = "Select * from contact WHERE _id =" + contactId;

        Cursor cursor = database.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            contact.setContactID(cursor.getInt(0));
            contact.setContactName(cursor.getString(1));
            contact.setStreetAddress(cursor.getString(2));
            contact.setCity(cursor.getString(3));
            contact.setState(cursor.getString(4));
            contact.setZipCode(cursor.getString(5));
            contact.setPhoneNumber(cursor.getString(6));
            contact.setCellNumber(cursor.getString(7));
            contact.seteMail(cursor.getString(8));
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(Long.valueOf(cursor.getString(9)));
            contact.setBirthday(calendar);

            cursor.close();

        }
        return contact;
    }

    public boolean deletecontact(int contactId){
        boolean didDelete = false;
        try {
            didDelete = database.delete("contact", "_id=" + contactId, null)>0;
        }
        catch (Exception e){

        }
        return didDelete;
    }


}