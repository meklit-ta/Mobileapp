package com.example.myapplication;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import androidx.appcompat.app.AppCompatActivity;

public class ContactSettingsActivity extends AppCompatActivity {
    private SharedPreferences sharedPreferences;
    private RadioGroup radioGroupSortBy, radioGroupSortOrder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_settings);

        // Initialize SharedPreferences
        sharedPreferences = getSharedPreferences("ContactPrefs", MODE_PRIVATE);

        // Initialize RadioGroups
        radioGroupSortBy = findViewById(R.id.radioGroupSortBy);
        radioGroupSortOrder = findViewById(R.id.radioGroupSortOrder);

        // Load saved preferences
        loadPreferences();

        // Save selection when changed
        radioGroupSortBy.setOnCheckedChangeListener((group, checkedId) -> savePreferences());
        radioGroupSortOrder.setOnCheckedChangeListener((group, checkedId) -> savePreferences());
    }

    private void loadPreferences() {
        String sortBy = sharedPreferences.getString("sortField", "Name");
        String sortOrder = sharedPreferences.getString("sortOrder", "Ascending");

        // Set RadioButton checked based on stored values
        if (sortBy.equals("Name")) {
            ((RadioButton) findViewById(R.id.radioName)).setChecked(true);
        } else if (sortBy.equals("City")) {
            ((RadioButton) findViewById(R.id.radioCity)).setChecked(true);
        } else {
            ((RadioButton) findViewById(R.id.radioBirthday)).setChecked(true);
        }

        if (sortOrder.equals("Ascending")) {
            ((RadioButton) findViewById(R.id.radioAscending)).setChecked(true);
        } else {
            ((RadioButton) findViewById(R.id.radioDescending)).setChecked(true);
        }
    }

    private void savePreferences() {
        SharedPreferences.Editor editor = sharedPreferences.edit();

        // Save SortBy
        int selectedSortBy = radioGroupSortBy.getCheckedRadioButtonId();
        if (selectedSortBy == R.id.radioName) {
            editor.putString("sortField", "Name");
        } else if (selectedSortBy == R.id.radioCity) {
            editor.putString("sortField", "City");
        } else {
            editor.putString("sortField", "Birthday");
        }

        // Save SortOrder
        int selectedSortOrder = radioGroupSortOrder.getCheckedRadioButtonId();
        if (selectedSortOrder == R.id.radioAscending) {
            editor.putString("sortOrder", "Ascending");
        } else {
            editor.putString("sortOrder", "Descending");
        }

        editor.apply();  // Save changes
    }
}
