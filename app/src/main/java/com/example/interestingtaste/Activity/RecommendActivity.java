package com.example.interestingtaste.Activity;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.content.ContentValues.TAG;

import static java.util.Arrays.asList;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.location.Address;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewStub;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.interestingtaste.Dto.CuisinePlaceDto;
import com.example.interestingtaste.Dto.FoodDto;
import com.example.interestingtaste.Dto.UserDto;
import com.example.interestingtaste.Model.Food;
import com.example.interestingtaste.R;
import com.example.interestingtaste.Receiver.ConnectionReceiver;
import com.example.interestingtaste.Services.Food.FoodService;
import com.example.interestingtaste.Shared.DateFormatter;
import com.example.interestingtaste.databinding.ActivityRecommendBinding;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMapOptions;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;

import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.AutocompleteSessionToken;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

public class RecommendActivity extends AppCompatActivity
    implements AdapterView.OnItemSelectedListener, OnMapReadyCallback {

  private ActivityRecommendBinding activityRecommendBinding;

  private final String apiKey = "AIzaSyA8Ya0vgFvYHnxJWDUIcpwkH2TnRbRORho";

  private final String defaultDishImg =
      "https://firebasestorage.googleapis.com/v0/b/interestingtaste-9ec6d.appspot.com/o/food_placeholder.png?alt=media&token=0e022448-e824-474a-b055-a0804a3658f1";

  private SupportMapFragment mapFragment;
  private View mapPanel;

  private Marker marker;
  private GoogleMap map;

  private LatLng coordinates;
  private Uri imageUri;

  private String selectedCategory = "Asian";

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

  private ActivityResultLauncher<Intent> activityResultLauncher =
      registerForActivityResult(
          new ActivityResultContracts.StartActivityForResult(),
          new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult activityResult) {
              int result = activityResult.getResultCode();
              Intent data = activityResult.getData();
              if (result == RESULT_OK && data != null) {
                imageUri = data.getData();
                setFoodImg(imageUri, activityRecommendBinding.addImg);
              }
            }
          });

  private void openFileChosen() {
    Intent intent = new Intent();
    intent.setType("image/*");
    intent.setAction(Intent.ACTION_GET_CONTENT);
    activityResultLauncher.launch(intent);
  }

  private void setFoodImg(Uri uri, ImageButton imageButton) {
    Picasso.get().load(uri).placeholder(R.drawable.food_placeholder).into(imageButton);
  }

  private Boolean isValidForm(
      String foodName, String foodDes, String foodPrice, String storeName, String storeAddress) {
    if (foodName.length() > 15 || foodName.length() < 6) {
      return false;
    }
    if (foodDes.length() > 50 || foodDes.length() < 20) {
      return false;
    }
    if (foodPrice == null) {
      return false;
    } else {
      if (Double.parseDouble(foodPrice) == 0) {
        return false;
      }
    }
    if (storeName.length() > 15 || storeName.length() < 6) {
      return false;
    }

    if (storeAddress.isEmpty()) {
      return false;
    }
    return true;
  }

  private void showMap(Place place) {

    // It isn't possible to set a fragment's id programmatically so we set a tag instead and
    // search for it using that.
    mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentByTag("MAP");

    // We only create a fragment if it doesn't already exist.
    if (mapFragment == null) {
      mapPanel = ((ViewStub) findViewById(R.id.stub_map)).inflate();
      GoogleMapOptions mapOptions = new GoogleMapOptions();
      mapOptions.mapToolbarEnabled(false);

      // To programmatically add the map, we first create a SupportMapFragment.
      mapFragment = SupportMapFragment.newInstance(mapOptions);

      // Then we add it using a FragmentTransaction.
      getSupportFragmentManager()
          .beginTransaction()
          .add(R.id.confirmation_map, mapFragment, "MAP")
          .commit();
      mapFragment.getMapAsync(this);
    } else {
      updateMap(coordinates);
    }
  }

  private void updateMap(LatLng latLng) {
    marker.setPosition(latLng);
    map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 18f));
    if (mapPanel.getVisibility() == View.GONE) {
      mapPanel.setVisibility(View.VISIBLE);
    }
  }

  @Override
  public void onMapReady(@NonNull GoogleMap googleMap) {
    map = googleMap;
    try {
      // Customise the styling of the base map using a JSON object defined
      // in a string resource.
      boolean success =
          map.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.style_raw));

      if (!success) {
        Log.e(TAG, "Style parsing failed.");
      }
    } catch (Resources.NotFoundException e) {
      Log.e(TAG, "Can't find style. Error: ", e);
    }
    map.moveCamera(CameraUpdateFactory.newLatLngZoom(coordinates, 15f));
    marker = map.addMarker(new MarkerOptions().position(coordinates));
  }

  private void resetForm() {
    //    Picasso.get()
    //        .load(R.drawable.add)
    //        .placeholder(R.drawable.food_placeholder)
    //        .into(activityRecommendBinding.addImg);
    activityRecommendBinding.addImg.setImageResource(R.drawable.add);
    activityRecommendBinding.foodName.setText("");
    activityRecommendBinding.storeName.setText("");
    activityRecommendBinding.description.setText("");
    activityRecommendBinding.addressDisplay.setText("");
    activityRecommendBinding.foodPrice.setText(null);
    imageUri = null;
  }

  private void setUpBottomNavigation() {
    activityRecommendBinding.bottomNav.setSelectedItemId(R.id.addFood);
    activityRecommendBinding.bottomNav.setOnItemSelectedListener(
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
    selectedCategory = parent.getItemAtPosition(position).toString();
  }

  @Override
  public void onNothingSelected(AdapterView<?> parent) {}

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    activityRecommendBinding = ActivityRecommendBinding.inflate(getLayoutInflater());
    setUpBottomNavigation();
    setContentView(activityRecommendBinding.getRoot());

    FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

    activityRecommendBinding.addImg.setOnClickListener(
        view -> {
          openFileChosen();
        });

    activityRecommendBinding
        .backButton
        .getRoot()
        .setOnClickListener(
            view -> {
              startActivity(new Intent(this, MainActivity.class));
              finish();
            });
    //        Places.initializeWithNewPlacesApiEnabled(getApplicationContext(), apiKey);

    Places.initialize(getApplicationContext(), apiKey);
    Places.createClient(this);
    activityRecommendBinding.addressDisplay.setEnabled(false);
    AutocompleteSupportFragment autocompleteSupportFragment =
        (AutocompleteSupportFragment)
            getSupportFragmentManager().findFragmentById(R.id.autoCompleteAddress);
    autocompleteSupportFragment.setActivityMode(AutocompleteActivityMode.FULLSCREEN);
    autocompleteSupportFragment.setPlaceFields(
        asList(Place.Field.LAT_LNG, Place.Field.ADDRESS, Place.Field.NAME));
    AutocompleteSessionToken.newInstance();

    autocompleteSupportFragment.setOnPlaceSelectedListener(
        new PlaceSelectionListener() {
          @Override
          public void onError(@NonNull Status status) {}

          @Override
          public void onPlaceSelected(@NonNull Place place) {
            activityRecommendBinding.addressDisplay.setText(place.getAddress());
            coordinates = place.getLatLng();
            showMap(place);
          }
        });

    ArrayAdapter<CharSequence> adapter =
        ArrayAdapter.createFromResource(
            this, R.array.food_category, android.R.layout.simple_spinner_item);
    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    activityRecommendBinding.foodCategory.setAdapter(adapter);
    activityRecommendBinding.foodCategory.setOnItemSelectedListener(this);

    activityRecommendBinding.submitButton.setEnabled(false);

    EditText foodNameEditText = activityRecommendBinding.foodName;
    EditText storeNameEditText = activityRecommendBinding.storeName;
    EditText foodPriceEditText = activityRecommendBinding.foodPrice;
    EditText foodDesEditText = activityRecommendBinding.description;
    EditText addressDisplay = activityRecommendBinding.addressDisplay;

    TextWatcher textWatcher =
        new TextWatcher() {
          @Override
          public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

          @Override
          public void onTextChanged(CharSequence s, int start, int before, int count) {
            if (foodNameEditText.getText().toString().length() > 15
                || foodNameEditText.getText().toString().length() < 6) {
              activityRecommendBinding.foodNameErr.setText("6 - 15 characters");
              activityRecommendBinding.foodNameErr.setVisibility(View.VISIBLE);

            } else {
              activityRecommendBinding.foodNameErr.setVisibility(View.INVISIBLE);
            }

            if (storeNameEditText.getText().toString().length() > 15
                || storeNameEditText.getText().toString().length() < 6) {
              activityRecommendBinding.storeNameErr.setText("6 - 15 characters");
              activityRecommendBinding.storeNameErr.setVisibility(View.VISIBLE);

            } else {
              activityRecommendBinding.storeNameErr.setVisibility(View.INVISIBLE);
            }

            if (foodDesEditText.getText().toString().length() > 50
                || foodDesEditText.getText().toString().length() < 20) {
              activityRecommendBinding.foodDesErr.setText("20 - 50 characters");
              activityRecommendBinding.foodDesErr.setVisibility(View.VISIBLE);

            } else {
              activityRecommendBinding.foodDesErr.setVisibility(View.INVISIBLE);
            }

            if (foodPriceEditText.getText().toString().isEmpty()) {
              activityRecommendBinding.foodPriceErr.setText("Required");
              activityRecommendBinding.foodPriceErr.setVisibility(View.VISIBLE);

            } else {
              activityRecommendBinding.foodPriceErr.setVisibility(View.INVISIBLE);
            }
            if (addressDisplay.getText().toString().isEmpty()) {
              activityRecommendBinding.addressDisplayErr.setText("Address is required");
              activityRecommendBinding.addressDisplayErr.setVisibility(View.VISIBLE);

            } else {
              activityRecommendBinding.addressDisplayErr.setVisibility(View.INVISIBLE);
            }
          }

          @Override
          public void afterTextChanged(Editable s) {
            if (foodPriceEditText.getText().toString().isEmpty()) {
              activityRecommendBinding.foodPriceErr.setText("Required");
              activityRecommendBinding.foodPriceErr.setVisibility(View.VISIBLE);

            } else {
              activityRecommendBinding.foodPriceErr.setVisibility(View.INVISIBLE);
            }
            if (!isValidForm(
                foodNameEditText.getText().toString(),
                foodDesEditText.getText().toString(),
                foodPriceEditText.getText().toString(),
                storeNameEditText.getText().toString(),
                addressDisplay.getText().toString())) {
              activityRecommendBinding.submitButton.setEnabled(false);
            } else {
              activityRecommendBinding.submitButton.setEnabled(true);
            }
          }
        };

    foodNameEditText.addTextChangedListener(textWatcher);
    storeNameEditText.addTextChangedListener(textWatcher);
    foodDesEditText.addTextChangedListener(textWatcher);
    foodPriceEditText.addTextChangedListener(textWatcher);
    addressDisplay.addTextChangedListener(textWatcher);

    activityRecommendBinding.submitButton.setOnClickListener(
        view -> {
          String dishName = activityRecommendBinding.foodName.getText().toString();
          String storeName = activityRecommendBinding.storeName.getText().toString();
          String foodPrice = activityRecommendBinding.foodPrice.getText().toString();
          String foodDes = activityRecommendBinding.description.getText().toString();
          String storePosition = activityRecommendBinding.addressDisplay.getText().toString();

          UserDto author =
              UserDto.builder()
                  .id(currentUser.getUid())
                  .email(currentUser.getEmail())
                  .displayName(currentUser.getDisplayName())
                  .imgUrl(String.valueOf(currentUser.getPhotoUrl()))
                  .build();

          if (imageUri == null) {
            imageUri = Uri.parse(defaultDishImg);
          }

          Food food =
              Food.builder()
                  .author(author)
                  .destination(
                      CuisinePlaceDto.builder()
                          .displayName(storeName)
                          .displayPosition(storePosition)
                          .longitude(coordinates.longitude)
                          .latitude(coordinates.latitude)
                          .build())
                  .displayName(dishName)
                  .price(Double.parseDouble(foodPrice))
                  .category(selectedCategory)
                  .imageUrl(String.valueOf(imageUri))
                  .reviews(new ArrayList<>())
                  .description(foodDes)
                  .createdDate(new Date())
                  .updatedDate(new Date())
                  .build();
          FoodService foodService = FoodService.builder().build();
          foodService.createFood(this, food);
          resetForm();
        });
  }
}
