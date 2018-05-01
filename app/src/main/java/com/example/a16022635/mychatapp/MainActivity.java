package com.example.a16022635.mychatapp;

import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private ViewPager mViewPager;
    private SectionsPagerAdapter mSectionsPagerAdapter;
    private TabLayout mTabLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();
        mViewPager = (ViewPager) findViewById(R.id.mainTabViewer);
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        mTabLayout = (TabLayout) findViewById(R.id.mainTab);

        mViewPager.setAdapter(mSectionsPagerAdapter);
        mTabLayout.setupWithViewPager(mViewPager);
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            sendToStart();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);

        getMenuInflater().inflate(R.menu.main_menu, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);

        if(item.getItemId() == R.id.mainLogoutBtn) {
            FirebaseAuth.getInstance().signOut();
            sendToStart();
        } else if (item.getItemId() == R.id.mainAccountSettingsBtn){
            sendToSettings();
        } else if (item.getItemId() == R.id.mainAllUsersBtn) {
            sendToAllUsers();
        }


        return true;
    }

    private void sendToAllUsers() {
        Intent allUsersIntent = new Intent(MainActivity.this, AllUsersActivity.class);
        startActivity(allUsersIntent);

    }

    private void sendToStart() {
        Intent startIntent = new Intent(MainActivity.this, StartActivity.class);
        startActivity(startIntent);
        finish();
    }

    private void sendToSettings() {
        Intent settingsIntent = new Intent(MainActivity.this, SettingsActivity.class);
        startActivity(settingsIntent);

    }
}
