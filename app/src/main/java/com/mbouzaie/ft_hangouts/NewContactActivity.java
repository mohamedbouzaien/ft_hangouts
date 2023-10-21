package com.mbouzaie.ft_hangouts;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
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
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputLayout;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class NewContactActivity extends AppCompatActivity {

    private static final int RESULT_LOAD_IMG = 1;
    TextInputLayout nameInputLayout, phoneInputLayout, emailInputLayout, streetInputLayout, postalCodeInputLayout;
    EditText nameEditText, phoneEditText, emailEdittext, streetEditText, postalCodeEditText;

    ImageButton chooseImageButton;
    ImageView profileImageView;
    DatabaseHelper databaseHelper;
    Boolean imageUploaded = false;
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
        profileImageView = findViewById(R.id.iv_profile_image);
        chooseImageButton = findViewById(R.id.ib_choose_image);
        chooseImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                photoPickerIntent.setType("image/*");
                startActivityForResult(photoPickerIntent, RESULT_LOAD_IMG);
            }
        });
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
        String filename = "";
        if (id == R.id.save_contact) {
            if (inputControl()) {
                if (imageUploaded == true) {
                    BitmapDrawable bitmapDrawable = (BitmapDrawable) profileImageView.getDrawable();
                    Bitmap imageBitmap = bitmapDrawable.getBitmap();

    // Save the Bitmap to a local file
                    filename = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new java.util.Date())+ ".png";
                    FileOutputStream outStream = null;
                    try {
                        File file = new File(getFilesDir(), filename);
                        outStream = new FileOutputStream(file);
                        imageBitmap.compress(Bitmap.CompressFormat.PNG, 100, outStream); // Compress and save as PNG
                        outStream.flush();
                        outStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    } finally {
                        try {
                            if (outStream != null) {
                                outStream.close();
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
                Contact contact = new Contact(
                        nameEditText.getText().toString(),
                        phoneEditText.getText().toString(),
                        emailEdittext.getText().toString(),
                        streetEditText.getText().toString(),
                        postalCodeEditText.getText().toString(),
                        filename);
                databaseHelper.createContact(contact);
                Toast.makeText(this, "Contact Saved", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);
                finish();
            }
            return true;
        } else if (id == R.id.delete_contact) {// Handle the "Delete Contact" action
            // Add your code here
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int reqCode, int resultCode, Intent data) {
        super.onActivityResult(reqCode, resultCode, data);


        if (reqCode == RESULT_LOAD_IMG && resultCode == RESULT_OK) {
            try {
                final Uri imageUri = data.getData();
                InputStream imageStream = getContentResolver().openInputStream(imageUri);

                // Decode the image dimensions without loading the full bitmap into memory
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inJustDecodeBounds = true;
                BitmapFactory.decodeStream(imageStream, null, options);

                // Calculate inSampleSize to resize the image
                options.inSampleSize = calculateInSampleSize(options, 200, 200);

                // Decode the image with the calculated sample size
                options.inJustDecodeBounds = false;
                imageStream = getContentResolver().openInputStream(imageUri);
                Bitmap selectedImage = BitmapFactory.decodeStream(imageStream, null, options);

                profileImageView.setImageBitmap(selectedImage);
                imageUploaded = true;
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                Toast.makeText(this, "Something went wrong", Toast.LENGTH_LONG).show();
            }

        }else {
            Toast.makeText(this, "You haven't picked Image",Toast.LENGTH_LONG).show();
        }
    }

    private int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        final int imageHeight = options.outHeight;
        final int imageWidth = options.outWidth;
        int inSampleSize = 1;

        if (imageHeight > reqHeight || imageWidth > reqWidth) {
            final int halfHeight = imageHeight / 2;
            final int halfWidth = imageWidth / 2;

            while ((halfHeight / inSampleSize) >= reqHeight && (halfWidth / inSampleSize) >= reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }

    private boolean isPhoneNumberValid(String phoneNumber) {
        // Use the Patterns class to check if the input matches a phone number pattern
        return Patterns.PHONE.matcher(phoneNumber).matches();
    }

    private boolean isEmailValid(String email) {
        // Use the Patterns class to check if the input matches a phone number pattern
        return Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    private boolean isPostalCodeValid(String postalCode) {
        String regexPattern = "\\d{5}"; // Matches 5 digits
        Pattern pattern = Pattern.compile(regexPattern);

        Matcher matcher = pattern.matcher(postalCode);

        return matcher.matches();
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
        if (!emailEdittext.getText().toString().isEmpty() && !isEmailValid(emailEdittext.getText().toString())) {
            emailInputLayout.setError("Email isn't valid");
            control = false;
        } else {
            emailInputLayout.setError(null);
        }
        if (!postalCodeEditText.getText().toString().isEmpty() && !isPostalCodeValid(postalCodeEditText.getText().toString())) {
            postalCodeEditText.setError("Postal code isn't valid");
            control = false;
        } else {
            postalCodeEditText.setError(null);
        }
        return control;
    }
}
