package com.example.myapplication;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.ToggleButton;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import java.sql.SQLException;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity {
    private Contact currentContact;
    private ContactDataSource ds;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        ds = new ContactDataSource(this);
        currentContact = new Contact();

        initSaveButton();
        initChangeButton();
        setForEditing(false);

        // Check if an existing contact is being edited
        int contactID = getIntent().getIntExtra("contactID", -1);
        if (contactID != -1) {
            loadContact(contactID);
        }
    }

    private void setForEditing(boolean enabled) {
        EditText editName = findViewById(R.id.editName);
        EditText editPhone = findViewById(R.id.editCell); // Ensure editPhone exists in XML
        Button buttonChange = findViewById(R.id.btnBirthday);
        Button buttonSave = findViewById(R.id.buttonSave);

        editName.setEnabled(enabled);
        editPhone.setEnabled(enabled);
        buttonChange.setEnabled(enabled);
        buttonSave.setEnabled(enabled);

        if (!enabled) {
            ScrollView s = findViewById(R.id.scrollView);
            s.fullScroll(ScrollView.FOCUS_UP);
        }
    }

    private void loadContact(int contactID) {
        try {
            ds.open();
            currentContact = ds.getContactById(contactID); // Ensure this method exists in ContactDataSource
            ds.close();

            EditText editName = findViewById(R.id.editName);
            EditText editPhone = findViewById(R.id.editCell);
            TextView birthdayText = findViewById(R.id.textDate);

            editName.setText(currentContact.getContactName());
            editPhone.setText(currentContact.getPhoneNumber());

            Calendar birthday = currentContact.getBirthday();
            if (birthday != null) {
                birthdayText.setText(String.format("%02d/%02d/%04d",
                        birthday.get(Calendar.MONTH) + 1,
                        birthday.get(Calendar.DAY_OF_MONTH),
                        birthday.get(Calendar.YEAR)));
            }
        } catch (Exception e) {
            Log.e("DB_ERROR", "Error loading contact: " + e.getMessage());
        }
    }

    private void initChangeButton() {
        Button changeDate = findViewById(R.id.btnBirthday);
        changeDate.setOnClickListener(v -> {
            final Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePickerDialog = new DatePickerDialog(MainActivity.this,
                    (view, selectedYear, selectedMonth, selectedDay) -> {
                        TextView birthday = findViewById(R.id.textDate);
                        birthday.setText(String.format("%02d/%02d/%04d",
                                selectedMonth + 1, selectedDay, selectedYear));

                        Calendar selectedTime = Calendar.getInstance();
                        selectedTime.set(selectedYear, selectedMonth, selectedDay);
                        currentContact.setBirthday(selectedTime);
                    }, year, month, day);
            datePickerDialog.show();
        });
    }

    private void initSaveButton() {
        Button saveButton = findViewById(R.id.buttonSave);
        saveButton.setOnClickListener(v -> {
            try {
                ds.open();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }

            String name = ((EditText) findViewById(R.id.editName)).getText().toString().trim();
            String phone = ((EditText) findViewById(R.id.editCell)).getText().toString().trim();

            if (name.isEmpty()) {
                Log.e("DB_ERROR", "Contact name is empty. Cannot save.");
                return;
            }

            currentContact.setContactName(name);
            currentContact.setPhoneNumber(phone);

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
                setForEditing(false);
                setResult(RESULT_OK); // Notify ContactListActivity to refresh
                finish();
            }
        });
    }
}
