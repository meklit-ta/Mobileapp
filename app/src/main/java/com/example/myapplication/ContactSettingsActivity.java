package com.example.myapplication;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.appcompat.app.AppCompatActivity;

public class ContactSettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_setting);  // ✅ Ensure this file exists

        initListButton();
        initMapButton();
        initSettingsButton();
        initSettings();
        initSortByClick();
        initSortOrderClick();
    }

    private void initListButton() {
        ImageButton ibList = findViewById(R.id.imageButtonList);
        if (ibList != null) {  // ✅ Prevent NullPointerException
            ibList.setOnClickListener(v -> {
                Intent intent = new Intent(ContactSettingsActivity.this, ContactListActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            });
        } else {
            Log.e("ContactSettingsActivity", "Error: imageButtonList is null! Check XML.");
        }
    }

    private void initMapButton() {
        ImageButton ibMap = findViewById(R.id.imageButtonMap);
        if (ibMap != null) {
            ibMap.setOnClickListener(v -> {
                Intent intent = new Intent(ContactSettingsActivity.this, ContactMapsActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            });
        } else {
            Log.e("ContactSettingsActivity", "Error: imageButtonMap is null! Check XML.");
        }
    }

    private void initSettingsButton() {
        ImageButton ibSettings = findViewById(R.id.imageButtonSettings);
        if (ibSettings != null) {
            ibSettings.setEnabled(false);
        }
    }

    private void initSettings() {
        String sortBy = getSharedPreferences("MyContactListPrefrences", Context.MODE_PRIVATE)
                .getString("sortfield", "contactname");
        String sortOrder = getSharedPreferences("MyContactListPrefrences", Context.MODE_PRIVATE)
                .getString("sortorder", "ASC");

        RadioButton rbName = findViewById(R.id.radioName);
        RadioButton rbCity = findViewById(R.id.radioCity);
        RadioButton rbBirthday = findViewById(R.id.radioBirthday);

        if (sortBy.equalsIgnoreCase("contactname")) {
            rbName.setChecked(true);
        } else if (sortBy.equalsIgnoreCase("city")) {
            rbCity.setChecked(true);
        } else {
            rbBirthday.setChecked(true);
        }

        RadioButton rbAscending = findViewById(R.id.radioAscending);
        RadioButton rbDescending = findViewById(R.id.radioDescending);

        if (sortOrder.equalsIgnoreCase("ASC")) {  // ✅ Corrected condition
            rbAscending.setChecked(true);
        } else {
            rbDescending.setChecked(true);
        }
    }

    private void initSortByClick() {
        RadioGroup rgSortBy = findViewById(R.id.radioGroupSortBy);
        rgSortBy.setOnCheckedChangeListener((group, checkedId) -> {
            RadioButton rbName = findViewById(R.id.radioName);
            RadioButton rbCity = findViewById(R.id.radioCity);

            if (rbName.isChecked()) {
                getSharedPreferences("MyContactListPrefrences", Context.MODE_PRIVATE)
                        .edit().putString("sortfield", "contactname").apply();
            } else if (rbCity.isChecked()) {
                getSharedPreferences("MyContactListPrefrences", Context.MODE_PRIVATE)
                        .edit().putString("sortfield", "city").apply();
            } else {
                getSharedPreferences("MyContactListPrefrences", Context.MODE_PRIVATE)
                        .edit().putString("sortfield", "birthday").apply();
            }
        });
    }

    private void initSortOrderClick() {
        RadioGroup rgSortOrder = findViewById(R.id.radioGroupSortOrder);
        rgSortOrder.setOnCheckedChangeListener((group, checkedId) -> {
            RadioButton rbAscending = findViewById(R.id.radioAscending);
            if (rbAscending.isChecked()) {
                getSharedPreferences("MyContactListPrefrences", Context.MODE_PRIVATE)
                        .edit().putString("sortorder", "ASC").apply();
            } else {
                getSharedPreferences("MyContactListPrefrences", Context.MODE_PRIVATE)
                        .edit().putString("sortorder", "DESC").apply();
            }
        });
    }
}
