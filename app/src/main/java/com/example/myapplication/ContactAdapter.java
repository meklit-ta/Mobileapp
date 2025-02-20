package com.example.myapplication;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class ContactAdapter extends RecyclerView.Adapter<ContactAdapter.ContactViewHolder> {
    private List<Contact> contactData;
    private View.OnClickListener onItemClickListener;
    private boolean isDeleteMode = false;

    public ContactAdapter(List<Contact> contactData) {
        this.contactData = contactData;
    }

    public void setOnItemClickListener(View.OnClickListener listener) {
        this.onItemClickListener = listener;
    }

    public void setDeleteMode(boolean isDeleteMode) {
        this.isDeleteMode = isDeleteMode;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ContactViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_contact_list_item, parent, false);
        return new ContactViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ContactViewHolder holder, int position) {
        Contact contact = contactData.get(position);
        holder.textViewName.setText(contact.getContactName());
        holder.textViewPhone.setText(contact.getPhoneNumber());

        holder.buttonDeleteContact.setVisibility(isDeleteMode ? View.VISIBLE : View.GONE);

        if (onItemClickListener != null) {
            holder.itemView.setOnClickListener(onItemClickListener);
        }

        holder.buttonDeleteContact.setOnClickListener(v -> {
            contactData.remove(position);
            notifyItemRemoved(position);
        });
    }

    @Override
    public int getItemCount() {
        return contactData.size();
    }

    public static class ContactViewHolder extends RecyclerView.ViewHolder {
        TextView textViewName, textViewPhone;
        Button buttonDeleteContact;

        public ContactViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewName = itemView.findViewById(R.id.textContactName);
            textViewPhone = itemView.findViewById(R.id.textPhoneNumber);
            buttonDeleteContact = itemView.findViewById(R.id.buttonDeleteContact);
        }
    }
}
