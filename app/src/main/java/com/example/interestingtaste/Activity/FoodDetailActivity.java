package com.example.interestingtaste.Activity;

import static android.content.ContentValues.TAG;

import android.app.Dialog;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.interestingtaste.Dto.ReviewDto;
import com.example.interestingtaste.Dto.UserDto;
import com.example.interestingtaste.Model.Review;
import com.example.interestingtaste.R;
import com.example.interestingtaste.Receiver.ConnectionReceiver;
import com.example.interestingtaste.Services.Food.FoodService;
import com.example.interestingtaste.Services.Food.ReviewService;
import com.example.interestingtaste.databinding.ActivityFoodDetailBinding;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.squareup.picasso.Picasso;

import java.util.Date;

public class FoodDetailActivity extends AppCompatActivity
    implements AdapterView.OnItemSelectedListener {

  private ActivityFoodDetailBinding activityFoodDetailBinding;

  private String rating = "1";

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
    //    activityFoodDetailBinding.bottomNav.setSelectedItemId(R.id.account);
    activityFoodDetailBinding.bottomNav.setOnItemSelectedListener(
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
    rating = parent.getItemAtPosition(position).toString();
  }

  @Override
  public void onNothingSelected(AdapterView<?> parent) {}

  private Boolean isValidFeedback(String feedback) {
    if (feedback.length() > 50 || feedback.length() < 10) {
      return false;
    }
    return true;
  }

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    activityFoodDetailBinding = ActivityFoodDetailBinding.inflate(getLayoutInflater());
    setContentView(activityFoodDetailBinding.getRoot());

    setUpBottomNavigation();
    FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

    String foodId = getIntent().getStringExtra("foodId");
    String createdDate = getIntent().getStringExtra("updatedDate");
    String foodImgUrl = getIntent().getStringExtra("image");
    String foodName = getIntent().getStringExtra("foodName");
    String foodRating = getIntent().getStringExtra("foodRating");
    String foodDes = getIntent().getStringExtra("des");
    String storeName = getIntent().getStringExtra("storeName");
    String storeLocation = getIntent().getStringExtra("storeLocation");
    String authorImg = getIntent().getStringExtra("authorImg");
    String authorEmail = getIntent().getStringExtra("authorDisplay");
    String foodPrice = getIntent().getStringExtra("foodPrice");
    String latitude = getIntent().getStringExtra("latitude");
    String longitude = getIntent().getStringExtra("longitude");

    Picasso.get()
        .load(foodImgUrl)
        .placeholder(R.drawable.food_placeholder)
        .into(activityFoodDetailBinding.foodDetailImg);
    Picasso.get()
        .load(authorImg)
        .placeholder(R.drawable.food_placeholder)
        .into(activityFoodDetailBinding.authorImg);
    activityFoodDetailBinding.foodDetailName.setText(foodName);

    if (foodRating.equals("NaN")) {
      activityFoodDetailBinding.foodDetailRating.setText("0.0");
    } else {
      activityFoodDetailBinding.foodDetailRating.setText(foodRating);
    }
    activityFoodDetailBinding.foodDetailDes.setText(foodDes);
    activityFoodDetailBinding.storeDetailName.setText(storeName);
    activityFoodDetailBinding.storePosition.setText(storeLocation);
    activityFoodDetailBinding.foodDate.setText(createdDate);
    activityFoodDetailBinding.foodPrice.setText(foodPrice);
    activityFoodDetailBinding.authorDisplay.setText(authorEmail);

    activityFoodDetailBinding.addReview.setOnClickListener(
        view -> {
          final Dialog dialog = new Dialog(this);
          dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
          dialog.setContentView(R.layout.add_review_dialog);
          Window reviewWindow = dialog.getWindow();
          if (reviewWindow == null) {
            return;
          }
          reviewWindow.setLayout(
              WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
          reviewWindow.setBackgroundDrawable(new ColorDrawable(Color.WHITE));
          //          WindowManager.LayoutParams winAttributes = reviewWindow.getAttributes();
          reviewWindow.setGravity(Gravity.BOTTOM);

          EditText feedback = reviewWindow.findViewById(R.id.feedback);
          Spinner spinner = reviewWindow.findViewById(R.id.feedbackStarCate);
          Button saveBtn = reviewWindow.findViewById(R.id.saveButton);
          Button cancelBtn = reviewWindow.findViewById(R.id.cancelButton);
          TextView feedBackErr = reviewWindow.findViewById(R.id.feedbackErr);

          cancelBtn.setOnClickListener(
              new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                  dialog.dismiss();
                }
              });

          saveBtn.setEnabled(false);

          TextWatcher textWatcher =
              new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                  if (!isValidFeedback(feedback.getText().toString())) {
                    feedBackErr.setText("10-50 characters");
                    feedBackErr.setVisibility(View.VISIBLE);
                  } else {
                    feedBackErr.setVisibility(View.INVISIBLE);
                  }
                }

                @Override
                public void afterTextChanged(Editable s) {
                  if (!isValidFeedback(feedback.getText().toString())) {
                    saveBtn.setEnabled(false);
                  } else {
                    saveBtn.setEnabled(true);
                  }
                }
              };

          feedback.addTextChangedListener(textWatcher);
          ArrayAdapter<CharSequence> adapter =
              ArrayAdapter.createFromResource(
                  this, R.array.start_category, android.R.layout.simple_spinner_item);
          adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
          spinner.setAdapter(adapter);
          spinner.setOnItemSelectedListener(this);

          saveBtn.setOnClickListener(
              new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                  UserDto userDto =
                      UserDto.builder()
                          .displayName(firebaseUser.getDisplayName())
                          .email(firebaseUser.getEmail())
                          .imgUrl(firebaseUser.getPhotoUrl().toString())
                          .build();
                  Review review =
                      Review.builder()
                          .userId(userDto)
                          .rating(Double.parseDouble(rating))
                          .feedBack(feedback.getText().toString())
                          .createdDate(new Date())
                          .updatedDate(new Date())
                          .build();
                  ReviewService reviewService = ReviewService.builder().build();
                  reviewService.addReview(review, foodId, FoodDetailActivity.this, dialog);
                }
              });
          dialog.show();
        });

    activityFoodDetailBinding
        .backButton
        .getRoot()
        .setOnClickListener(
            view -> {
              startActivity(new Intent(this, MainActivity.class));
              finish();
            });

    activityFoodDetailBinding.storePosition.setOnClickListener(
        view -> {
          Intent intent = new Intent(FoodDetailActivity.this, StoreLocationActivity.class);
          intent.putExtra("latitude", latitude);
          intent.putExtra("longitude", longitude);
          intent.putExtra("storeName", storeName);
          intent.putExtra("storeAddress", storeLocation);
          startActivity(intent);
        });

    FoodService foodService = FoodService.builder().build();
    activityFoodDetailBinding.foodDetailReviews.setVisibility(View.INVISIBLE);
    activityFoodDetailBinding.reviewShimmer.setVisibility(View.VISIBLE);
    activityFoodDetailBinding.reviewShimmer.startShimmer();
    foodService.getOneFood(
        FoodDetailActivity.this,
        activityFoodDetailBinding.foodDetailReviews,
        activityFoodDetailBinding.reviewShimmer,
        foodId);
  }
}
