package com.example.myapplication;

import android.content.Intent;
import android.icu.util.Calendar;
import android.os.Bundle;
import android.telephony.PhoneNumberFormattingTextWatcher;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.FragmentManager;

public class MainActivity extends AppCompatActivity implements DatePickerDialog.SaveDateListener {

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
            int contactId = extras.getInt("contactId", -1); // Default to -1 if missing
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
        setForEditing(false); // Ensure editing is disabled by default
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

    private void openContacts() {
        Intent intent = new Intent(MainActivity.this, ContactListActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    private void openMap() {
        Intent intent = new Intent(MainActivity.this, ContactMapActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    private void openSettings() {
        Intent intent = new Intent(MainActivity.this, ContactSettingsActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    @Override
    public void didFinishDatePickerDialog(java.util.Calendar selectedTime) {
        textBirthday.setText(DateFormat.format("MM/dd/yyyy", selectedTime));
        currentContact.setBirthday(selectedTime);
    }

    private void initChangeDateButton() {
        buttonBirthday.setOnClickListener(v -> {
            FragmentManager fm = getSupportFragmentManager();
            DatePickerDialog datePickerDialog = new DatePickerDialog();
            datePickerDialog.show(fm, "DatePick");
        });
    }

    private void initTextChangedEvents() {
        editContact.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                currentContact.setContactName(editContact.getText().toString());
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
        });

        editAddress.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                currentContact.setStreetAddress(editAddress.getText().toString());
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
        });

        editCity.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                currentContact.setCity(editCity.getText().toString());
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
        });

        editState.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                currentContact.setState(editState.getText().toString());
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
        });
        editHome.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                currentContact.setPhoneNumber(editHome.getText().toString());
            }
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
        });

        editCell.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                currentContact.setCellNumber(editCell.getText().toString());
            }
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
        });

        editEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                currentContact.seteMail(editEmail.getText().toString());
            }
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
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
    /*
        private void initSaveButton() {
            dataSource = new ContactDataSource(MainActivity.this);

            boolean wasSuccessful;
            try {
                dataSource.open();
                if (currentContact.getContactID() == -1) {
                    wasSuccessful = dataSource.insertContact(currentContact);
                    if (wasSuccessful) {
                        int newId = dataSource.getLastContactID();
                        currentContact.setContactID(newId);
                    }
                } else {
                    wasSuccessful = dataSource.updateContact(currentContact);
                }
                dataSource.close();
            } catch (Exception e) {
                wasSuccessful = false;
                e.printStackTrace();
            }

            if (wasSuccessful) {
                toggleEdit.setChecked(false);
                setForEditing(false);
            }
        }*/
    private void initSaveButton() {
        dataSource = new ContactDataSource(MainActivity.this);

        boolean wasSuccessful;
        try {
            dataSource.open();

            Log.d("SAVE", "Saving Contact: " + currentContact.getContactName());
            Log.d("SAVE", "Phone: " + currentContact.getPhoneNumber());
            Log.d("SAVE", "Cell: " + currentContact.getCellNumber());
            Log.d("SAVE", "Email: " + currentContact.geteMail());

            if (currentContact.getContactID() == -1) {
                wasSuccessful = dataSource.insertContact(currentContact);
                if (wasSuccessful) {
                    int newId = dataSource.getLastContactID();
                    currentContact.setContactID(newId);
                }
            } else {
                wasSuccessful = dataSource.updateContact(currentContact);
            }
            dataSource.close();
        } catch (Exception e) {
            wasSuccessful = false;
            e.printStackTrace();
        }

        if (wasSuccessful) {
            toggleEdit.setChecked(false);
            setForEditing(false);
        }
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
            textBirthday.setText(DateFormat.format("MM/dd/yyyy", currentContact.getBirthday().getTimeInMillis()).toString());
        } else {
            textBirthday.setText("No Birthday");
        }
    }
}