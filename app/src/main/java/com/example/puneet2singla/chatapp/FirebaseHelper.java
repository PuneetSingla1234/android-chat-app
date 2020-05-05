package com.example.puneet2singla.chatapp;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class FirebaseHelper {

    private DatabaseReference dataReference;
    private final static String SEPARATOR = "___";
    private final static String CHATS_PATH = "chats";
    private final static String USERS_PATH = "users";
    public final static String CONTACTS_PATH = "contacts";


    public FirebaseHelper() {
        dataReference= FirebaseDatabase.getInstance().getReference();
    }

    public DatabaseReference getDataReference(String email){
        if(email==null)
            return null;
        String emailKey=email.replace(".","_");
        return dataReference.getRoot().child(USERS_PATH).child(emailKey);
    }

    public DatabaseReference getOneContactReference(String mainEmail,String childEmail){
        String childKey=childEmail.replace(".","_");
        return getDataReference(mainEmail).child(CONTACTS_PATH).child(childKey);
    }

    public DatabaseReference getDataReference(){
        return dataReference;
    }

    public String getAuthEmail(){
        FirebaseUser user= FirebaseAuth.getInstance().getCurrentUser();
        if(user!=null){
            String email=user.getEmail();
            return email;
        }
        return null;
    }

    public DatabaseReference getMyDataReference() {
        return getDataReference(getAuthEmail());
    }

    public DatabaseReference getContactsReference(){
        return getMyDataReference().child(CONTACTS_PATH);
    }

    public void changeUserConnectionStatus(boolean online) {
        if (getMyDataReference() != null) {
            Map<String, Object> updates = new HashMap<String, Object>();
            updates.put("online", online);
            getMyDataReference().updateChildren(updates);
            notifyContactsOfConnectionChange(online);
        }
    }

    private void notifyContactsOfConnectionChange(boolean online) {
        notifyContactsOfConnectionChange(online,false);
    }
    public DatabaseReference getContactsReference(String email){
        DatabaseReference reference=getDataReference(email);
        return reference.child(CONTACTS_PATH);
    }

    private void notifyContactsOfConnectionChange(final boolean online, final boolean signoff) {
         final String myEmail=getAuthEmail();
         getContactsReference().addListenerForSingleValueEvent(new ValueEventListener() {
             @Override
             public void onDataChange(DataSnapshot dataSnapshot) {
                 for(DataSnapshot snapshot:dataSnapshot.getChildren()){
                     String email=snapshot.getKey();
                     DatabaseReference reference=getOneContactReference(email,myEmail);
                     reference.setValue(online);
                 }
                 if (signoff){
                     FirebaseAuth.getInstance().signOut();
                 }
             }


             @Override
             public void onCancelled(DatabaseError databaseError) {

             }
         });
    }

    public void signOff(){
        notifyContactsOfConnectionChange(User.OFFLINE, true);
    }
    public DatabaseReference getChatsReference(String receiver){
        String keySender = getAuthEmail().replace(".","_");
        String keyReceiver = receiver.replace(".","_");

        String keyChat = keySender + SEPARATOR + keyReceiver;
        if (keySender.compareTo(keyReceiver) > 0) {
            keyChat = keyReceiver + SEPARATOR + keySender;
        }
        return dataReference.getRoot().child(CHATS_PATH).child(keyChat);
    }
}
