package com.example.myapplication;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Point;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.gms.location.Priority;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ContactMapActivity extends AppCompatActivity implements OnMapReadyCallback {
    LocationManager locationManager;
    LocationListener gpsListener;
    LocationListener networkListener;
    Location currentBestLocation;

    FusedLocationProviderClient fusedLocationProviderClient;

    LocationRequest locationRequest;
    LocationCallback locationCallback;

    ArrayList<Contact> contacts = new ArrayList<>();
    Contact currentContact = null;
    GoogleMap gMap;
    SensorManager sensorManager;
    Sensor accelerometer;
    Sensor magnetometer;

    TextView textHeading;
    final int PERMISSION_REQUEST_LOCATION = 101;
    private boolean locationUpdatesRequested = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_contact_map2);

        Bundle extras = getIntent().getExtras();
        try {
            ContactDataSource ds = new ContactDataSource(ContactMapActivity.this);
            ds.open();
            if (extras != null) {
                currentContact = ds.getSpecificContact(extras.getInt("contactId"));
            } else {
                contacts = ds.getContacts("contactname", "ASC");
            }
            ds.close();
        } catch (Exception e) {
            Toast.makeText(getBaseContext(), "Contact could not be retrieved.", Toast.LENGTH_LONG).show();
        }

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        createLocationRequest();
        createLocationCallback();

        // initGetLocationButton();
        initNavigationButtons();
        initMapTypeButton();

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        magnetometer = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);

        if (accelerometer != null && magnetometer != null) {
            sensorManager.registerListener(mySensorEventListener, accelerometer, SensorManager.SENSOR_DELAY_FASTEST);
            sensorManager.registerListener(mySensorEventListener, magnetometer, SensorManager.SENSOR_DELAY_FASTEST);
        } else {
            Toast.makeText(this, "Sensors not found", Toast.LENGTH_LONG).show();
        }

        textHeading = findViewById(R.id.textHeading);


    }

    private void openContacts() {
        Intent intent = new Intent(ContactMapActivity.this, ContactListActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    private void openSetting() {
        Intent intent = new Intent(ContactMapActivity.this, ContactSettingsActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    private void openMap() {
        ImageButton ibMap = findViewById(R.id.btnMap);
        ibMap.setEnabled(false);
    }

    private void initNavigationButtons() {
        ImageButton btnContacts = findViewById(R.id.btnContacts);
        btnContacts.setOnClickListener(v -> openContacts());

        ImageButton btnSettings = findViewById(R.id.btnSettings);
        btnSettings.setOnClickListener(v -> openSetting());

        // For the map button, if you only want to disable it on the map screen:
        ImageButton btnMap = findViewById(R.id.btnMap);
        btnMap.setEnabled(false);
    }

/*
    private void initGetLocationButton() {
        Button locationButton = findViewById(R.id.buttonGetLocation);
        locationButton.setOnClickListener(v -> {
            getCoordinatesFromAddress();
            requestLocationUpdates();
        });
    }

    private void getCoordinatesFromAddress() {
        try {
            EditText editAddress = findViewById(R.id.editAddress);
            EditText editCity = findViewById(R.id.editCity);
            EditText editState = findViewById(R.id.editState);
            EditText editZipcode = findViewById(R.id.editZipcode);
            String addressString = editAddress.getText().toString() + ","
                    + editCity.getText().toString() + ","
                    + editState.getText().toString() + ","
                    + editZipcode.getText().toString();
            Geocoder geo = new Geocoder(ContactMapActivity.this);
            List<Address> results = geo.getFromLocationName(addressString, 1);
            TextView textLatitude = findViewById(R.id.textLatitude);
            TextView textLongitude = findViewById(R.id.textLongitude);
            TextView textAccuracy = findViewById(R.id.textAccuracy);
            if (results != null && !results.isEmpty()) {
                Address bestGuess = results.get(0);
                textLatitude.setText(String.valueOf(bestGuess.getLatitude()));
                textLongitude.setText(String.valueOf(bestGuess.getLongitude()));
                textAccuracy.setText("N/A");
            } else {
                Toast.makeText(ContactMapActivity.this, "No geocoder results for that address", Toast.LENGTH_LONG).show();
            }
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(ContactMapActivity.this, "Geocoder error", Toast.LENGTH_LONG).show();
        }
    }*/

    @Override
    public void onResume() {
        super.onResume();
        /*if (!locationUpdatesRequested) {
            requestLocationUpdates();
        }*/
    }

    @Override
    public void onPause() {
        super.onPause();
        stopLocationUpdates();
    }

    private void requestLocationUpdates() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (ContextCompat.checkSelfPermission(ContactMapActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(ContactMapActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                    Snackbar.make(findViewById(R.id.activity_contact_map),
                                    "MyContactList requires this permission to locate your contacts",
                                    Snackbar.LENGTH_INDEFINITE)
                            .setAction("OK", view -> ActivityCompat.requestPermissions(ContactMapActivity.this,
                                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                    PERMISSION_REQUEST_LOCATION))
                            .show();
                } else {
                    ActivityCompat.requestPermissions(ContactMapActivity.this,
                            new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                            PERMISSION_REQUEST_LOCATION);
                }
            } else {
                startLocationUpdates();
            }
        } else {
            startLocationUpdates();
        }
    }

    private void startLocationUpdates() {
        if (Build.VERSION.SDK_INT >= 23 &&
                ContextCompat.checkSelfPermission(getBaseContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(getBaseContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        if (gMap == null) {
            // The map is not ready yet, so don't call setMyLocationEnabled.
            return;
        }
        fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, null);
        gMap.setMyLocationEnabled(true);
        locationUpdatesRequested = true;
    }

    /*try {
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        gpsListener = new LocationListener() {
            @Override
            public void onLocationChanged(@NonNull Location location) {
                if (isBetterLocation(location)) {
                    currentBestLocation = location;
                }
                TextView textLatitude = findViewById(R.id.textLatitude);
                TextView textLongitude = findViewById(R.id.textLongitude);
                TextView textAccuracy = findViewById(R.id.textAccuracy);
                if (currentBestLocation != null) {
                    textLatitude.setText(String.valueOf(currentBestLocation.getLatitude()));
                    textLongitude.setText(String.valueOf(currentBestLocation.getLongitude()));
                    textAccuracy.setText(String.valueOf(currentBestLocation.getAccuracy()));
                }
            }
            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) { }
            @Override
            public void onProviderEnabled(String provider) { }
            @Override
            public void onProviderDisabled(String provider) { }
        };

        networkListener = new LocationListener() {
            @Override
            public void onLocationChanged(@NonNull Location location) {
                if (isBetterLocation(location)) {
                    currentBestLocation = location;
                }
            }
            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) { }
            @Override
            public void onProviderEnabled(String provider) { }
            @Override
            public void onProviderDisabled(String provider) { }
        };

        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, gpsListener);
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, networkListener);
        locationUpdatesRequested = true;
    } catch (Exception e) {
        Toast.makeText(getBaseContext(), "Error, Location not available", Toast.LENGTH_LONG).show();
    }*/
    private void stopLocationUpdates() {
        if (Build.VERSION.SDK_INT >= 23 &&
                ContextCompat.checkSelfPermission(getBaseContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(getBaseContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        fusedLocationProviderClient.removeLocationUpdates(locationCallback);
    }

    /* (locationManager != null) {
        if (gpsListener != null) {
            locationManager.removeUpdates(gpsListener);
            gpsListener = null;
        }
        if (networkListener != null) {
            locationManager.removeUpdates(networkListener);
            networkListener = null;
        }
        locationManager = null;
    }
    locationUpdatesRequested = false;
}*/
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_LOCATION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startLocationUpdates();
            } else {
                Toast.makeText(ContactMapActivity.this, "MyContactList will not locate your contacts.", Toast.LENGTH_LONG).show();
            }
        }
    }
    /*
    private boolean isBetterLocation(Location location) {
        boolean isBetter = false;
        if (currentBestLocation == null) {
            isBetter = true;
        } else if (location.getAccuracy() <= currentBestLocation.getAccuracy()) {
            isBetter = true;
        } else if (location.getTime() - currentBestLocation.getTime() > 5 * 60 * 1000) {
            isBetter = true;
        }
        return isBetter;
    }*/
/*
    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        gMap = googleMap;
        gMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

        Point size = new Point();
        WindowManager w = getWindowManager();
        w.getDefaultDisplay().getSize(size);
        int measuredWidth = size.x;
        int measuredHeight = size.y;
        if (contacts.size()>0) {
            LatLngBounds.Builder builder = new LatLngBounds.Builder();
            for(int i =0; i<contacts.size(); i++) {
                currentContact = contacts.get(i);

                Geocoder geo = new Geocoder(this);
                List<Address> addresses = null;

                String address = currentContact.getStreetAddress() + "," + currentContact.getCity() + "," + currentContact.getState() + "," + currentContact.getZipCode();

                try {
                    addresses = geo.getFromLocationName(address, 1);
                }
                catch (IOException e) {
                    e.printStackTrace();
                }
            if (addresses != null && !addresses.isEmpty()) {
                LatLng point = new LatLng(addresses.get(0).getLatitude(), addresses.get(0).getLongitude());
                builder.include(point);
                gMap.addMarker(new MarkerOptions()
                        .position(point)
                        .title(currentContact.getContactName())
                        .snippet(address));
            } else {
                Log.w("ContactMapActivity", "No geocoder results for address: " + address);
            }
        }
        try {
            gMap.animateCamera(CameraUpdateFactory.newLatLngBounds(builder.build(), measuredWidth, measuredHeight, 450));
        } catch (Exception e) {
            e.printStackTrace();
        }} else {
            if (currentContact != null) {
                Geocoder geo = new Geocoder(this);
                List<Address> addresses = null;

                String address = currentContact.getStreetAddress() + "," + currentContact.getCity() + "," + currentContact.getState() + "," + currentContact.getZipCode();
                try {
                    addresses = geo.getFromLocationName(address, 1);
                }
                catch (IOException e) {
                    e.printStackTrace();
                }
                LatLng point = new LatLng(addresses.get(0).getLatitude(), addresses.get(0).getLongitude());
                gMap.addMarker(new MarkerOptions().position(point).title(currentContact.getContactName()).snippet(address));
                gMap.animateCamera(CameraUpdateFactory.newLatLngZoom(point, 16));

            }
            else {
                AlertDialog alertDialog = new AlertDialog.Builder(ContactMapActivity.this).create();
                alertDialog.setTitle("No data");
                alertDialog.setMessage("No data is available for the mapping function.");
                alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }});
                alertDialog.show();
                }
            }

        try {
            if (Build.VERSION.SDK_INT >= 23) {
                if (ContextCompat.checkSelfPermission(ContactMapActivity.this,
                        android.Manifest.permission.ACCESS_FINE_LOCATION)
                        == PackageManager.PERMISSION_GRANTED) {
                    startLocationUpdates();
                } else {
                    if (ActivityCompat.shouldShowRequestPermissionRationale(
                            ContactMapActivity.this,
                            android.Manifest.permission.ACCESS_FINE_LOCATION)) {

                        Snackbar.make(findViewById(R.id.activity_contact_map),
                                        "MyContactList requires this permission to locate your contacts",
                                        Snackbar.LENGTH_INDEFINITE)
                                .setAction("OK", new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        ActivityCompat.requestPermissions(
                                                ContactMapActivity.this,
                                                new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                                                PERMISSION_REQUEST_LOCATION
                                        );
                                    }
                                })
                                .show();

                    } else {
                        ActivityCompat.requestPermissions(
                                ContactMapActivity.this,
                                new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                                PERMISSION_REQUEST_LOCATION
                        );
                    }
                }
            } else {
                startLocationUpdates();
            }
        } catch (Exception e) {
            Toast.makeText(getBaseContext(), "Error requesting permission", Toast.LENGTH_LONG).show();
        }
        if (!locationUpdatesRequested) {
            requestLocationUpdates();
        }

    }*/

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        gMap = googleMap;
        gMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        RadioButton rbNormal = findViewById(R.id.radioButtonNormal);
        rbNormal.setChecked(true);

        Point size = new Point();
        WindowManager w = getWindowManager();
        w.getDefaultDisplay().getSize(size);
        int measuredWidth = size.x;
        int measuredHeight = size.y;

        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        boolean atLeastOneMarker = false;

        if (contacts != null && contacts.size() > 0) {
            for (int i = 0; i < contacts.size(); i++) {
                currentContact = contacts.get(i);
                Geocoder geo = new Geocoder(this);
                List<Address> addresses = null;
                String addressString = currentContact.getStreetAddress() + ", " +
                        currentContact.getCity() + ", " +
                        currentContact.getState() + " " +
                        currentContact.getZipCode();
                Log.d("ContactMapActivity", "Geocoding address: " + addressString);
                try {
                    addresses = geo.getFromLocationName(addressString, 1);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if (addresses != null && !addresses.isEmpty()) {
                    Address bestGuess = addresses.get(0);
                    LatLng point = new LatLng(bestGuess.getLatitude(), bestGuess.getLongitude());
                    builder.include(point);
                    gMap.addMarker(new MarkerOptions()
                            .position(point)
                            .title(currentContact.getContactName())
                            .snippet(addressString));
                    atLeastOneMarker = true;
                } else {
                    Log.w("ContactMapActivity", "No geocoder results for address: " + addressString);
                }
            }
        } else if (currentContact != null) {
            Geocoder geo = new Geocoder(this);
            List<Address> addresses = null;
            String addressString = currentContact.getStreetAddress() + ", " +
                    currentContact.getCity() + ", " +
                    currentContact.getState() + ", " +
                    currentContact.getZipCode();
            try {
                addresses = geo.getFromLocationName(addressString, 1);
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (addresses != null && !addresses.isEmpty()) {
                Address bestGuess = addresses.get(0);
                LatLng point = new LatLng(bestGuess.getLatitude(), bestGuess.getLongitude());
                gMap.addMarker(new MarkerOptions()
                        .position(point)
                        .title(currentContact.getContactName())
                        .snippet(addressString));
                gMap.animateCamera(CameraUpdateFactory.newLatLngZoom(point, 16));
                atLeastOneMarker = true;
            } else {
                Log.w("ContactMapActivity", "No geocoder results for address: " + addressString);
            }
        }

        if (atLeastOneMarker) {
            try {
                gMap.animateCamera(CameraUpdateFactory.newLatLngBounds(builder.build(), measuredWidth, measuredHeight, 450));
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            AlertDialog alertDialog = new AlertDialog.Builder(ContactMapActivity.this).create();
            alertDialog.setTitle("No Data");
            alertDialog.setMessage("No valid location data is available for mapping.");
            alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK", (dialog, which) -> finish());
            alertDialog.show();
        }

        // Request location updates for real-time GPS, etc.
        try {
            if (Build.VERSION.SDK_INT >= 23) {
                if (ContextCompat.checkSelfPermission(ContactMapActivity.this,
                        Manifest.permission.ACCESS_FINE_LOCATION)
                        == PackageManager.PERMISSION_GRANTED) {
                    startLocationUpdates();
                } else {
                    if (ActivityCompat.shouldShowRequestPermissionRationale(
                            ContactMapActivity.this,
                            Manifest.permission.ACCESS_FINE_LOCATION)) {
                        Snackbar.make(findViewById(R.id.activity_contact_map),
                                        "MyContactList requires this permission to locate your contacts",
                                        Snackbar.LENGTH_INDEFINITE)
                                .setAction("OK", view -> ActivityCompat.requestPermissions(
                                        ContactMapActivity.this,
                                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                        PERMISSION_REQUEST_LOCATION))
                                .show();
                    } else {
                        ActivityCompat.requestPermissions(
                                ContactMapActivity.this,
                                new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                PERMISSION_REQUEST_LOCATION);
                    }
                }
            } else {
                startLocationUpdates();
            }
        } catch (Exception e) {
            Toast.makeText(getBaseContext(), "Error requesting permission", Toast.LENGTH_LONG).show();
        }
        if (!locationUpdatesRequested) {
            requestLocationUpdates();
        }
    }


    private void createLocationRequest() {
        locationRequest = LocationRequest.create();
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(5000);
        locationRequest.setPriority(Priority.PRIORITY_HIGH_ACCURACY);
    }

    private void createLocationCallback() {
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {

                if (locationResult == null) {
                    return;
                }
                for (Location location : locationResult.getLocations()) {
                    Toast.makeText(getBaseContext(), "Lat: " + location.getLatitude() + " Long: " + location.getLongitude() + " Accuracy: " + location.getAccuracy(), Toast.LENGTH_SHORT).show();
                }

            }

            ;
        };
    }

    private void initMapTypeButton() {
        RadioGroup radioGroupMapType = findViewById(R.id.radioGroupMapType);
        radioGroupMapType.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                RadioButton rbNormal = findViewById(R.id.radioButtonNormal);
                if (rbNormal.isChecked()) {
                    gMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                } else {
                    gMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
                }
            }
        });
    }

    private SensorEventListener mySensorEventListener = new SensorEventListener() {
        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {
        }

        float[] accelerometerValues;
        float[] magneticValues;

        public void onSensorChanged(SensorEvent event) {
            if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER)
                accelerometerValues = event.values;
            if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) magneticValues = event.values;
            if (accelerometerValues !=null && magneticValues != null){
                float R[] = new float[9];
                float I[] = new float[9];

                boolean success = SensorManager.getRotationMatrix(R, I, accelerometerValues, magneticValues);

                if (success) {
                    float orientation[] = new float[3];
                    SensorManager.getOrientation(R, orientation);
                    float azimut = (float) Math.toDegrees(orientation[0]);
                    if (azimut < 0.0f) {
                        azimut += 360.0f;
                    }

                    String direction;
                    if (azimut >= 315 || azimut < 45) {
                        direction = "N";
                    } else if (azimut >= 225 && azimut < 315) {
                        direction = "W";
                    } else if (azimut >= 135 && azimut < 225) {
                        direction = "S";
                    } else {
                        direction = "E";
                    }
                    textHeading.setText(direction);
                }
            }
        }
    };
}