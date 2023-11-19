package com.example.interestingtaste.Activity;

import android.app.Dialog;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import com.example.interestingtaste.R;
import com.example.interestingtaste.Receiver.ConnectionReceiver;
import com.example.interestingtaste.databinding.ActivityWelcomeBinding;

public class WelcomeActivity extends AppCompatActivity {

  private ActivityWelcomeBinding activityWelcomeBinding;

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
    activityWelcomeBinding = ActivityWelcomeBinding.inflate(getLayoutInflater());
    setContentView(activityWelcomeBinding.getRoot());
    activityWelcomeBinding.toSignInButton.setOnClickListener(
        view -> {
          startActivity(new Intent(this, SignInActivity.class));
        });

    activityWelcomeBinding.toSignUpButton.setOnClickListener(
        view -> {
          startActivity(new Intent(this, SignUpActivity.class));
        });
    activityWelcomeBinding.toAboutUsButton.setOnClickListener(
        view -> {
          final Dialog dialog = new Dialog(this);
          dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
          dialog.setContentView(R.layout.about_us);
          Window reviewWindow = dialog.getWindow();
          if (reviewWindow == null) {
            return;
          }
          reviewWindow.setLayout(
              WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
          reviewWindow.setBackgroundDrawable(new ColorDrawable(Color.WHITE));
          //          WindowManager.LayoutParams winAttributes = reviewWindow.getAttributes();
          reviewWindow.setGravity(Gravity.CENTER);
          reviewWindow.setBackgroundDrawableResource(R.drawable.rounded_button);

          dialog.show();
        });
  }
}
