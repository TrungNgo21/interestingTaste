package com.example.interestingtaste.Activity;

import android.accounts.Account;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.interestingtaste.R;
import com.example.interestingtaste.Receiver.ConnectionReceiver;
import com.example.interestingtaste.Services.Food.FoodService;
import com.example.interestingtaste.databinding.ActivityMainBinding;
import com.google.android.libraries.places.api.Places;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.squareup.picasso.Picasso;

import org.checkerframework.checker.units.qual.C;

import java.util.List;
import java.util.Objects;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

  private ActivityMainBinding mainBinding;

  private final ConnectionReceiver connectionReceiver = new ConnectionReceiver();

  private String filterCate = "Asian";

  private void setUpBottomNavigation() {
    mainBinding.bottomNav.setSelectedItemId(R.id.foods);
    mainBinding.bottomNav.setOnItemSelectedListener(
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
  public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
    filterCate = parent.getItemAtPosition(position).toString();
    FoodService foodService = FoodService.builder().build();
    foodService.getQueryFood(
        MainActivity.this,
        mainBinding.allFood,
        mainBinding.shimmerView,
        mainBinding.nothingFound,
        filterCate,
        mainBinding.queryBox.searchBox.getText().toString());
  }

  @Override
  public void onNothingSelected(AdapterView<?> parent) {}

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

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    mainBinding = ActivityMainBinding.inflate(getLayoutInflater());
    // Retrieve the content view that renders the map.
    setContentView(mainBinding.getRoot());
    setUpBottomNavigation();
    mainBinding.nothingFound.setVisibility(View.INVISIBLE);

    FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
    Uri userPhoto = currentUser.getPhotoUrl();
    Picasso.get()
        .load(userPhoto)
        .placeholder(R.drawable.food_placeholder)
        .into(mainBinding.userImg);
    mainBinding.userEmail.setText(currentUser.getEmail());

    ArrayAdapter<CharSequence> adapter =
        ArrayAdapter.createFromResource(
            this, R.array.filter_category, android.R.layout.simple_spinner_item);
    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    mainBinding.queryBox.filterCategory.setAdapter(adapter);
    mainBinding.queryBox.filterCategory.setOnItemSelectedListener(this);

    EditText searchEditText = mainBinding.queryBox.searchBox;

    TextWatcher textWatcher =
        new TextWatcher() {
          @Override
          public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

          @Override
          public void onTextChanged(CharSequence s, int start, int before, int count) {}

          @Override
          public void afterTextChanged(Editable s) {
            FoodService foodService = FoodService.builder().build();
            foodService.getQueryFood(
                MainActivity.this,
                mainBinding.allFood,
                mainBinding.shimmerView,
                mainBinding.nothingFound,
                filterCate,
                searchEditText.getText().toString());
          }
        };

    searchEditText.addTextChangedListener(textWatcher);

    Toast.makeText(
            getApplicationContext(),
            "Welcome " + FirebaseAuth.getInstance().getCurrentUser().getEmail(),
            Toast.LENGTH_SHORT)
        .show();

    mainBinding.accountPlaceHolder.setOnClickListener(
        view -> {
          startActivity(new Intent(this, AccountActivity.class));
        });
    mainBinding
        .signOutButton
        .getRoot()
        .setOnClickListener(
            view -> {
              FirebaseAuth.getInstance().signOut();
              startActivity(new Intent(this, WelcomeActivity.class));
            });
    mainBinding.shimmerView.setVisibility(View.VISIBLE);
    mainBinding.shimmerView.startShimmer();
    mainBinding.allFood.setVisibility(View.INVISIBLE);
    FoodService foodService = FoodService.builder().build();
    foodService.getAllFood(MainActivity.this, mainBinding.allFood, mainBinding.shimmerView);
  }
}
