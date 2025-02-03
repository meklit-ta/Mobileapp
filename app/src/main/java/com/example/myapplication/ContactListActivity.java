package com.example.myapplication;
import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
public class ContactListActivity extends AppCompatActivity {

        private List<String> contactList;
        private TextView textViewContacts;

        @SuppressLint("MissingInflatedId")
        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_contact_list);  // Ensure this matches your XML file name


            textViewContacts = findViewById(R.id.textViewContacts);

            // Debug: Check if TextView is null
            if (textViewContacts == null) {
                throw new NullPointerException("TextView textViewContacts not found in activity_contact_list.xml");
            }

            // Retrieve sorting preferences
            SharedPreferences sharedPreferences = getSharedPreferences("ContactPrefs", MODE_PRIVATE);
            String sortBy = sharedPreferences.getString("sortField", "Name");
            String sortOrder = sharedPreferences.getString("sortOrder", "Ascending");

            // Load contacts (Replace this with real contact data)
            contactList = getContacts();

            // Apply sorting based on preferences
            sortContacts(sortBy, sortOrder);

            // Display contacts
            displayContacts();
        }

        // Example function to retrieve contacts (Replace with actual database/API call)
        private List<String> getContacts() {
            List<String> contacts = new ArrayList<>();
            contacts.add("Alice - City: New York");
            contacts.add("Bob - City: Milan");
            contacts.add("Charlie - City: Rome");
            return contacts;
        }

        // Sort contacts based on preferences
        private void sortContacts(String sortBy, String sortOrder) {
            if (sortBy.equals("Name")) {
                Collections.sort(contactList); // Default sorting by name
            } else if (sortBy.equals("City")) {
                Collections.sort(contactList, (a, b) -> a.split(": ")[1].compareTo(b.split(": ")[1])); // Sort by city
            }

            if (sortOrder.equals("Descending")) {
                Collections.reverse(contactList); // Reverse for descending order
            }
        }

        // Display sorted contacts in the TextView
        private void displayContacts() {
            StringBuilder builder = new StringBuilder();
            for (String contact : contactList) {
                builder.append(contact).append("\n");
            }
            textViewContacts.setText(builder.toString());
        }
    }
