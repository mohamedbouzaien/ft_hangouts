package com.mbouzaie.ft_hangouts;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.util.ArrayList;

public class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.MyViewHolder> {

    Activity activity;
    Context context;
    ArrayList<String> contactsIds,
            contactsNames,
            contactsPhones,
            contactsEmails,
            contactsStreets,
            contactsPostalCodes,
            contactsImages;

    CustomAdapter(Activity activity,
                  Context context,
                  ArrayList<String> contactsIds,
                  ArrayList<String> contactsNames,
                  ArrayList<String> contactsPhones,
                  ArrayList<String> contactsEmails,
                  ArrayList<String> contactsStreets,
                  ArrayList<String> contactsPostalCodes,
                  ArrayList<String> contactsImages) {
        this.activity = activity;
        this.context = context;
        this.contactsIds = contactsIds;
        this.contactsNames = contactsNames;
        this.contactsPhones = contactsPhones;
        this.contactsEmails = contactsEmails;
        this.contactsStreets = contactsStreets;
        this.contactsPostalCodes = contactsPostalCodes;
        this.contactsImages = contactsImages;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(R.layout.card_contact, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CustomAdapter.MyViewHolder holder, final int position) {
        holder.name.setText(String.valueOf(contactsNames.get(position)));
        if (!contactsImages.get(position).isEmpty()) {
            Bitmap savedImage = BitmapFactory.decodeFile(new File(context.getFilesDir(), contactsImages.get(position)).getAbsolutePath());

            holder.image.setImageBitmap(savedImage);
        }
        else {
            holder.image.setImageResource(R.mipmap.ic_user);
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            int p = position;
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ContactDetailsActivity.class);
                intent.putExtra("id", String.valueOf(contactsIds.get(p)));
                intent.putExtra("name", String.valueOf(contactsNames.get(p)));
                intent.putExtra("phone", String.valueOf(contactsPhones.get(p)));
                intent.putExtra("email", String.valueOf(contactsEmails.get(p)));
                intent.putExtra("street", String.valueOf(contactsStreets.get(p)));
                intent.putExtra("postal_code", String.valueOf(contactsPostalCodes.get(p)));
                intent.putExtra("image", String.valueOf(contactsImages.get(p)));
                context.startActivity(intent);
            }
        });
        holder.message.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sms(position);
            }
        });
        holder.call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                call(position);
            }
        });
    }

    public void filterList(ArrayList<String> filteredNames,
                           ArrayList<String> filteredIds,
                           ArrayList<String> filteredPhones,
                           ArrayList<String> filteredEmails,
                           ArrayList<String> filteredStreets,
                           ArrayList<String> filteredPostalCodes,
                           ArrayList<String> filteredImages) {
        contactsNames = filteredNames;
        contactsIds = filteredIds;
        contactsPhones = filteredPhones;
        contactsEmails = filteredEmails;
        contactsStreets = filteredStreets;
        contactsPostalCodes = filteredPostalCodes;
        contactsImages = filteredImages;
        notifyDataSetChanged();
    }

    public void sms(int position) {
        String num = String.valueOf(contactsPhones.get(position));
        Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.parse("sms:" + num));
        intent.putExtra("sms_body", "");
        context.startActivity(intent);
    }

    public void call(int position) {
        String num = String.valueOf(contactsPhones.get(position));;
        Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + num));
        final int REQUEST_PHONE_CALL = 1;

        if (ContextCompat.checkSelfPermission(context, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.CALL_PHONE},REQUEST_PHONE_CALL);
        }
        else
        {
            context.startActivity(intent);
        }
    }

    @Override
    public int getItemCount() {
        return contactsIds.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView name;
        ImageButton call, message;
        ImageView image;
        // list of contacts created Adapter
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.et_name);
            message = itemView.findViewById(R.id.message);
            call = itemView.findViewById(R.id.call);
            image = itemView.findViewById(R.id.iv_contact);
        }
    }
}
