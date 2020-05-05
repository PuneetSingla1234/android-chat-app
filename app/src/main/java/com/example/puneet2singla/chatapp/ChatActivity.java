package com.example.puneet2singla.chatapp;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;

public class ChatActivity extends AppCompatActivity {

    @BindView(R.id.imgAvatar)
    CircleImageView imgAvatar;
    @BindView(R.id.txtUser)
    TextView txtUser;
    @BindView(R.id.txtStatus)
    TextView txtStatus;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.messageRecyclerView)
    RecyclerView messageRecyclerView;
    @BindView(R.id.editTxtMessage)
    EditText editTxtMessage;
    @BindView(R.id.btnSendMessage)
    ImageButton btnSendMessage;
    FirebaseHelper helper;
    String receiver;
    ChatAdapter chatAdapter;
    ChildEventListener childEventListener;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        Intent intent = getIntent();
        receiver = intent.getStringExtra("Receiver");
        setToolbarData(intent);
        helper = new FirebaseHelper();
        try {
            FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        } catch (Exception e) {
        }
        setupAdapter();
    }

    @Override
    protected void onResume() {
        helper.changeUserConnectionStatus(true);
        subscribeForChatUpdates();
        super.onResume();

    }
    protected void onPause(){
        helper.changeUserConnectionStatus(false);
        unsubscribeForChatUpdates();
        super.onPause();

    }

    private void unsubscribeForChatUpdates() {
        if(childEventListener!=null){
            helper.getChatsReference(receiver).removeEventListener(childEventListener);
        }
    }

    private void subscribeForChatUpdates() {
        childEventListener=new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                String key=dataSnapshot.getKey();
                ChatMessage chatMessage=dataSnapshot.getValue(ChatMessage.class);
                String messageSender=chatMessage.getSender();
                chatMessage.setKey(key);
                messageSender=messageSender.replace("_",".");
                String currUserEmail=helper.getAuthEmail();
                chatMessage.setSentByMe(messageSender.equals(currUserEmail));
                onMessageReceived(chatMessage);
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        helper.getChatsReference(receiver).addChildEventListener(childEventListener);
    }

    private void onMessageReceived(ChatMessage msg) {

            chatAdapter.add(msg);
            Log.d("Received",msg.getMsg());
        messageRecyclerView.scrollToPosition(chatAdapter.getItemCount() - 1);

    }

    private void setupAdapter() {
        LinearLayoutManager llm = new LinearLayoutManager(this);
        chatAdapter = new ChatAdapter(getApplicationContext(), new ArrayList<ChatMessage>());
        messageRecyclerView.setLayoutManager(llm);
        messageRecyclerView.setAdapter(chatAdapter);
    }

    public void setToolbarData(Intent intent) {
        String recipient = intent.getStringExtra("Receiver");
        boolean online = intent.getBooleanExtra("IsOnline", false);
        String status = online ? "online" : "offline";
        int color = online ? Color.GREEN : Color.RED;
        txtUser.setText(recipient);
        txtStatus.setText(status);
        txtStatus.setTextColor(color);
        GlideImageLoader imageLoader = new GlideImageLoader(this);
        imageLoader.load(imgAvatar, AvatarHelper.getAvatarUrl(recipient));
    }

    @OnClick(R.id.btnSendMessage)
    public void onViewClicked() {

        String msg=editTxtMessage.getText().toString();
        if(TextUtils.isEmpty(msg)){
            editTxtMessage.setError("Enter Message");
        }
        else{
            sendMsg(msg);
            editTxtMessage.setText("");
        }
    }

    private void sendMsg(String msg) {
        DatabaseReference reference=helper.getChatsReference(receiver);
        String keySender=helper.getAuthEmail().replace(".","_");
        ChatMessage chatMessage=new ChatMessage(keySender,msg);
        reference.push().setValue(chatMessage);
    }


}
