package com.mbouzaie.ft_hangouts;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.Menu;
import android.view.MenuInflater;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import com.google.android.material.textfield.TextInputLayout;

public class NewContactActivity extends AppCompatActivity {
    TextInputLayout nameInputLayout, phoneInputLayout, emailInputLayout, streetInputLayout, postalCodeInputLayout;
    EditText nameEditText, phoneEditText, emailEdittext, streetEditText, postalCodeEditText;
    DatabaseHelper databaseHelper;
    private void findViews() {
        nameInputLayout = findViewById(R.id.til_name);
        nameEditText = findViewById(R.id.et_name);
        phoneInputLayout = findViewById(R.id.til_phone);
        phoneEditText = findViewById(R.id.et_phone);
        emailInputLayout = findViewById(R.id.til_email);
        emailEdittext = findViewById(R.id.et_email);
        streetInputLayout = findViewById(R.id.til_street);
        streetEditText = findViewById(R.id.et_street);
        postalCodeInputLayout = findViewById(R.id.til_postal_code);
        postalCodeEditText = findViewById(R.id.et_postal_code);
    }
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_contact);
        findViews();
        Toolbar toolbar = findViewById(R.id.contact_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("New Contact");
        databaseHelper = new DatabaseHelper(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.contact_menu, menu);
        //if (!TextUtils.isEmpty(id))
            menu.findItem(R.id.delete_contact).setVisible(true);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.save_contact) {
            if (inputControl()) {
                Contact contact = new Contact(
                        nameEditText.getText().toString(),
                        phoneEditText.getText().toString(),
                        emailEdittext.getText().toString(),
                        streetEditText.getText().toString(),
                        postalCodeEditText.getText().toString());
                databaseHelper.createContact(contact);
            }
            return true;
        } else if (id == R.id.delete_contact) {// Handle the "Delete Contact" action
            // Add your code here
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private boolean isPhoneNumberValid(String phoneNumber) {
        // Use the Patterns class to check if the input matches a phone number pattern
        return Patterns.PHONE.matcher(phoneNumber).matches();
    }

    private boolean isEmailValid(String email) {
        // Use the Patterns class to check if the input matches a phone number pattern
        return Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    private boolean inputControl() {
        boolean control = true;
        if (nameEditText.getText().length() < 3 || nameEditText.getText().length() > 20) {
            nameInputLayout.setError("full name is mandatory and must be between 3 and 20 characters");
            control = false;
        } else {
            nameInputLayout.setError(null);
        }
        if (!isPhoneNumberValid(phoneEditText.getText().toString())) {
            phoneInputLayout.setError("Phone number isn't valid");
            control = false;
        } else {
            phoneInputLayout.setError(null);
        }
        if (!isEmailValid(emailEdittext.getText().toString())) {
            emailInputLayout.setError("Phone number isn't valid");
            control = false;
        } else {
            emailInputLayout.setError(null);
        }
        return control;
    }
}
