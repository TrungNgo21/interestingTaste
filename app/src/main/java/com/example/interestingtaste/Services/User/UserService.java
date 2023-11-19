package com.example.interestingtaste.Services.User;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.interestingtaste.Activity.MainActivity;
import com.example.interestingtaste.Activity.SignInActivity;
import com.example.interestingtaste.Activity.SignUpActivity;

import com.example.interestingtaste.Dto.UserDto;
import com.example.interestingtaste.R;
import com.example.interestingtaste.Repository.FirebaseAuthRepository;
import com.example.interestingtaste.Services.FirebaseCallback;
import com.example.interestingtaste.Shared.Result;

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
        new FirebaseCallback<Result<UserDto>>() {
          @Override
          public void callbackListRes(List<Result<UserDto>> listT) {}

          @Override
          public void callbackRes(Result<UserDto> result) {
            if (result instanceof Result.Error) {
              ProgressBar progressBar =
                  (ProgressBar) ((Activity) context).findViewById(R.id.progressBar);
              TextView error =
                  (TextView) ((Activity) context).findViewById(R.id.authenticatedFailed);
              progressBar.setVisibility(View.INVISIBLE);
              error.setVisibility(View.VISIBLE);
              SignInActivity activity = (SignInActivity) context;
              activity.setUiComponentDisabled(true);
            } else {
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
        new FirebaseCallback<Result<UserDto>>() {
          @Override
          public void callbackListRes(List<Result<UserDto>> listT) {}

          @Override
          public void callbackRes(Result<UserDto> result) {
            if (result instanceof Result.Error) {
              ProgressBar progressBar =
                  (ProgressBar) ((Activity) context).findViewById(R.id.progressBarSignUp);
              TextView error = (TextView) ((Activity) context).findViewById(R.id.createdFailed);
              progressBar.setVisibility(View.INVISIBLE);
              if (((Result.Error) result)
                  .getError()
                  .toString()
                  .contains("The email address is already in use by another account")) {
                error.setText("Email already exist!");
              }
              error.setVisibility(View.VISIBLE);
              SignUpActivity activity = (SignUpActivity) context;
              activity.setUiComponentDisabled(true);
            } else {

              Intent intent = new Intent(context, MainActivity.class);
              context.startActivity(intent);
            }
          }
        });
  }
}
