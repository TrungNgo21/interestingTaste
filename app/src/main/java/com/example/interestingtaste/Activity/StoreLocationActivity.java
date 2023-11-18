package com.example.interestingtaste.Activity;

import android.content.IntentFilter;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.util.Log;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import com.example.interestingtaste.R;
import com.example.interestingtaste.Receiver.ConnectionReceiver;
import com.example.interestingtaste.databinding.ActivityStoreLocationBinding;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.MarkerOptions;

public class StoreLocationActivity extends AppCompatActivity implements OnMapReadyCallback {

  private ActivityStoreLocationBinding activityStoreLocationBinding;

  private GoogleMap map;
  private Double latitude;
  private Double longitude;

  private String address;

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

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    activityStoreLocationBinding = ActivityStoreLocationBinding.inflate(getLayoutInflater());
    setContentView(activityStoreLocationBinding.getRoot());

    SupportMapFragment mapFragment =
        (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
    mapFragment.getMapAsync(this);
    String storeName = getIntent().getStringExtra("storeName");
    String storeLocation = getIntent().getStringExtra("storeAddress");
    address = getIntent().getStringExtra("storeAddress");
    activityStoreLocationBinding.storeAddress.setText(storeLocation);

    activityStoreLocationBinding.storeName.setText(storeName);
    activityStoreLocationBinding
        .backButton
        .getRoot()
        .setOnClickListener(
            view -> {
              finish();
            });
  }

  @Override
  public void onMapReady(@NonNull GoogleMap googleMap) {

    latitude = Double.parseDouble(getIntent().getStringExtra("latitude"));
    longitude = Double.parseDouble(getIntent().getStringExtra("longitude"));

    try {
      // Customise the styling of the base map using a JSON object defined
      // in a string resource.
      boolean success =
          googleMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.style_raw));

      if (!success) {
        Log.e("Google map", "Style parsing failed.");
      }
    } catch (Resources.NotFoundException e) {
      Log.e("Google map", "Can't find style. Error: ", e);
    }

    LatLng storeLocation = new LatLng(latitude, longitude);
    googleMap.addMarker(new MarkerOptions().position(storeLocation).title(address));
    googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(storeLocation, 15f));
    googleMap.getUiSettings().setZoomControlsEnabled(true);
  }
}
