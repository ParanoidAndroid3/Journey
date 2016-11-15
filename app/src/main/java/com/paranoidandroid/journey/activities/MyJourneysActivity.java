package com.paranoidandroid.journey.activities;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.paranoidandroid.journey.fragments.LogoutConfirmationDialogFragment;
import com.paranoidandroid.journey.fragments.MyJourneysListFragment;
import com.paranoidandroid.journey.R;
import com.paranoidandroid.journey.databinding.ActivityMyJourneysBinding;
import com.paranoidandroid.journey.models.Journey;
import com.parse.ParseUser;

public class MyJourneysActivity extends AppCompatActivity implements
        LogoutConfirmationDialogFragment.OnLogoutListener,
        MyJourneysListFragment.OnJourneySelectedListener {

    private ActivityMyJourneysBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupViews();
        setupListeners();

        if (savedInstanceState == null) {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.content, MyJourneysListFragment.newInstance());
            transaction.commit();
        }
    }

    private void setupViews() {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_my_journeys);
        setSupportActionBar(binding.toolbar);
    }

    private void setupListeners() {
        binding.fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onCreateNewJourney();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_logout:
                confirmLogout();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void confirmLogout() {
        LogoutConfirmationDialogFragment fragment =
                LogoutConfirmationDialogFragment.newInstance();
        fragment.show(getSupportFragmentManager(), "confirm_logout");
    }

    @Override
    public void onLogout() {
        ParseUser.logOut();
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void onJourneySelected(Journey journey) {
        String name = journey.getName();
        Toast.makeText(this, "Selected " + name, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onCreateNewJourney() {
        Toast.makeText(this, "Make a new Journey", Toast.LENGTH_SHORT).show();
    }
}
