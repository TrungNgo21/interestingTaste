package com.example.interestingtaste.Activity;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import com.example.interestingtaste.R;
import com.example.interestingtaste.Receiver.ConnectionReceiver;
import com.example.interestingtaste.Services.User.UserService;
import com.example.interestingtaste.databinding.ActivityMainBinding;
import com.example.interestingtaste.databinding.ActivitySignInBinding;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.checkerframework.checker.units.qual.C;

public class SignInActivity extends AppCompatActivity {
  private ActivitySignInBinding activitySignInBinding;

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

  public void setUiComponentDisabled(Boolean isEnabled) {
    activitySignInBinding.loginButton.setEnabled(isEnabled);
    activitySignInBinding.email.setEnabled(isEnabled);
    activitySignInBinding.password.setEnabled(isEnabled);
  }

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    activitySignInBinding = ActivitySignInBinding.inflate(getLayoutInflater());
    setContentView(activitySignInBinding.getRoot());
    FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

    TextInputEditText email = activitySignInBinding.email;
    TextInputEditText password = activitySignInBinding.password;
    activitySignInBinding.loginButton.setEnabled(false);

    activitySignInBinding.toSignUpButton.setOnClickListener(
        view -> {
          startActivity(new Intent(this, SignUpActivity.class));
          finish();
        });

    TextWatcher onTextChangedListener =
        new TextWatcher() {
          @Override
          public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            if (email.getText().toString().isEmpty() || password.getText().toString().isEmpty()) {
              activitySignInBinding.loginButton.setEnabled(false);
            } else {
              activitySignInBinding.loginButton.setEnabled(true);
            }
          }

          @Override
          public void onTextChanged(CharSequence s, int start, int before, int count) {}

          @Override
          public void afterTextChanged(Editable s) {
            if (email.getText().toString().isEmpty() || password.getText().toString().isEmpty()) {
              activitySignInBinding.loginButton.setEnabled(false);
            } else {
              activitySignInBinding.loginButton.setEnabled(true);
            }
          }
        };

    email.addTextChangedListener(onTextChangedListener);
    password.addTextChangedListener(onTextChangedListener);

    activitySignInBinding.progressBar.setVisibility(View.INVISIBLE);
    activitySignInBinding.authenticatedFailed.setVisibility(View.INVISIBLE);

    if (currentUser != null) {
      startActivity(new Intent(getApplicationContext(), MainActivity.class));

      finish();
    } else {
      UserService userService = UserService.builder().build();
      activitySignInBinding.loginButton.setOnClickListener(
          view -> {
            String inputEmail = email.getText().toString();
            String inputPass = password.getText().toString();
            activitySignInBinding.progressBar.setVisibility(View.VISIBLE);
            activitySignInBinding.authenticatedFailed.setVisibility(View.INVISIBLE);
            setUiComponentDisabled(false);
            userService.signIn(inputEmail, inputPass, SignInActivity.this);
          });
      //

    }
  }
}
