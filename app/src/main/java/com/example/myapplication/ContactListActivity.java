package com.example.myapplication;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ToggleButton;
import android.widget.Toast;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

public class ContactListActivity extends AppCompatActivity {
    private ImageButton imageButtonMap, imageButtonList, imageButtonSettings;
    private ContactDataSource dataSource;
    private List<Contact> contacts;
    private ContactAdapter contactAdapter;
    private boolean deleteMode = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_contact_list);

        initViews();
        initNavigationButtons();
        initDeleteToggle();
        initAddContactButton();
        loadContacts();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadContacts();
    }

    private void initViews() {
        imageButtonMap = findViewById(R.id.imageButtonMap);
        imageButtonList = findViewById(R.id.imageButtonList);
        imageButtonSettings = findViewById(R.id.imageButtonSettings);
    }

    private void initNavigationButtons() {
        imageButtonMap.setOnClickListener(v -> openActivity(ContactMapsActivity.class));
        imageButtonList.setOnClickListener(v -> imageButtonList.setEnabled(false));
        imageButtonSettings.setOnClickListener(v -> openActivity(ContactSettingsActivity.class));
    }

    private void openActivity(Class<?> cls) {
        Intent intent = new Intent(ContactListActivity.this, cls);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    private void initAddContactButton() {
        Button buttonNew = findViewById(R.id.buttonAddNew);
        buttonNew.setOnClickListener(v -> {
            Intent intent = new Intent(ContactListActivity.this, MainActivity.class);
            startActivity(intent);
        });
    }

    private void initDeleteToggle() {
        ToggleButton toggleDelete = findViewById(R.id.toggleDelete); // ✅ FIX: Changed Switch to ToggleButton
        toggleDelete.setOnCheckedChangeListener((buttonView, isChecked) -> {
            deleteMode = isChecked;
            if (contactAdapter != null) {
                contactAdapter.setDeleteMode(deleteMode);
            }
        });
    }

    private void loadContacts() {
        dataSource = new ContactDataSource(this);
        contacts = new ArrayList<>();

        try {
            dataSource.open();
            contacts = dataSource.getAllContacts();
            dataSource.close();

            if (contacts.isEmpty()) {
                Log.w("WARNING", "No contacts found, redirecting to MainActivity.");
                startActivity(new Intent(ContactListActivity.this, MainActivity.class));
                return;
            }

            setupRecyclerView();
        } catch (Exception e) {
            Log.e("ERROR", "Exception retrieving contacts", e);
            Toast.makeText(this, "Error retrieving contacts", Toast.LENGTH_LONG).show();
        }
    }

    private void setupRecyclerView() {
        RecyclerView rvContacts = findViewById(R.id.rvContacts);
        rvContacts.setLayoutManager(new LinearLayoutManager(this));

        contactAdapter = new ContactAdapter(contacts);
        contactAdapter.setDeleteMode(deleteMode);

        rvContacts.setAdapter(contactAdapter);


        contactAdapter.setOnItemClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int position = rvContacts.getChildAdapterPosition(view);
                if (position != RecyclerView.NO_POSITION) {
                    Contact selectedContact = contacts.get(position);
                    Intent intent = new Intent(ContactListActivity.this, MainActivity.class);
                    intent.putExtra("contactId", selectedContact.getContactID());
                    startActivity(intent);
                }
            }
        });
    }
}
