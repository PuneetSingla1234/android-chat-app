package com.example.puneet2singla.chatapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {
    FirebaseAuth firebaseAuth;
    @BindView(R.id.etEmail)
    TextInputEditText etEmail;
    @BindView(R.id.etPassword)
    TextInputEditText etPassword;
    @BindView(R.id.buttonbSignUp)
    Button bSignUp;
    @BindView(R.id.bSignIn)
    Button bSignIn;
    FirebaseHelper helper;
    DatabaseReference myDataReference;
    ProgressDialog pdSignUp;
    ProgressDialog pdLogin;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        pdSignUp=new ProgressDialog(this);
        pdSignUp.setMessage("Signing Up...");
        pdLogin=new ProgressDialog(this);
        pdLogin.setMessage("Logging In...");
        ButterKnife.bind(this);
        try{
            FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        }
        catch(Exception e){

        }
        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        helper=new FirebaseHelper();

        if (firebaseUser != null) {
            Toast.makeText(getApplicationContext(), "Already Login!", Toast.LENGTH_LONG).show();
            myDataReference=helper.getMyDataReference();
            myDataReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    initSignIn(dataSnapshot);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Toast.makeText(getApplicationContext(),databaseError.getMessage(),Toast.LENGTH_SHORT);
                }
            });
        }

    }

    @OnClick({R.id.buttonbSignUp, R.id.bSignIn})
    public void onViewClicked(View view) {


        String email=""
                ,password="";
        try {
             email = etEmail.getText().toString();
             password = etPassword.getText().toString();
        }catch (Exception e){
            Toast.makeText(getApplicationContext(),"Invalid Email Or Password Format!",Toast.LENGTH_SHORT);
            return;
        }

        switch (view.getId()) {
            case R.id.buttonbSignUp: {

                registerUser(email, password);

            }
            break;
            case R.id.bSignIn: {
                loginUser(email, password);
            }
            break;
        }
    }

    private void loginUser(String email, String password) {

        if (TextUtils.isEmpty(email)) {
            etEmail.setError("ENTER EMAIL!");
            return;
        }
        if (TextUtils.isEmpty(password)) {
            etPassword.setError("ENTER PASSWORD!");
            return;
        }
        pdLogin.show();
        firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(
                new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            etEmail.setText("");
                            etPassword.setText("");
                            pdLogin.dismiss();
                            myDataReference=helper.getMyDataReference();
                            myDataReference.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    initSignIn(dataSnapshot);
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {
                                    Toast.makeText(getApplicationContext(),databaseError.getMessage(),Toast.LENGTH_SHORT);
                                }
                            });

                            Toast.makeText(getApplicationContext(), "Login Successfully", Toast.LENGTH_LONG).show();
                        } else {
                            pdLogin.dismiss();
                            Toast.makeText(getApplicationContext(), task.getException().getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                }
        );
    }
    private void registerNewUser() {
        String email = helper.getAuthEmail();
        if (email != null) {
            User currentUser = new User(email, true, null);
            myDataReference.setValue(currentUser);
        }
    }

    private void initSignIn(DataSnapshot snapshot){
        User currUuser=snapshot.getValue(User.class);
        if(currUuser==null){
            registerNewUser();
        }
        displayContactsList();
    }

    private void registerUser(final String email, final String password) {
        if (TextUtils.isEmpty(email)) {
            etEmail.setError("ENTER EMAIL!");
            return;
        }
        if (TextUtils.isEmpty(password)) {
            etPassword.setError("ENTER PASSWORD!");
            return;
        }
        pdSignUp.show();
        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {

                            pdSignUp.dismiss();
                            Toast.makeText(getApplicationContext(), "User Registered Successfully!Logging In...", Toast.LENGTH_LONG).show();
                            loginUser(email, password);
                        } else {
                            pdSignUp.dismiss();
                            Toast.makeText(getApplicationContext(), task.getException().getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                });

    }
    public void displayContactsList(){
        Intent intent=new Intent(this,ContactList.class);
        startActivity(intent);
    }

}
