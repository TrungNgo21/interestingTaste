package com.example.interestingtaste.Services.User;

import static android.content.ContentValues.TAG;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.interestingtaste.Activity.MainActivity;
import com.example.interestingtaste.Activity.SignInActivity;
import com.example.interestingtaste.Activity.SignUpActivity;

import com.example.interestingtaste.Dto.UserDto;
import com.example.interestingtaste.Model.User;
import com.example.interestingtaste.R;
import com.example.interestingtaste.Repository.FirebaseAuthRepository;
import com.example.interestingtaste.Services.FirebaseCallback;
import com.example.interestingtaste.Shared.SharePreferenceUnity;
import com.google.android.gms.common.util.SharedPreferencesUtils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Builder
@AllArgsConstructor
@Data
public class UserService {
  private final FirebaseAuthRepository firebaseAuthRepository =
      FirebaseAuthRepository.builder().build();
  private UserDto currentUser;

  public void signIn(String email, String password, Context context) {
    firebaseAuthRepository.signInUser(
        email,
        password,
        new FirebaseCallback<UserDto>() {
          @Override
          public void callbackListRes(List<UserDto> listT) {}

          @Override
          public void callbackRes(UserDto userDto) {
            if (userDto == null) {
              ProgressBar progressBar =
                  (ProgressBar) ((Activity) context).findViewById(R.id.progressBar);
              TextView error =
                  (TextView) ((Activity) context).findViewById(R.id.authenticatedFailed);
              progressBar.setVisibility(View.INVISIBLE);
              error.setVisibility(View.VISIBLE);
              SignInActivity activity = (SignInActivity) context;
              activity.setUiComponentDisabled(true);
            } else {
              currentUser = userDto;
              Intent intent = new Intent(context, MainActivity.class);
              context.startActivity(intent);
            }
          }
        });
  }

  public void signUp(String email, String password, Uri imageUri, Context context) {
    firebaseAuthRepository.createUser(
        email,
        password,
        imageUri,
        new FirebaseCallback<UserDto>() {
          @Override
          public void callbackListRes(List<UserDto> listT) {}

          @Override
          public void callbackRes(UserDto userDto) {
            if (userDto == null) {
              ProgressBar progressBar =
                  (ProgressBar) ((Activity) context).findViewById(R.id.progressBarSignUp);
              TextView error = (TextView) ((Activity) context).findViewById(R.id.createdFailed);
              progressBar.setVisibility(View.INVISIBLE);
              error.setVisibility(View.VISIBLE);
              SignUpActivity activity = (SignUpActivity) context;
              activity.setUiComponentDisabled(true);
            } else {
              currentUser = userDto;
              Intent intent = new Intent(context, MainActivity.class);
              context.startActivity(intent);
            }
          }
        });
  }
}
