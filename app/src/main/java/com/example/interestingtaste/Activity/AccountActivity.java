package com.example.interestingtaste.Activity;

import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import com.example.interestingtaste.R;
import com.example.interestingtaste.Receiver.ConnectionReceiver;
import com.example.interestingtaste.databinding.ActivityAccountBinding;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.squareup.picasso.Picasso;

public class AccountActivity extends AppCompatActivity {

  private ActivityAccountBinding activityAccountBinding;

  private final ConnectionReceiver connectionReceiver = new ConnectionReceiver();

  @Override
  protected void onStart() {
    IntentFilter intentFilter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
    registerReceiver(connectionReceiver, intentFilter);
    super.onStart();
  }

  @Override
  protected void onStop() {
    unregisterReceiver(connectionReceiver);
    super.onStop();
  }

  private void setUpBottomNavigation() {
    activityAccountBinding.bottomNav.setSelectedItemId(R.id.account);
    activityAccountBinding.bottomNav.setOnItemSelectedListener(
        new NavigationBarView.OnItemSelectedListener() {
          @Override
          public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            Integer itemId = item.getItemId();
            if (itemId == R.id.foods) {
              startActivity(new Intent(getApplicationContext(), MainActivity.class));
              overridePendingTransition(0, 0);
              return true;
            } else if (itemId == R.id.addFood) {
              startActivity(new Intent(getApplicationContext(), RecommendActivity.class));
              overridePendingTransition(0, 0);
              return true;
            } else if (itemId == R.id.account) {
              startActivity(new Intent(getApplicationContext(), AccountActivity.class));
              overridePendingTransition(0, 0);
              return true;
            }
            return false;
          }
        });
  }

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    activityAccountBinding = ActivityAccountBinding.inflate(getLayoutInflater());
    setUpBottomNavigation();

    activityAccountBinding.logoutButton.setOnClickListener(
        view -> {
          FirebaseAuth.getInstance().signOut();
          startActivity(new Intent(this, WelcomeActivity.class));
          finish();
        });

    FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

    Picasso.get()
        .load(currentUser.getPhotoUrl())
        .placeholder(R.drawable.food_placeholder)
        .into(activityAccountBinding.userImg);

    activityAccountBinding
        .backButton
        .getRoot()
        .setOnClickListener(
            view -> {
              finish();
            });
    activityAccountBinding.displayName.setText(currentUser.getDisplayName());
    activityAccountBinding.userEmail.setText(currentUser.getEmail());

    setContentView(activityAccountBinding.getRoot());
  }
}
