package com.example.a16022635.mychatapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.media.Image;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.Random;
import java.util.Set;

import de.hdodenhof.circleimageview.CircleImageView;

public class SettingsActivity extends AppCompatActivity {

    private DatabaseReference mUsersDatabase;
    private FirebaseUser mCurrentUser;

    //Firebase Storage
    private StorageReference mImageStorage;

    // Android Layout
    private CircleImageView mDisplayImage;
    private TextView mDisplayName;
    private TextView mBio;
    private Button mChangeImgBtn;
    private Button mChangeBioBtn;
    private ProgressDialog mProgresDialog;


    private static final int GALLERY_PICK = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        getSupportActionBar().setTitle("Account Settings");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();
        mDisplayImage = (CircleImageView) findViewById(R.id.settingsImage);
        mDisplayName = (TextView) findViewById(R.id.settingsDisplayName);
        mBio = (TextView) findViewById(R.id.settingsBio);
        mChangeBioBtn = (Button) findViewById(R.id.settingsChgBio);
        mChangeImgBtn = (Button) findViewById(R.id.settingsChgImg);

        //Firebase Storage
        mImageStorage = FirebaseStorage.getInstance().getReference();

        String userID = mCurrentUser.getUid();
        mUsersDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(userID);

        mUsersDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //Whenever retrieving / changing data

                String displayName = dataSnapshot.child("name").getValue().toString();
                String img = dataSnapshot.child("image").getValue().toString();
                String bio = dataSnapshot.child("bio").getValue().toString();
                String thumb_img = dataSnapshot.child("thumb_image").getValue().toString();

                mDisplayName.setText(displayName);
                mBio.setText(bio);

                Picasso.with(SettingsActivity.this).load(img).into(mDisplayImage);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        mChangeBioBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String bio = mBio.getText().toString();

                Intent statusIntent = new Intent(SettingsActivity.this, StatusActivity.class);
                statusIntent.putExtra("bio", bio);
                startActivity(statusIntent);
            }
        });

        mChangeImgBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent galleryIntent = new Intent();
                galleryIntent.setType("image/*");
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(galleryIntent, "SELECT IMAGE"), GALLERY_PICK);

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //Check if there's an error
        if(requestCode == GALLERY_PICK && resultCode == RESULT_OK) {
            Uri imageUri = data.getData();

            CropImage.activity(imageUri)
                    .setAspectRatio(1,1)
                    .start(this);

        }

        //Check if the request code is passed through the CropActivity - CROP_IMAGE_ACTIVITY_REQUEST_CODE
        //makes sure the result retrieved is from the CropActivity
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {

            CropImage.ActivityResult result = CropImage.getActivityResult(data);

            if (resultCode == RESULT_OK) {
                mProgresDialog = new ProgressDialog(SettingsActivity.this);
                mProgresDialog.setTitle("Uploading Image");
                mProgresDialog.setMessage("Please wait a moment while we upload your image.");
                mProgresDialog.setCanceledOnTouchOutside(false);
                mProgresDialog.show();

                Uri resultUri = result.getUri();
                String current_userID = mCurrentUser.getUid();
                StorageReference filepath = mImageStorage.child("profile_images").child( current_userID + ".jpg");


                filepath.putFile(resultUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        if(task.isSuccessful()){
                            String downloadUrl = task.getResult().getDownloadUrl().toString();
                            mUsersDatabase.child("image").setValue(downloadUrl).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()){
                                        mProgresDialog.dismiss();
                                        Toast.makeText(SettingsActivity.this, "Successfully uploaded", Toast.LENGTH_LONG).show();

                                    }
                                }
                            });

                        } else {
                            mProgresDialog.dismiss();
                            Toast.makeText(SettingsActivity.this, "Error in uploading...", Toast.LENGTH_LONG).show();

                        }
                    }
                });

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {

                Exception error = result.getError();

            }
        }
    }

//    public static String random() {
//        Random generator = new Random();
//        StringBuilder randomStringBuilder = new StringBuilder();
//        int randomLength = generator.nextInt(10);
//        char tempChar;
//        for (int i = 0; i < randomLength; i++) {
//            tempChar = (char) (generator.nextInt(96) + 32);
//            randomStringBuilder.append(tempChar);
//        }
//
//        return randomStringBuilder.toString();
//    }
}
