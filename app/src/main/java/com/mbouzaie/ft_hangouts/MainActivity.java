package com.mbouzaie.ft_hangouts;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.MenuItemCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.SearchView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    RecyclerView recyclerView;
    DatabaseHelper databaseHelper;
    ArrayList<String> contactsIds, contactsNames, contactsPhones, contactsEmails, contactsStreets, contactsPostalCodes, contactsImages;
    CustomAdapter customAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.main_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Contacts");
        FloatingActionButton button = findViewById(R.id.addClientButton);
        recyclerView = findViewById(R.id.recyclerview);

        databaseHelper = new DatabaseHelper(this);
        contactsIds = new ArrayList<>();
        contactsNames = new ArrayList<>();
        contactsPhones = new ArrayList<>();
        contactsEmails = new ArrayList<>();
        contactsStreets = new ArrayList<>();
        contactsPostalCodes = new ArrayList<>();
        contactsImages = new ArrayList<>();


        customAdapter = new CustomAdapter(this,this,
                contactsIds,
                contactsNames,
                contactsPhones,
                contactsEmails,
                contactsStreets,
                contactsPostalCodes,
                contactsImages);
        recyclerView.setAdapter(customAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(MainActivity.this));
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent createContactIntent = new Intent(MainActivity.this, NewContactActivity.class);
                startActivity(createContactIntent);
            }
        });
    }
    @Override
    protected void onResume() {
        super.onResume();
        getContacts();
        customAdapter.notifyDataSetChanged();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        // on below line we are getting our menu item as search view item
        MenuItem searchViewItem = menu.findItem(R.id.app_bar_search);
        // on below line we are creating a variable for our search view.
        final SearchView searchView = (SearchView) searchViewItem.getActionView();
        // on below line we are setting on query text listener for our search view.
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // on query submit we are clearing the focus for our search view.
                searchView.clearFocus();
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                // on changing the text in our search view we are calling
                // a filter method to filter our array list.
                filter(newText.toLowerCase());
                return false;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }

    private void filter(String text) {
        ArrayList<String> filteredNames = new ArrayList<>();
        ArrayList<String> filteredIds = new ArrayList<>();
        ArrayList<String> filteredPhones = new ArrayList<>();
        ArrayList<String> filteredEmails = new ArrayList<>();
        ArrayList<String> filteredStreets = new ArrayList<>();
        ArrayList<String> filteredPostalCodes = new ArrayList<>();
        ArrayList<String> filteredImages = new ArrayList<>();
        for (int i = 0; i < contactsNames.size(); i++) {
            if (contactsNames.get(i).toLowerCase().contains(text.toLowerCase())) {
                filteredNames.add(contactsNames.get(i));
                filteredIds.add(contactsIds.get(i));
                filteredPhones.add(contactsPhones.get(i));
                filteredEmails.add(contactsEmails.get(i));
                filteredStreets.add(contactsStreets.get(i));
                filteredPostalCodes.add(contactsPostalCodes.get(i));
                filteredImages.add(contactsImages.get(i));
            }
        }
        if (filteredNames.isEmpty()) {
            Toast.makeText(this, "No Contact Found", Toast.LENGTH_SHORT).show();
        }
        customAdapter.filterList(filteredNames, filteredIds, filteredPhones, filteredEmails, filteredStreets, filteredPostalCodes, filteredImages);
    }

    void getContacts() {
        Cursor cursor = databaseHelper.getAllContact();
        contactsIds.clear();
        contactsNames.clear();
        contactsPhones.clear();
        contactsEmails.clear();
        contactsStreets.clear();
        contactsPostalCodes.clear();
        contactsImages.clear();

        if (cursor.getCount() == 0)
            Log.v(this.getClass().getName(), "No Contacts");
        else {
            while (cursor.moveToNext()) {
                contactsIds.add(cursor.getString(0));
                contactsNames.add(cursor.getString(1));
                contactsPhones.add(cursor.getString(2));
                contactsEmails.add(cursor.getString(3));
                contactsStreets.add(cursor.getString(4));
                contactsPostalCodes.add(cursor.getString(5));
                contactsImages.add(cursor.getString(6));
            }
        }
    }
}