package com.mbouzaie.ft_hangouts;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.io.File;

public class ContactDetailsActivity extends AppCompatActivity {

    // creating variables for our image view and text view and string. .
    private String contactName, contactNumber, contactEmail, contactStreet, contactImage;
    private TextView contactTextView, nameTextView, emailTextView, streetTextView;
    private ImageView contactImageView;
    private ImageButton callImageButton, messageImageButton, emailImageButton, streetImageButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_details);
        contactName = getIntent().getStringExtra("name");
        contactNumber = getIntent().getStringExtra("phone");
        contactEmail = getIntent().getStringExtra("email");
        contactStreet = getIntent().getStringExtra("street");
        contactImage = getIntent().getStringExtra("image");
        nameTextView = findViewById(R.id.tv_contact_name);
        contactImageView = findViewById(R.id.iv_contact_details);
        contactTextView = findViewById(R.id.tv_phone);
        emailTextView = findViewById(R.id.tv_mail);
        streetTextView = findViewById(R.id.tv_street);
        nameTextView.setText(contactName);
        contactTextView.setText(contactNumber);
        emailTextView.setText(contactEmail);
        streetTextView.setText(contactStreet);
        callImageButton = findViewById(R.id.ib_call);
        messageImageButton = findViewById(R.id.ib_message);
        emailImageButton = findViewById(R.id.ib_mail);
        streetImageButton = findViewById(R.id.ib_street);
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
        startActivity(Intent.createChooser(emailIntent, "Send email..."));
    }
}