package com.example.myapplication;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class ContactAdapter extends RecyclerView.Adapter<ContactAdapter.ContactViewHolder> {
    private ArrayList<Contact> contactData;
    private View.OnClickListener mOnItemClickListener; // Click listener
    private boolean isDeleting;
    private Context parentContext;

    // Constructor
    public ContactAdapter(ArrayList<Contact> arrayList, Context context) {
        contactData = arrayList;
        parentContext = context;
    }

    // Method to set click listener from Activity
    public void setOnItemClickListener(View.OnClickListener itemClickListener) {
        this.mOnItemClickListener = itemClickListener;
    }

    public void setDelete(boolean isDeleting) {
        this.isDeleting = isDeleting;
        notifyDataSetChanged(); // Refresh RecyclerView when deleting mode changes
    }

    @NonNull
    @Override
    public ContactViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_contact_list_item, parent, false);
        return new ContactViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ContactViewHolder holder, int position) {
        Log.d("DEBUG", "onBindViewHolder called for position: " + position);
        Contact contact = contactData.get(position);

        // Set contact name
        if (holder.textContactName == null) {
            Log.e("ERROR", "getContactTextView() returned null at position: " + position);
        } else {
            Log.d("DEBUG", "Setting contact name for position: " + position);
            holder.textContactName.setText(contact.getContactName());

            // Alternate colors: Even positions = Red, Odd positions = Blue
            int colorResId = (position % 2 == 0) ? R.color.system_red : R.color.system_blue;
            holder.textContactName.setTextColor(ContextCompat.getColor(parentContext, colorResId));
        }

        // Set phone number
        if (holder.textPhoneNumber == null) {
            Log.e("ERROR", "getTextPhoneView() returned null at position: " + position);
        } else {
            Log.d("DEBUG", "Setting phone number for position: " + position);
            holder.textPhoneNumber.setText(contact.getPhoneNumber());
        }

        // Set click listener
        holder.itemView.setTag(holder);
        holder.itemView.setOnClickListener(mOnItemClickListener);

        // Handle delete button visibility
        if (isDeleting) {
            holder.deleteButton.setVisibility(View.VISIBLE);
            holder.deleteButton.setOnClickListener(view -> deleteItem(position));
        } else {
            holder.deleteButton.setVisibility(View.INVISIBLE);
        }
    }

    private void deleteItem(int position) {
        Contact contact = contactData.get(position);
        ContactDataSource ds = new ContactDataSource(parentContext);
        try {
            ds.open();
            boolean didDelete = ds.deletecontact(contact.getContactID()); // Ensure correct method name
            ds.close();

            if (didDelete) {
                contactData.remove(position);
                notifyDataSetChanged();
                Toast.makeText(parentContext, "Contact Deleted!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(parentContext, "Delete failed!", Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            Toast.makeText(parentContext, "Delete failed!", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public int getItemCount() {
        return contactData != null ? contactData.size() : 0;
    }

    // ViewHolder Class
    public static class ContactViewHolder extends RecyclerView.ViewHolder {
        public TextView textContactName;
        public TextView textPhoneNumber;
        public Button deleteButton;

        public ContactViewHolder(@NonNull View itemView) {
            super(itemView);
            textContactName = itemView.findViewById(R.id.textContactName);
            textPhoneNumber = itemView.findViewById(R.id.textPhoneNumber);
            deleteButton = itemView.findViewById(R.id.buttonDeleteContact);

            // Debugging logs
            if (textContactName == null) {
                Log.e("ERROR", "TextView textContactName is NULL in ViewHolder");
            }
            if (textPhoneNumber == null) {
                Log.e("ERROR", "TextView textPhoneNumber is NULL in ViewHolder");
            }
            if (deleteButton == null) {
                Log.e("ERROR", "Button deleteButton is NULL in ViewHolder");
            }
        }
    }
}
