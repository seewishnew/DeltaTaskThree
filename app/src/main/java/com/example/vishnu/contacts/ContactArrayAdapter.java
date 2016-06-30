package com.example.vishnu.contacts;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

/**
 * Created by vishnu on 29/6/16.
 */
public class ContactArrayAdapter extends ArrayAdapter<Contact> {

    Context context;
    List<Contact> contacts;

    public ContactArrayAdapter(Context context, int resource, List<Contact> objects) {
        super(context, resource, objects);

        this.context = context;
        this.contacts = objects;
    }

    public View getView(int position, View converterView, ViewGroup parent){
        Contact contact = contacts.get(position);

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(
                context.LAYOUT_INFLATER_SERVICE
        );

        View view = inflater.inflate(R.layout.custom_list_item, null);

        TextView details = (TextView) view.findViewById(R.id.details);

        if(contact.hasPhoneNo())
            details.setText(contact.getName() + "\n" + contact.getPhoneNo());

        else if(contact.hasEmailID())
            details.setText(contact.getName() + "\n" + contact.getEmailID());

        ImageView profile = (ImageView) view.findViewById(R.id.profile);
        int res = context.getResources().getIdentifier(
                "image_" + contact.getId(),
                "drawable",
                context.getPackageName()
        );

        if(res == 0){
            profile.setImageResource(R.mipmap.ic_contact_default);
        }

        else{
            profile.setImageResource(res);
        }

        return view;

    }
}

