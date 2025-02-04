package com.example.myapplication;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import androidx.appcompat.app.AppCompatActivity;


public class ContactSettingsActivity extends AppCompatActivity {

        private RadioGroup radioGroupSortBy, radioGroupSortOrder;
        private SharedPreferences sharedPreferences;
        private Button buttonSaveSettings;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_contact_setting);

            // Initialize UI elements
            radioGroupSortBy = findViewById(R.id.radioGroupSortBy);
            radioGroupSortOrder = findViewById(R.id.radioGroupSortOrder);
            buttonSaveSettings = findViewById(R.id.buttonSaveSettings);

            // Load saved preferences
            sharedPreferences = getSharedPreferences("MyContactListPreferences", MODE_PRIVATE);
            loadPreferences();

            // Save preferences when button is clicked
            buttonSaveSettings.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    savePreferences();
                }
            });
        }

        private void loadPreferences() {
            // Retrieve stored values
            String sortBy = sharedPreferences.getString("sortfield", "contactname");
            String sortOrder = sharedPreferences.getString("sortorder", "ASC");

            // Set selected RadioButton based on stored values
            if (sortBy.equalsIgnoreCase("contactname")) {
                ((RadioButton) findViewById(R.id.radioName)).setChecked(true);
            } else if (sortBy.equalsIgnoreCase("city")) {
                ((RadioButton) findViewById(R.id.radioCity)).setChecked(true);
            } else {
                ((RadioButton) findViewById(R.id.radioBirthday)).setChecked(true);
            }

            if (sortOrder.equalsIgnoreCase("ASC")) {
                ((RadioButton) findViewById(R.id.radioAscending)).setChecked(true);
            } else {
                ((RadioButton) findViewById(R.id.radioDescending)).setChecked(true);
            }
        }

        private void savePreferences() {
            SharedPreferences.Editor editor = sharedPreferences.edit();

            // Get selected sort field
            int selectedSortBy = radioGroupSortBy.getCheckedRadioButtonId();
            if (selectedSortBy == R.id.radioName) {
                editor.putString("sortfield", "contactname");
            } else if (selectedSortBy == R.id.radioCity) {
                editor.putString("sortfield", "city");
            } else {
                editor.putString("sortfield", "birthday");
            }

            // Get selected sort order
            int selectedSortOrder = radioGroupSortOrder.getCheckedRadioButtonId();
            if (selectedSortOrder == R.id.radioAscending) {
                editor.putString("sortorder", "ASC");
            } else {
                editor.putString("sortorder", "DESC");
            }

            editor.apply(); // Save preferences
        }
    }
