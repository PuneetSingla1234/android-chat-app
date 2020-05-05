package com.example.puneet2singla.chatapp;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

public class AddContactFragment extends DialogFragment{

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder=new AlertDialog.Builder(getContext());
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.fragment_add_contact, null, false);
        final EditText etEmail=(EditText)view.findViewById(R.id.etEmail);
        builder.setView(view);
        builder.setTitle("Add Contact");
        builder.setPositiveButton("Add", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String email=etEmail.getText().toString();
                addContact(email);
            }
        })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dismiss();
                    }
                });
        return builder.create();
    }
    public void addContact(final String email){
        final FirebaseHelper helper=new FirebaseHelper();
        DatabaseReference myDataReference=helper.getMyDataReference();
        DatabaseReference addedDataReference=helper.getDataReference(email);
        addedDataReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user=dataSnapshot.getValue(User.class);
                if(user!=null){
                    DatabaseReference myContactsReference=helper.getContactsReference();
                    boolean online=user.isOnline();
                    String key=email.replace(".","_");
                    myContactsReference.child(key).setValue(online);
                    String mykey=helper.getAuthEmail().replace(".","_");
                    DatabaseReference reverseUserReference=helper.getContactsReference(email).child(mykey);
                    reverseUserReference.setValue(true);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }


}

