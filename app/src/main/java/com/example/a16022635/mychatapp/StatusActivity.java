package com.example.a16022635.mychatapp;

import android.app.ProgressDialog;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class StatusActivity extends AppCompatActivity {

    private TextInputLayout bioInput;
    private Button bioSaveBtn;
    private ProgressDialog mProgressDialog;

    //Firebase
    private DatabaseReference mStatusDatabase;
    private FirebaseUser mCurrentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_status);
        getSupportActionBar().setTitle("My Status");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //Firebase
        mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();
        String current_userID = mCurrentUser.getUid();
        mStatusDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(current_userID);

        mProgressDialog = new ProgressDialog(this);
        bioInput = (TextInputLayout) findViewById(R.id.bioInput);
        bioSaveBtn = (Button) findViewById(R.id.bioSave);

        // Gives the user the current bio
        String bio = getIntent().getStringExtra("bio");
        bioInput.getEditText().setText(bio);

        bioSaveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mProgressDialog.setCanceledOnTouchOutside(false);
                mProgressDialog.setTitle("Updating Biography");
                mProgressDialog.setMessage("Please wait a moment");
                mProgressDialog.show();

                String bio = bioInput.getEditText().getText().toString();
                mStatusDatabase.child("bio").setValue(bio).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            mProgressDialog.dismiss();

                        } else {
                            mProgressDialog.hide();
                            Toast.makeText(getApplicationContext(), "Error when updating biography", Toast.LENGTH_LONG).show();
                        }
                    }
                });

            }
        });
    }
}
