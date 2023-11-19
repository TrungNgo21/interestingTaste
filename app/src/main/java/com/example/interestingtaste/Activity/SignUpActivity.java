package com.example.interestingtaste.Activity;

import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContract;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import com.example.interestingtaste.R;
import com.example.interestingtaste.Receiver.ConnectionReceiver;
import com.example.interestingtaste.Services.User.UserService;
import com.example.interestingtaste.Shared.AuthValidator;
import com.example.interestingtaste.databinding.ActivitySignUpBinding;
import com.google.android.material.textfield.TextInputEditText;
import com.squareup.picasso.Picasso;

public class SignUpActivity extends AppCompatActivity {
  private ActivitySignUpBinding activitySignUpBinding;
  private Uri imageUri;

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
                setUserImg(imageUri, activitySignUpBinding.addImg);
              }
            }
          });

  private void openFileChosen() {
    Intent intent = new Intent();
    intent.setType("image/*");
    intent.setAction(Intent.ACTION_GET_CONTENT);
    activityResultLauncher.launch(intent);
  }

  private void setUserImg(Uri uri, ImageButton imageButton) {
    Picasso.get().load(uri).into(imageButton);
  }

  public void setUiComponentDisabled(Boolean isEnabled) {
    activitySignUpBinding.registerButton.setEnabled(isEnabled);
    activitySignUpBinding.emailSignUp.setEnabled(isEnabled);
    activitySignUpBinding.passwordSignUp.setEnabled(isEnabled);
  }

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    activitySignUpBinding = ActivitySignUpBinding.inflate(getLayoutInflater());
    setContentView(activitySignUpBinding.getRoot());

    TextInputEditText registeredEmail = activitySignUpBinding.emailSignUp;
    TextInputEditText registeredPass = activitySignUpBinding.passwordSignUp;
    Button registeredButton = activitySignUpBinding.registerButton;
    TextView emailErr = activitySignUpBinding.emailError;
    TextView passErr = activitySignUpBinding.passError;

    emailErr.setVisibility(View.INVISIBLE);
    passErr.setVisibility(View.INVISIBLE);
    activitySignUpBinding.progressBarSignUp.setVisibility(View.INVISIBLE);
    activitySignUpBinding.createdFailed.setVisibility(View.INVISIBLE);

    activitySignUpBinding.toSignInButton.setOnClickListener(
        view -> {
          startActivity(new Intent(this, SignInActivity.class));
          finish();
        });
    activitySignUpBinding
        .backButton
        .getRoot()
        .setOnClickListener(
            view -> {
              startActivity(new Intent(this, SignInActivity.class));
              finish();
            });

    registeredButton.setEnabled(false);

    activitySignUpBinding.addImg.setOnClickListener(
        view -> {
          openFileChosen();
        });

    TextWatcher onTextInputListener =
        new TextWatcher() {
          @Override
          public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            if (registeredEmail.getText().toString().isEmpty()
                || registeredPass.getText().toString().isEmpty()) {
              registeredButton.setEnabled(false);
            } else {
              registeredButton.setEnabled(true);
            }
          }

          @Override
          public void onTextChanged(CharSequence s, int start, int before, int count) {
            if (AuthValidator.isValidEmail(registeredEmail.getText().toString().trim())
                && AuthValidator.isValidPass(registeredPass.getText().toString())) {
              emailErr.setVisibility(View.INVISIBLE);
              passErr.setVisibility(View.INVISIBLE);
              registeredButton.setEnabled(true);
            } else {
              if (!AuthValidator.isValidEmail(registeredEmail.getText().toString().trim())) {
                emailErr.setVisibility(View.VISIBLE);
              } else {
                emailErr.setVisibility(View.INVISIBLE);
              }
              if (!AuthValidator.isValidPass(registeredPass.getText().toString())) {
                passErr.setVisibility(View.VISIBLE);
              } else {
                passErr.setVisibility(View.INVISIBLE);
              }

              registeredButton.setEnabled(false);
            }
          }

          @Override
          public void afterTextChanged(Editable s) {
            if (AuthValidator.isValidEmail(registeredEmail.getText().toString().trim())
                && AuthValidator.isValidPass(registeredPass.getText().toString())) {
              emailErr.setVisibility(View.INVISIBLE);
              passErr.setVisibility(View.INVISIBLE);
              registeredButton.setEnabled(true);
            } else {
              if (!AuthValidator.isValidEmail(registeredEmail.getText().toString().trim())) {
                emailErr.setVisibility(View.VISIBLE);
              } else {
                emailErr.setVisibility(View.INVISIBLE);
              }
              if (!AuthValidator.isValidPass(registeredPass.getText().toString())) {
                passErr.setVisibility(View.VISIBLE);
              } else {
                passErr.setVisibility(View.INVISIBLE);
              }

              registeredButton.setEnabled(false);
            }
          }
        };

    registeredPass.addTextChangedListener(onTextInputListener);
    registeredEmail.addTextChangedListener(onTextInputListener);

    UserService userService = UserService.builder().build();

    registeredButton.setOnClickListener(
        view -> {
          activitySignUpBinding.progressBarSignUp.setVisibility(view.VISIBLE);
          setUiComponentDisabled(false);
          activitySignUpBinding.createdFailed.setVisibility(View.INVISIBLE);
          userService.signUp(
              registeredEmail.getText().toString().trim(),
              registeredPass.getText().toString(),
              imageUri,
              this);
        });
  }
}
