package com.example.a16022635.mychatapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity {
    //Firebase Auth
    private FirebaseAuth mAuth;

    private DatabaseReference mDatabase;

    private TextInputLayout etDisplayName;
    private TextInputLayout etEmail;
    private TextInputLayout etPassword;
    private Button btnCreateAcc;

    private ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        getSupportActionBar().setTitle("Create Account");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        mProgressDialog = new ProgressDialog(this);
        etDisplayName = (TextInputLayout) findViewById(R.id.regDisplayName);
        etEmail = (TextInputLayout) findViewById(R.id.regEmail);
        etPassword = (TextInputLayout) findViewById(R.id.regPassword);
        btnCreateAcc = (Button) findViewById(R.id.regCreateBtn);

        btnCreateAcc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String newUserDisplayName = etDisplayName.getEditText().getText().toString();
                String newUserEmail = etEmail.getEditText().getText().toString();
                String newUserPassword = etPassword.getEditText().getText().toString();

                if(newUserDisplayName.isEmpty() || newUserEmail.isEmpty() || newUserPassword.isEmpty()){

                } else {
                    mProgressDialog.setTitle("Registering your account");
                    mProgressDialog.setMessage("Please wait a moment.");
                    mProgressDialog.setCanceledOnTouchOutside(false);
                    mProgressDialog.show();

                    registerUser(newUserDisplayName, newUserEmail, newUserPassword);
                }
            }
        });


    }

    private void registerUser(final String newUserDisplayName, String newUserEmail, String newUserPassword) {
        mAuth.createUserWithEmailAndPassword(newUserEmail, newUserPassword).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {

                    FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
                    String userID = currentUser.getUid();
                    mDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(userID);

                    HashMap<String, String> userMap = new HashMap<>();
                    userMap.put("name", newUserDisplayName);
                    userMap.put("bio", "Hey there! I'm using MyChatApp!");
                    userMap.put("image", "default");
                    userMap.put("thumb_image", "default");

                    //Checking if it adds into the Database
                    mDatabase.setValue(userMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                mProgressDialog.dismiss();
                                Intent mainIntent = new Intent(RegisterActivity.this, MainActivity.class);
                                mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(mainIntent);
                                finish();
                            }
                        }
                    });



                } else {
                    mProgressDialog.hide();
                    Toast.makeText(RegisterActivity.this, "Unable to create account", Toast.LENGTH_LONG).show();
                }
            }
        });

    }


}
