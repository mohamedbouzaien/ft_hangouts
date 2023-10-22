package com.mbouzaie.ft_hangouts;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.io.File;

public class ContactDetailsActivity extends AppCompatActivity {

    // creating variables for our image view and text view and string. .
    private String contactId,contactName, contactNumber, contactEmail, contactStreet, contactPostalCode, contactImage;
    private TextView contactTextView, nameTextView, emailTextView, streetTextView, postalCodeTextView;
    private ImageView contactImageView;
    private ImageButton callImageButton, messageImageButton, emailImageButton, streetImageButton, editImageButton, deleteImageButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        int selectedTheme = loadThemeFromPreferences();
        // Set the selected theme.
        setTheme(selectedTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_details);
        getContact();
        nameTextView = findViewById(R.id.tv_contact_name);
        contactImageView = findViewById(R.id.iv_contact_details);
        contactTextView = findViewById(R.id.tv_phone);
        emailTextView = findViewById(R.id.tv_mail);
        streetTextView = findViewById(R.id.tv_street);
        postalCodeTextView = findViewById(R.id.tv_postal_code);
        nameTextView.setText(contactName);
        contactTextView.setText(contactNumber);
        emailTextView.setText(contactEmail);
        streetTextView.setText(contactStreet);
        postalCodeTextView.setText(contactPostalCode);
        callImageButton = findViewById(R.id.ib_call);
        messageImageButton = findViewById(R.id.ib_message);
        emailImageButton = findViewById(R.id.ib_mail);
        streetImageButton = findViewById(R.id.ib_street);
        editImageButton = findViewById(R.id.ib_edit);
        deleteImageButton = findViewById(R.id.ib_delete);
        if (!contactImage.isEmpty()) {
            Bitmap savedImage = BitmapFactory.decodeFile(new File(getFilesDir(), contactImage).getAbsolutePath());

            contactImageView.setImageBitmap(savedImage);
        }
        callImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                call(contactNumber);
            }
        });

        messageImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sms(contactNumber);
            }
        });
        emailImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                email(contactEmail);
            }
        });
        streetImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                map(contactStreet + " " + contactPostalCode);
            }
        });

        if (contactStreet.isEmpty()) {
            streetImageButton.setVisibility(View.GONE);
        }
        if (contactEmail.isEmpty()) {
            emailImageButton.setVisibility(View.GONE);
        }

        editImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ContactDetailsActivity.this, NewContactActivity.class);
                intent.putExtra("id", String.valueOf(contactId));
                intent.putExtra("name", String.valueOf(contactName));
                intent.putExtra("phone", String.valueOf(contactNumber));
                intent.putExtra("email", String.valueOf(contactEmail));
                intent.putExtra("street", String.valueOf(contactStreet));
                intent.putExtra("postal_code", String.valueOf(contactPostalCode));
                intent.putExtra("image", String.valueOf(contactImage));
                startActivity(intent);
                finish();
            }
        });
        deleteImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(ContactDetailsActivity.this);

                builder.setTitle(getResources().getString(R.string.delete_dialog_title));
                builder.setMessage(getResources().getString(R.string.delete_dialog_description));
                builder.setPositiveButton(getResources().getString(R.string.delete_dialog_yes), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        DatabaseHelper databaseHelper = new DatabaseHelper(ContactDetailsActivity.this);
                        databaseHelper.deleteContact(contactId);
                        Toast.makeText(ContactDetailsActivity.this, getResources().getString(R.string.toast_contact_deleted), Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(ContactDetailsActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    }
                });
                builder.setNegativeButton(getResources().getString(R.string.delete_dialog_no), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        getContact();
    }

    void getContact() {
        contactId = getIntent().getStringExtra("id");
        contactName = getIntent().getStringExtra("name");
        contactNumber = getIntent().getStringExtra("phone");
        contactEmail = getIntent().getStringExtra("email");
        contactStreet = getIntent().getStringExtra("street");
        contactPostalCode = getIntent().getStringExtra("postal_code");
        contactImage = getIntent().getStringExtra("image");
    }
    public void sms(String contactNumber) {
        Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.parse("sms:" + contactNumber));
        intent.putExtra("sms_body", "");
        startActivity(intent);
    }

    public void call(String contactNumber) {
        Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + contactNumber));
        final int REQUEST_PHONE_CALL = 1;

        if (ContextCompat.checkSelfPermission(ContactDetailsActivity.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CALL_PHONE},REQUEST_PHONE_CALL);
        }
        else
        {
            startActivity(intent);
        }
    }

    public void email(String contactEmail) {
        Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                "mailto", contactEmail, null));
        startActivity(Intent.createChooser(emailIntent, getResources().getString(R.string.intent_email_title)));
    }
    private void map(String address) {
        Intent mapIntent = new Intent(Intent.ACTION_VIEW);
        Uri uri = Uri.parse("geo:0,0?q=" + address);
        mapIntent.setData(uri);
        mapIntent.setPackage("com.google.android.apps.maps");
        startActivity(mapIntent);
    }

    private int loadThemeFromPreferences() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        int defaultTheme = R.style.Theme_Ft_hangouts;
        return preferences.getInt("selected_theme", defaultTheme);
    }
}