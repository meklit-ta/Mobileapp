package com.example.myapplication;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import java.util.Calendar;

public class MainActivity extends AppCompatActivity {

    private ToggleButton toggleEdit;
    private Button btnSave, buttonBirthday;
    private TextView textBirthday;
    private EditText editContact, editAddress, editCity, editState, editZipcode, editHome, editCell, editEmail;
    private ImageButton btnContacts, btnMap, btnSettings;
    private LinearLayout toolbar, bottomNavigationBar;
    private Contact currentContact;
    private ContactDataSource dataSource;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        dataSource = new ContactDataSource(this);

        initViews();
        initTextChangedEvents();

        // Get contact ID from Intent
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            int contactId = extras.getInt("contactId",-1);
            if (contactId != -1) {
                initContact(contactId);
            } else {
                currentContact = new Contact();
            }
        } else {
            currentContact = new Contact();
        }

        // Initialize Toggle Button behavior
        initToggleButton();
        setForEditing(false);
    }

    private void initContact(int id) {
        dataSource = new ContactDataSource(MainActivity.this);

        try {
            dataSource.open();
            currentContact = dataSource.getSpecificContact(id);
            dataSource.close();
        } catch (Exception e) {
            Toast.makeText(this, "Load Contact Failed", Toast.LENGTH_LONG).show();
            return;
        }

        if (currentContact == null) {
            Toast.makeText(this, "No contact found with ID: " + id, Toast.LENGTH_LONG).show();
            return;
        }

        editContact.setText(currentContact.getContactName());
        editAddress.setText(currentContact.getStreetAddress());
        editCity.setText(currentContact.getCity());
        editState.setText(currentContact.getState());
        editZipcode.setText(currentContact.getZipCode());
        editHome.setText(currentContact.getPhoneNumber());
        editCell.setText(currentContact.getCellNumber());
        editEmail.setText(currentContact.geteMail());

        if (currentContact.getBirthday() != null) {
            textBirthday.setText(DateFormat.format("MM/dd/yyyy", currentContact.getBirthday().getTime()));
        } else {
            textBirthday.setText("No Birthday");
        }
    }

    private void initToggleButton() {
        toggleEdit.setOnClickListener(v -> setForEditing(toggleEdit.isChecked()));
    }

    private void initViews() {
        toolbar = findViewById(R.id.toolbar);
        bottomNavigationBar = findViewById(R.id.bottomNavigationBar);

        toggleEdit = findViewById(R.id.toggleEdit);
        btnSave = findViewById(R.id.btnSave);
        buttonBirthday = findViewById(R.id.buttonBirthday);
        textBirthday = findViewById(R.id.textBirthday);

        editContact = findViewById(R.id.editContact);
        editAddress = findViewById(R.id.editAddress);
        editCity = findViewById(R.id.editCity);
        editState = findViewById(R.id.editState);
        editZipcode = findViewById(R.id.editZipcode);
        editHome = findViewById(R.id.editHome);
        editCell = findViewById(R.id.editCell);
        editEmail = findViewById(R.id.editEmail);

        btnContacts = findViewById(R.id.btnContacts);
        btnMap = findViewById(R.id.btnMap);
        btnSettings = findViewById(R.id.btnSettings);

        btnSave.setOnClickListener(v -> initSaveButton());
        buttonBirthday.setOnClickListener(v -> initChangeDateButton());
        btnContacts.setOnClickListener(v -> openContacts());
        btnMap.setOnClickListener(v -> openMap());
        btnSettings.setOnClickListener(v -> openSettings());
    }

    private void initSaveButton() {
    }


    private void openContacts() {
        Intent intent = new Intent(MainActivity.this, ContactListActivity.class);
        startActivity(intent);
    }

    private void openMap() {
        Intent intent = new Intent(MainActivity.this, ContactMapsActivity.class);
        startActivity(intent);
    }

    private void openSettings() {
        Intent intent = new Intent(MainActivity.this, ContactSettingsActivity.class);
        startActivity(intent);
    }

    private void initChangeDateButton() {
        buttonBirthday.setOnClickListener(v -> showDatePickerDialog());
    }

    private void showDatePickerDialog() {
        Calendar calendar = Calendar.getInstance();
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                MainActivity.this,
                (view, year, month, dayOfMonth) -> {
                    Calendar selectedDate = Calendar.getInstance();
                    selectedDate.set(year, month, dayOfMonth);
                    textBirthday.setText(DateFormat.format("MM/dd/yyyy", selectedDate));
                    currentContact.setBirthday(selectedDate);
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        );
        datePickerDialog.show();
    }

    private void initTextChangedEvents() {
        editContact.addTextChangedListener(new SimpleTextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                currentContact.setContactName(s.toString());
            }
        });

        editAddress.addTextChangedListener(new SimpleTextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                currentContact.setStreetAddress(s.toString());
            }
        });

        editEmail.addTextChangedListener(new SimpleTextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                currentContact.seteMail(s.toString());
            }
        });
    }

    private void setForEditing(boolean enabled) {
        editContact.setEnabled(enabled);
        editAddress.setEnabled(enabled);
        editCity.setEnabled(enabled);
        editState.setEnabled(enabled);
        editZipcode.setEnabled(enabled);
        editHome.setEnabled(enabled);
        editCell.setEnabled(enabled);
        editEmail.setEnabled(enabled);
        buttonBirthday.setEnabled(enabled);
    }

    private abstract class SimpleTextWatcher implements TextWatcher {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {}

        @Override
        public abstract void afterTextChanged(Editable s);
    }
}
