package com.mbouzaie.ft_hangouts;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.MenuItemCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.RadioGroup;
import android.widget.SearchView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import com.mbouzaie.ft_hangouts.App;
public class MainActivity extends AppCompatActivity {
    RecyclerView recyclerView;
    DatabaseHelper databaseHelper;
    ArrayList<String> contactsIds, contactsNames, contactsPhones, contactsEmails, contactsStreets, contactsPostalCodes, contactsImages;
    CustomAdapter customAdapter;
    String pauseDate = "";
    boolean isAppInBackground = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        int selectedTheme = loadThemeFromPreferences();
        // Set the selected theme.
        setTheme(selectedTheme);
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
    protected void onPause() {
        super.onPause();
        SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        long time = System.currentTimeMillis();
        editor.putLong("pref_last_visit", time);
        editor.apply();

        ((App) this.getApplication()).startActivityTransitionTimer();
    }

    @Override
    protected void onResume() {
        super.onResume();
        getContacts();
        customAdapter.notifyDataSetChanged();
        App app = (App) this.getApplication();
        if (app.wasInBackground) {
            SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
            long time = sharedPref.getLong("pref_last_visit", System.currentTimeMillis());
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss", Locale.US);
            Date date = new Date(time);
            String resDate = formatter.format(date);
            String message = getString(R.string.toast_last_visit) + ": " + resDate;
            Toast.makeText(this, message, Toast.LENGTH_LONG).show();
        }

        app.stopActivityTransitionTimer();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        MenuItem searchViewItem = menu.findItem(R.id.app_bar_search);
        final SearchView searchView = (SearchView) searchViewItem.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchView.clearFocus();
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filter(newText.toLowerCase());
                return false;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (R.id.pick_color == id) {
            showColorSelectionDialog();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showColorSelectionDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = getLayoutInflater().inflate(R.layout.color_selection_dialog, null);
        builder.setView(dialogView);

        final RadioGroup colorRadioGroup = dialogView.findViewById(R.id.color_radio_group);

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                int selectedColorId = colorRadioGroup.getCheckedRadioButtonId();
                if (selectedColorId == R.id.red_option) {
                    setAppTheme(R.style.Theme_Ft_hangouts_Red);
                    recreate();
                }
                else if (selectedColorId == R.id.green_option) {
                    setAppTheme(R.style.Theme_Ft_hangouts_Green);
                    recreate();
                } else if (selectedColorId == R.id.blue_option) {
                    setAppTheme(R.style.Theme_Ft_hangouts_Blue);
                    recreate();
                }
            }
        });

        builder.setNegativeButton(getResources().getString(R.string.dialog_cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });

        builder.create().show();
    }
    private void setAppTheme(int themeId) {
        saveThemeToPreferences(themeId);
        setTheme(themeId);
        recreate();
    }
    private int loadThemeFromPreferences() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        int defaultTheme = R.style.Theme_Ft_hangouts;
        return preferences.getInt("selected_theme", defaultTheme);
    }
    private void saveThemeToPreferences(int themeId) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt("selected_theme", themeId);
        editor.apply();
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
            Toast.makeText(this, getResources().getString(R.string.toast_not_found), Toast.LENGTH_SHORT).show();
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