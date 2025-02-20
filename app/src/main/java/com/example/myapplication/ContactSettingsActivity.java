package com.example.myapplication;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class ContactSettingsActivity extends AppCompatActivity {

    private ImageButton btnMap, btnContacts, btnSettings;
    private RadioGroup radioGroupSortBy, RadioGroupSortOrder;
    private RadioButton radioName, radioCity, radioBirthday, radioAscending, radioDescending;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_contact_settings);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.btnSettings), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        btnMap = findViewById(R.id.btnMap);
        btnContacts = findViewById(R.id.btnContacts);
        btnSettings = findViewById(R.id.btnSettings);

        // Set click listeners
        btnMap.setOnClickListener(v -> openMap());
        btnContacts.setOnClickListener(v -> openContacts());
        btnSettings.setOnClickListener(v -> openSettings());
        initSettings();
        initSortOrderClick();
        initSortByClick();
    }
    private void openContacts() {
        Intent intent = new Intent(ContactSettingsActivity.this, ContactListActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    private void openMap() {
        Intent intent = new Intent(ContactSettingsActivity.this, ContactMapsActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    private void openSettings() {
        ImageButton ibSettings = findViewById(R.id.btnSettings);
        ibSettings.setEnabled(false);

    }

    private void initSettings(){
        String sortBy = getSharedPreferences("MyContactListPreferences", Context.MODE_PRIVATE).getString("sortfield","contactname");
        String sortOrder = getSharedPreferences("MyContactListPreferences", Context.MODE_PRIVATE).getString("sortorder","ASC");


        RadioButton radioName = findViewById(R.id.radioName);
        RadioButton radioCity = findViewById(R.id.radioCity);
        RadioButton radioBirthday = findViewById(R.id.radioBirthday);

        if (sortBy.equalsIgnoreCase("contactname")){
            radioName.setChecked(true);
        } else if (sortBy.equalsIgnoreCase("city")) {
            radioCity.setChecked(true);
        } else {
            radioBirthday.setChecked(true);
        }
        RadioButton radioAscending = findViewById(R.id.radioAscending);
        RadioButton radioDescending = findViewById(R.id.radioDescending);

        if (sortOrder.equalsIgnoreCase("ASC")){
            radioAscending.setChecked(true);
        } else {
            radioDescending.setChecked(true);
        }
    }

    private void  initSortByClick(){
        RadioGroup radioGroupSortBy =findViewById(R.id.radioGroupSortBy);
        radioGroupSortBy.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                RadioButton radioName = findViewById(R.id.radioName);
                RadioButton radioCity = findViewById(R.id.radioCity);

                if (radioName.isChecked()){
                    getSharedPreferences("MyContactListPreferences", Context.MODE_PRIVATE).edit().putString("sortfield", "contactname").apply();
                } else if (radioCity.isChecked()) {
                    getSharedPreferences("MyContactListPreferences", Context.MODE_PRIVATE).edit().putString("sortfield", "city").apply();
                } else {
                    getSharedPreferences("MyContactListPreferences", Context.MODE_PRIVATE).edit().putString("sortfield", "birthday").apply();

                }

            }
        });
    }

    private void  initSortOrderClick(){
        RadioGroup radioGroupSortOrder =findViewById(R.id.RadioGroupSortOrder);
        radioGroupSortOrder.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                RadioButton radioAscending = findViewById(R.id.radioAscending);
                RadioButton radioDescending = findViewById(R.id.radioDescending);

                if (radioAscending.isChecked()){
                    getSharedPreferences("MyContactListPreferences", Context.MODE_PRIVATE).edit().putString("sortorder", "ASC").apply();
                } else {
                    getSharedPreferences("MyContactListPreferences", Context.MODE_PRIVATE).edit().putString("sortorder", "DESC").apply();

                }

            }
        });
    }

}