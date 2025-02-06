package com.example.myapplication;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.PhoneNumberFormattingTextWatcher;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.ToggleButton;
import android.text.format.DateFormat;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.Calendar;

public class MainActivity extends AppCompatActivity {
    private Contact currentContact;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        initListButton();
        initMapButton();
        initSettingsButton();
        initToggleButton();
        setForEditing(false);
        initChangeButton();  // Updated to use DatePickerDialog directly
        initSaveButton();
        currentContact = new Contact();
    }

    private void initListButton(){
        ImageButton ibList = findViewById(R.id.imageButtonList);
        ibList.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, ContactListActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        });
    }

    private void initMapButton(){
        ImageButton ibMap = findViewById(R.id.imageButtonMap);
        ibMap.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, ContactMapsActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        });
    }

    private void initSettingsButton(){
        ImageButton ibSettings = findViewById(R.id.imageButtonSettings);
        ibSettings.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, ContactSettingsActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        });
    }

    private void initToggleButton(){
        final ToggleButton editToggle = findViewById(R.id.toggleButtonEdit);
        editToggle.setOnClickListener(v -> setForEditing(editToggle.isChecked()));
    }

    private void setForEditing(boolean enabled) {
        EditText editName = findViewById(R.id.editName);
        EditText editAddress = findViewById(R.id.editAddress);
        EditText editCity = findViewById(R.id.editCity);
        EditText editState = findViewById(R.id.editState);
        EditText editZipCode = findViewById(R.id.editZipcode);
        EditText editPhone = findViewById(R.id.editHome);
        EditText editCell = findViewById(R.id.editCell);
        EditText editEmail = findViewById(R.id.editEmail);
        Button buttonChange = findViewById(R.id.btnBirthday);
        Button buttonSave = findViewById(R.id.buttonSave);

        editName.setEnabled(enabled);
        editAddress.setEnabled(enabled);
        editCity.setEnabled(enabled);
        editState.setEnabled(enabled);
        editZipCode.setEnabled(enabled);
        editPhone.setEnabled(enabled);
        editCell.setEnabled(enabled);
        editEmail.setEnabled(enabled);
        buttonChange.setEnabled(enabled);
        buttonSave.setEnabled(enabled);

        if(enabled){
            editName.requestFocus();
        } else {
            ScrollView s = findViewById(R.id.scrollView);
            s.fullScroll(ScrollView.FOCUS_UP);
        }
    }

    public void initChangeButton() {
        Button changeDate = findViewById(R.id.btnBirthday);
        changeDate.setOnClickListener(v -> {
            final Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePickerDialog = new DatePickerDialog(MainActivity.this,
                    (view, selectedYear, selectedMonth, selectedDay) -> {
                        TextView birthday = findViewById(R.id.textDate);
                        String formattedDate = String.format("%02d/%02d/%04d", selectedMonth + 1, selectedDay, selectedYear);
                        birthday.setText(formattedDate);

                        Calendar selectedTime = Calendar.getInstance();
                        selectedTime.set(selectedYear, selectedMonth, selectedDay);
                        currentContact.setBirthday(selectedTime);
                    }, year, month, day);

            datePickerDialog.show();
        });
    }
/*
    private void initSaveButton(){
        Button saveButton = findViewById(R.id.buttonSave);
        saveButton.setOnClickListener(v -> {
            boolean wasSuccessful;
            ContactDataSource ds = new ContactDataSource(MainActivity.this);
            try {
                ds.open();
                if(currentContact.getContactID() == -1) {
                    wasSuccessful = ds.insertContact(currentContact);
                } else {
                    wasSuccessful = ds.updateContact(currentContact);
                }
                if (wasSuccessful){
                    int newId = ds.getLastContactId();
                    currentContact.setContactID(newId);
                }
                ds.close();
            } catch (Exception e){
                wasSuccessful = false;
            }

            if (wasSuccessful){
                ToggleButton editToggle = findViewById(R.id.toggleButtonEdit);
                editToggle.toggle();
                setForEditing(false);
            }
        });
    }*/
private void initSaveButton() {
    Button saveButton = findViewById(R.id.buttonSave);
    saveButton.setOnClickListener(v -> {
        ContactDataSource ds = new ContactDataSource(MainActivity.this);
        try {
            ds.open();

            // Read user input
            String name = ((EditText) findViewById(R.id.editName)).getText().toString().trim();
            String street = ((EditText) findViewById(R.id.editAddress)).getText().toString().trim();
            String city = ((EditText) findViewById(R.id.editCity)).getText().toString().trim();
            String state = ((EditText) findViewById(R.id.editState)).getText().toString().trim();
            String zip = ((EditText) findViewById(R.id.editZipcode)).getText().toString().trim();
            String phone = ((EditText) findViewById(R.id.editHome)).getText().toString().trim();
            String cell = ((EditText) findViewById(R.id.editCell)).getText().toString().trim();
            String email = ((EditText) findViewById(R.id.editEmail)).getText().toString().trim();

            // Validation: Prevent inserting if name is empty
            if (name.isEmpty()) {
                Log.e("DB_ERROR", "Contact name is empty. Cannot save.");
                return;
            }

            // Assign values to currentContact
            currentContact.setContactName(name);
            currentContact.setStreetAddress(street);
            currentContact.setCity(city);
            currentContact.setState(state);
            currentContact.setZipCode(zip);
            currentContact.setPhoneNumber(phone);
            currentContact.setCelllNumber(cell);
            currentContact.seteMail(email);

            // Set birthday
            TextView birthdayText = findViewById(R.id.textDate);
            String birthdayString = birthdayText.getText().toString();
            Calendar calendar = Calendar.getInstance();

            if (!birthdayString.equals("01/01/1970")) {  // Prevent default date from being saved
                String[] dateParts = birthdayString.split("/");
                int month = Integer.parseInt(dateParts[0]) - 1; // Months are 0-based
                int day = Integer.parseInt(dateParts[1]);
                int year = Integer.parseInt(dateParts[2]);
                calendar.set(year, month, day);
                currentContact.setBirthday(calendar);
            } else {
                currentContact.setBirthday(null);
            }

            // Insert or Update Contact
            boolean wasSuccessful;
            if (currentContact.getContactID() == -1) {
                wasSuccessful = ds.insertContact(currentContact);
                if (wasSuccessful) {
                    int newId = ds.getLastContactId();
                    currentContact.setContactID(newId);
                }
            } else {
                wasSuccessful = ds.updateContact(currentContact);
            }

            ds.close();

            if (wasSuccessful) {
                Log.d("DB_SAVE", "Contact saved successfully!");
                ToggleButton editToggle = findViewById(R.id.toggleButtonEdit);
                editToggle.toggle();
                setForEditing(false);
            } else {
                Log.e("DB_SAVE", "Failed to save contact.");
            }
        } catch (Exception e) {
            Log.e("DB_ERROR", "Error saving contact: " + e.getMessage());
        }
    });
}

}
