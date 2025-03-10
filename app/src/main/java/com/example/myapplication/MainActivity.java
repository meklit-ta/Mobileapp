package com.example.myapplication;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.icu.util.Calendar;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.telephony.PhoneNumberFormattingTextWatcher;
import android.text.Editable;
import android.text.InputType;
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
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.FragmentManager;

import com.google.android.material.snackbar.Snackbar;

public class MainActivity extends AppCompatActivity implements DatePickerDialog.SaveDateListener {

    private ToggleButton toggleEdit;
    private Button btnSave, buttonBirthday;
    private TextView textBirthday;
    private EditText editContact, editAddress, editCity, editState, editZipcode, editHome, editCell, editEmail;
    private ImageButton btnContacts, btnMap, btnSettings;
    private LinearLayout toolbar, bottomNavigationBar;
    private Contact currentContact;
    private static final int PERMISSION_REQUEST_PHONE = 102;

    private static final int PERMISSION_REQUEST_CAMERA = 103;

    private static final int PERMISSION_REQUEST_SMS = 104;

    final int CAMERA_REQUEST = 1888;
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
        initCallFunction();
        initMessageFunction();
        initImageButton();
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

        if (currentContact.getContactID() != -1) {
            Toast.makeText(getBaseContext(), "Contact must be saved before it can be mapped", Toast.LENGTH_LONG).show();
        } else {
            intent.putExtra("contactId", currentContact.getContactID());
        }


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
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
        });

        editAddress.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                currentContact.setStreetAddress(editAddress.getText().toString());
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
        });

        editCity.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                currentContact.setCity(editCity.getText().toString());
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
        });

        editState.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                currentContact.setState(editState.getText().toString());
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
        });

        editZipcode.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                currentContact.setZipCode(editZipcode.getText().toString());
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
        });

        editHome.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                currentContact.setPhoneNumber(editHome.getText().toString());
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
        });

        editCell.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                currentContact.setCellNumber(editCell.getText().toString());
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
        });

        editEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                currentContact.seteMail(editEmail.getText().toString());
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
        });
    }

    private void setForEditing(boolean enabled) {
        editContact.setEnabled(enabled);
        editAddress.setEnabled(enabled);
        editCity.setEnabled(enabled);
        editState.setEnabled(enabled);
        editZipcode.setEnabled(enabled);
        if (enabled) {
            editHome.setInputType(android.text.InputType.TYPE_CLASS_PHONE);
            editCell.setInputType(android.text.InputType.TYPE_CLASS_PHONE);
        } else {
            editHome.setInputType(android.text.InputType.TYPE_NULL);
            editCell.setInputType(android.text.InputType.TYPE_NULL);
        }
        editEmail.setEnabled(enabled);
        buttonBirthday.setEnabled(enabled);
        ImageButton picture = findViewById(R.id.imageContact);
        picture.setEnabled(enabled);
    }

    /*
        private void initSaveButton() {
            dataSource = new ContactDataSource(com.example.myproject.com.example.myproject.MainActivity.this);

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
        ImageButton picture = findViewById(R.id.imageContact);
        if(currentContact.getPicture() != null) {
            picture.setImageBitmap(currentContact.getPicture());
        } else {
            picture.setImageResource(R.drawable.photoicon);

        }

        if (currentContact.getBirthday() != null) {
            textBirthday.setText(DateFormat.format("MM/dd/yyyy", currentContact.getBirthday().getTimeInMillis()).toString());
        } else {
            textBirthday.setText("No Birthday");
        }
    }

    private void initCallFunction() {
        EditText editPhone = (EditText) findViewById(R.id.editHome);
        editPhone.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                checkPhonePermission(currentContact.getPhoneNumber());
                return false;
            }
        });
        /*EditText editCell = (EditText) findViewById(R.id.editCell);
        editCell.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                checkPhonePermission(currentContact.getCellNumber());
                return false;
            }
        });*/
    }

    private void initMessageFunction() {
        EditText editCell = (EditText) findViewById(R.id.editCell);
        editCell.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                checkSMSPermission(currentContact.getCellNumber());
                return false;
            }
        });
    }

    private void checkPhonePermission(String phoneNumber) {
        if (Build.VERSION.SDK_INT >= 23) {
            if (ContextCompat.checkSelfPermission(MainActivity.this,
                    android.Manifest.permission.CALL_PHONE)
                    != PackageManager.PERMISSION_GRANTED) {

                if (ActivityCompat.shouldShowRequestPermissionRationale(
                        MainActivity.this,
                        Manifest.permission.CALL_PHONE)) {
                    Snackbar.make(findViewById(R.id.activity_main),
                                    "MyContactList requires this permission to place a call from the app.",
                                    Snackbar.LENGTH_INDEFINITE).setAction("OK", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    ActivityCompat.requestPermissions(MainActivity.this, new String[]{
                                            android.Manifest.permission.CALL_PHONE}, PERMISSION_REQUEST_PHONE);
                                }
                            })
                            .show();
                } else {
                    ActivityCompat.requestPermissions(MainActivity.this, new String[]{ android.Manifest.permission.CALL_PHONE}, PERMISSION_REQUEST_PHONE);
                }
            } else {
                callContact(phoneNumber);
            }
        } else {
            callContact(phoneNumber);
        }
    }

    private void checkSMSPermission(String cellNumber) {
        if (Build.VERSION.SDK_INT >= 23) {
            if (ContextCompat.checkSelfPermission(MainActivity.this,
                    Manifest.permission.SEND_SMS)
                    != PackageManager.PERMISSION_GRANTED) {

                if (ActivityCompat.shouldShowRequestPermissionRationale(
                        MainActivity.this,
                        Manifest.permission.SEND_SMS)) {
                    Snackbar.make(findViewById(R.id.activity_main),
                                    "MyContactList requires this permission to send a text from the app.",
                                    Snackbar.LENGTH_INDEFINITE).setAction("OK", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    ActivityCompat.requestPermissions(MainActivity.this, new String[]{
                                            Manifest.permission.SEND_SMS}, PERMISSION_REQUEST_SMS);
                                }
                            })
                            .show();
                } else {
                    ActivityCompat.requestPermissions(MainActivity.this, new String[]{ Manifest.permission.SEND_SMS}, PERMISSION_REQUEST_SMS);
                }
            } else {
                SMSContact(cellNumber);
            }
        } else {
            SMSContact(cellNumber);
        }
    }



    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[], @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case PERMISSION_REQUEST_PHONE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(MainActivity.this, "You may now call from this app.", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(MainActivity.this, "You will not be able to make calls from this app.", Toast.LENGTH_SHORT).show();
                }
            }
            case PERMISSION_REQUEST_CAMERA: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    takePhoto();
                } else {
                    Toast.makeText(MainActivity.this, "You will not be able to save contact pictures from this app.", Toast.LENGTH_SHORT).show();
                }
            }
            case PERMISSION_REQUEST_SMS: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(MainActivity.this, "You may now send a sms from this app.", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(MainActivity.this, "You will not be able to send sms from this app.", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }


    private void callContact(String phoneNumber) {
        Intent intent = new Intent(Intent.ACTION_CALL);
        intent.setData(Uri.parse("tel:" + phoneNumber));

        if (Build.VERSION.SDK_INT >= 23 && ContextCompat.checkSelfPermission( this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        startActivity(intent);
    }

    private void SMSContact(String cellNumber) {
        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setData(Uri.parse("smsto:" + cellNumber));
        if (intent.resolveActivity(getPackageManager()) != null) {
            if (Build.VERSION.SDK_INT >= 23 && ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            startActivity(intent);
        } else {
            Toast.makeText(MainActivity.this, "No app sms.", Toast.LENGTH_SHORT).show();
        }
    }

    private void initImageButton() {
        ImageButton ib = findViewById(R.id.imageContact);
        ib.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT >= 23) {
                    if (ContextCompat.checkSelfPermission(MainActivity.this,
                            Manifest.permission.CAMERA)
                            != PackageManager.PERMISSION_GRANTED)  {
                        if(ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this,android.Manifest.permission.CAMERA)){
                            Snackbar.make(findViewById(R.id.activity_main), "The app needs permission to take pictures.", Snackbar.LENGTH_INDEFINITE).setAction("OK", new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            ActivityCompat.requestPermissions(MainActivity.this, new String[]{android.Manifest.permission.CAMERA}, PERMISSION_REQUEST_CAMERA);
                                        }
                                    })
                                    .show();
                        } else {
                            ActivityCompat.requestPermissions(MainActivity.this, new String[]{android.Manifest.permission.CAMERA}, PERMISSION_REQUEST_CAMERA);
                        }
                    } else {
                        takePhoto();
                    }
                }
                else {
                    takePhoto();
                }
            }
        });
    }



    public void takePhoto() {
        Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(cameraIntent, CAMERA_REQUEST);
    }

    protected void onActivityResult(int RequestCode, int resultcode, Intent data) {
        super.onActivityResult(RequestCode, resultcode, data);
        if (RequestCode == CAMERA_REQUEST) {
            Bitmap photo = (Bitmap) data.getExtras().get("data");
            Bitmap scaledPhoto = Bitmap.createScaledBitmap(photo, 144, 144, true);
            ImageButton imageContact = findViewById(R.id.imageContact);
            imageContact.setImageBitmap(scaledPhoto);
            currentContact.setPicture(scaledPhoto);
        }
    }
}