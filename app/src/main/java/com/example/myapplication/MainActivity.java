package com.example.myapplication;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.icu.util.Calendar;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {

    private ToggleButton toggleEdit;
    private Button btnSave, buttonBirthday;
    private TextView textContact, textAddress, textBDay, textBirthday;
    private EditText editContact, editAddress, editCity, editState, editZipcode;
    private ImageButton btnContacts, btnMap, btnSettings;
    private LinearLayout toolbar, bottomNavigationBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        try {
            setContentView(R.layout.activity_main);
            ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.btnSave), (v, insets) -> {
                Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
                v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
                return insets;
            });
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Initialize Views
        toolbar = findViewById(R.id.toolbar);
        bottomNavigationBar = findViewById(R.id.bottomNavigationBar);

        toggleEdit = findViewById(R.id.toggleEdit);
        btnSave = findViewById(R.id.btnSave);
        buttonBirthday = findViewById(R.id.buttonBirthday);

        textContact = findViewById(R.id.textContact);
        textAddress = findViewById(R.id.textAddress);
        textBDay = findViewById(R.id.textBDay);
        textBirthday = findViewById(R.id.textBirthday);

        editContact = findViewById(R.id.editContact);
        editAddress = findViewById(R.id.editAddress);
        editCity = findViewById(R.id.editCity);
        editState = findViewById(R.id.editState);
        editZipcode = findViewById(R.id.editZipcode);

        btnContacts = findViewById(R.id.btnContacts);
        btnMap = findViewById(R.id.btnMap);
        btnSettings = findViewById(R.id.btnSettings);

        // Set click listeners for buttons
        btnSave.setOnClickListener(v -> saveData());
        buttonBirthday.setOnClickListener(v -> openDatePicker());
        btnContacts.setOnClickListener(v -> openContacts());
        btnMap.setOnClickListener(v -> openMap());
        btnSettings.setOnClickListener(v -> openSettings());

        // Toggle Edit Mode
        //toggleEdit.setOnCheckedChangeListener((buttonView, isChecked) -> enableEditing(isChecked));
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
        Intent intent = new Intent(MainActivity.this, ContactSettingActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }
    private void openDatePicker() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                (view, selectedYear, selectedMonth, selectedDay) -> {
                    String formattedDate = selectedDay + "/" + (selectedMonth + 1) + "/" + selectedYear;
                    textBirthday.setText(formattedDate);
                },
                year, month, day
        );
        datePickerDialog.show();
    }
    private void saveData() {
        String name = editContact.getText().toString().trim();
        String address = editAddress.getText().toString().trim();
        String city = editCity.getText().toString().trim();
        String state = editState.getText().toString().trim();
        String zipcode = editZipcode.getText().toString().trim();
        String birthday = textBirthday.getText().toString().trim();

        if (name.isEmpty() || address.isEmpty() || city.isEmpty() || state.isEmpty() || zipcode.isEmpty()) {
            textContact.setText("Please fill in all fields before saving.");
        } else {
            textContact.setText("Saved: " + name + " | " + birthday);
        }
    }


}
