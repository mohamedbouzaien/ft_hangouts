package com.mbouzaie.ft_hangouts;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.TextUtils;
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
    TextInputLayout nameInputLayout;
    EditText nameEditText;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_contact);

        nameInputLayout = findViewById(R.id.til_name);
        nameEditText = findViewById(R.id.et_name);
        nameInputLayout.getEditText().setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    nameInputLayout.setHintTextAppearance(R.style.FocusedHint);
                } else {
                    nameInputLayout.setHintTextAppearance(R.style.UnfocusedHint);
                }
            }
        });
        Toolbar toolbar = findViewById(R.id.contact_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("New Contact");
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
        nameInputLayout = findViewById(R.id.til_name);
        if (id == R.id.save_contact) {// Handle the "Save Contact" action
            nameInputLayout.setError("problemos");
            return true;
        } else if (id == R.id.delete_contact) {// Handle the "Delete Contact" action
            // Add your code here
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
