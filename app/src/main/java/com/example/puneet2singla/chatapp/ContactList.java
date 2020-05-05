package com.example.puneet2singla.chatapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ContactList extends AppCompatActivity {
    @BindView(R.id.rvContactList)
    RecyclerView rvContactList;
    ContactListAdapter contactListAdapter;
    FirebaseHelper helper;
    private ChildEventListener childEventListener;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_list);
        ButterKnife.bind(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        GlideImageLoader imageLoader = new GlideImageLoader(this);
        contactListAdapter=new ContactListAdapter(this, new ArrayList<User>(),imageLoader);
        LinearLayoutManager llm=new LinearLayoutManager(this);
        rvContactList.setAdapter(contactListAdapter);
        rvContactList.setLayoutManager(llm);

        helper=new FirebaseHelper();
        FloatingActionButton fabAddContact = (FloatingActionButton) findViewById(R.id.fabAddContact);
        fabAddContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AddContactFragment addContactFragment=new AddContactFragment();
                addContactFragment.show(getSupportFragmentManager(),"Add Contact");
            }
        });
    }
    public void onPause(){
        try{
            unsubscribeForEvents();
            helper.changeUserConnectionStatus(false);
        }
        catch (Exception e){

        }
        helper.changeUserConnectionStatus(false);
        super.onPause();
    }

    private void unsubscribeForEvents() {
        if(childEventListener!=null){
            helper.getContactsReference().removeEventListener(childEventListener);
        }
    }

    public void onResume(){
        subscribeForEvents();
        helper.changeUserConnectionStatus(true);
        super.onResume();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_contactlist, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_logout) {
            helper.signOff();
            Intent intent = new Intent(this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
                    | Intent.FLAG_ACTIVITY_NEW_TASK
                    | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }

    private void subscribeForEvents() {
        childEventListener=new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                String emailkey=dataSnapshot.getKey();
                String email=emailkey.replace("_",".");
                boolean value=((Boolean)dataSnapshot.getValue()).booleanValue();
                User user=new User(email,value,null);
                onContactAdded(user);
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                String email = dataSnapshot.getKey();
                email = email.replace("_",".");
                boolean online = ((Boolean)dataSnapshot.getValue()).booleanValue();
                User user = new User(email, online, null);
                onContactChanged(user);
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                String email = dataSnapshot.getKey();
                email = email.replace("_",".");
                boolean online = ((Boolean)dataSnapshot.getValue()).booleanValue();
                User user = new User(email, online, null);
                onContactRemoved(user);
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        helper.getContactsReference().addChildEventListener(childEventListener);

    }


    public void onContactAdded(User user){
        contactListAdapter.addUser(user);
    }
    public void onContactRemoved(User user){
        contactListAdapter.removeUser(user);
    }
    public void onContactChanged(User user){
        contactListAdapter.update(user);
    }
}
