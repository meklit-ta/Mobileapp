package com.example.myapplication;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class ContactDataSource {
    private SQLiteDatabase database;
    private ContactDBHelper dbHelper;

    public ContactDataSource(Context context) {
        dbHelper = new ContactDBHelper(context);
    }

    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    public boolean insertContact(Contact c) {
        boolean didSucceed = false;
        try {
            ContentValues initialValues = new ContentValues();

            initialValues.put("contactname", c.getContactName());
            initialValues.put("streetaddress", c.getStreetAddress());
            initialValues.put("city", c.getCity());
            initialValues.put("state", c.getState());
            initialValues.put("zipcode", c.getZipCode());
            initialValues.put("phonenumber", c.getPhoneNumber());
            initialValues.put("cellnumber", c.getCellNumber());
            initialValues.put("email", c.geteMail());
            initialValues.put("birthday", String.valueOf(c.getBirthday().getTimeInMillis()));

            didSucceed = database.insert("contact", null, initialValues) > 0;

            if (didSucceed) {
                Log.d("DB_INSERT", "Contact saved successfully: " + c.getContactName());
            } else {
                Log.e("DB_INSERT", "Failed to save contact.");
            }
        } catch (Exception e) {
            Log.e("DB_ERROR", "Error inserting contact: " + e.getMessage());
        }
        return didSucceed;
    }

    public boolean updateContact(Contact c) {
        boolean didSucceed = false;
        try {
            Long rowId = (long) c.getContactID();
            ContentValues updateValues = new ContentValues();

            updateValues.put("contactname", c.getContactName());
            updateValues.put("streetaddress", c.getStreetAddress());
            updateValues.put("city", c.getCity());
            updateValues.put("state", c.getState());
            updateValues.put("zipcode", c.getZipCode());
            updateValues.put("phonenumber", c.getPhoneNumber());
            updateValues.put("cellnumber", c.getCellNumber());
            updateValues.put("email", c.geteMail());
            updateValues.put("birthday", String.valueOf(c.getBirthday().getTimeInMillis()));

            didSucceed = database.update("contact", updateValues, "_id=?", new String[]{String.valueOf(rowId)}) > 0;

            if (didSucceed) {
                Log.d("DB_UPDATE", "Contact updated successfully: " + c.getContactName());
            } else {
                Log.e("DB_UPDATE", "Failed to update contact.");
            }
        } catch (Exception e) {
            Log.e("DB_ERROR", "Error updating contact: " + e.getMessage());
        }
        return didSucceed;
    }

    public int getLastContactId() {
        int lastId = -1;
        try {
            String query = "SELECT MAX(_id) FROM contact";
            Cursor cursor = database.rawQuery(query, null);

            if (cursor != null && cursor.moveToFirst()) { // ✅ Ensure data is retrieved
                lastId = cursor.getInt(0);
            }

            cursor.close();
        } catch (Exception e) {
            Log.e("DB_ERROR", "Error retrieving last contact ID: " + e.getMessage());
        }
        return lastId;
    }

    public List<Contact> getAllContacts() {
        List<Contact> contactList = new ArrayList<>();
        try {
            String query = "SELECT * FROM contact ORDER BY contactname ASC";
            Cursor cursor = database.rawQuery(query, null);

            if (cursor != null && cursor.moveToFirst()) {
                do {
                    Contact contact = new Contact();
                    contact.setContactID(cursor.getInt(0));
                    contact.setContactName(cursor.getString(1));
                    contact.setStreetAddress(cursor.getString(2));
                    contact.setCity(cursor.getString(3));
                    contact.setState(cursor.getString(4));
                    contact.setZipCode(cursor.getString(5));
                    contact.setPhoneNumber(cursor.getString(6));
                    contact.setCelllNumber(cursor.getString(7));
                    contact.seteMail(cursor.getString(8));

                    Calendar calendar = Calendar.getInstance();
                    calendar.setTimeInMillis(Long.parseLong(cursor.getString(9)));
                    contact.setBirthday(calendar);

                    contactList.add(contact);
                } while (cursor.moveToNext());
            }

            cursor.close();
            Log.d("DB_FETCH", "Contacts retrieved successfully: " + contactList.size());
        } catch (Exception e) {
            Log.e("DB_ERROR", "Error retrieving contacts: " + e.getMessage());
        }

        return contactList;
    }
}
