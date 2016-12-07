package com.paranoidandroid.journey.journeys.activities;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.paranoidandroid.journey.R;
import com.paranoidandroid.journey.databinding.ActivityJourneysBinding;
import com.paranoidandroid.journey.journeys.fragments.LogoutConfirmationDialogFragment;
import com.paranoidandroid.journey.journeys.fragments.MyJourneysListFragment;
import com.paranoidandroid.journey.legplanner.activities.PlannerActivity;
import com.paranoidandroid.journey.login.LoginActivity;
import com.paranoidandroid.journey.models.Journey;
import com.paranoidandroid.journey.network.FacebookClient;
import com.paranoidandroid.journey.support.SharedPreferenceUtils;
import com.paranoidandroid.journey.wizard.activities.WizardActivity;
import com.parse.ParseUser;

public class JourneysActivity extends AppCompatActivity implements
        LogoutConfirmationDialogFragment.OnLogoutListener,
        MyJourneysListFragment.OnJourneyActionListener {

    private ActivityJourneysBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupViews();
        setupListeners();

        // todo: add view pager and change out fragments programmatically
        if (savedInstanceState == null) {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.content, MyJourneysListFragment.newInstance());
            transaction.commit();
        }
    }

    private void setupViews() {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_journeys);
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
        FacebookClient.revokeAppPermissions();
        ParseUser.logOut();
        SharedPreferenceUtils.clearUserInfo(this);
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void onJourneySelected(Journey journey) {
        Intent intent = new Intent(this, PlannerActivity.class);
        intent.putExtra("journey_id", journey.getObjectId());
        startActivity(intent);
    }

    @Override
    public void onJourneyDeleted(Journey journey) {
        // Hack to ensure that FAB is visible if it has been hidden but there aren't enough items
        // remaining in the list to allow scrolling (which would show the FAB again).
        binding.fab.show();
    }

    @Override
    public void onCreateNewJourney() {
        Intent intent = new Intent(this, WizardActivity.class);
        startActivity(intent);
    }
}
