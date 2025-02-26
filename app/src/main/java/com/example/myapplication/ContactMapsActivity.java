package com.example.myapplication;

import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class ContactMapsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_contact_map);

        // Adjust insets for the button

        // Initialize the Get Location button functionality
        initGetLocationButton();
    }

    private void initGetLocationButton() {
        Button locationButton = findViewById(R.id.buttonGetLocation);
        locationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText editAddress = findViewById(R.id.addressInput);
                EditText editCity = findViewById(R.id.cityInput);
                EditText editState = findViewById(R.id.stateInput);
                EditText editZipCode = findViewById(R.id.zipcodeInput);

                String address = editAddress.getText().toString() + ", " +
                        editCity.getText().toString() + ", " +
                        editState.getText().toString() + ", " +
                        editZipCode.getText().toString();

                List<Address> addresses = null;
                Geocoder geo = new Geocoder(ContactMapsActivity.this, Locale.getDefault());

                try {
                    addresses = geo.getFromLocationName(address, 1);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                TextView txtLatitude = findViewById(R.id.textLatitude);
                TextView txtLongitude = findViewById(R.id.textLongitude);

                if (addresses != null && !addresses.isEmpty()) {
                    txtLatitude.setText(String.valueOf(addresses.get(0).getLatitude()));
                    txtLongitude.setText(String.valueOf(addresses.get(0).getLongitude()));
                } else {
                    txtLatitude.setText("Not Found");
                    txtLongitude.setText("Not Found");
                }
            }
        });
    }
}
