package com.example.interestingtaste.Repository;

import static android.content.ContentValues.TAG;

import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;

import com.example.interestingtaste.Dto.UserDto;
import com.example.interestingtaste.Services.FirebaseCallback;
import com.example.interestingtaste.Shared.Result;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FirebaseAuthRepository {

  private final FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

  private final FirebaseStorage storage = FirebaseStorage.getInstance();
  ;
  private final String defaultUserImg =
      "https://firebasestorage.googleapis.com/v0/b/interestingtaste-9ec6d.appspot.com/o/default.jpg?alt=media&token=c6b84bd9-f3b8-48fc-ad68-d271da090338";

  private FirebaseUser firebaseUser;

  public void createUser(
      String email, String password, Uri imgUri, final FirebaseCallback<Result<UserDto>> callback) {
    firebaseAuth
        .createUserWithEmailAndPassword(email, password)
        .addOnCompleteListener(
            task -> {
              if (task.isSuccessful()) {
                // Sign in success, update UI with the signed-in user's information
                firebaseUser = firebaseAuth.getCurrentUser();
                UserProfileChangeRequest profileUpdates =
                    new UserProfileChangeRequest.Builder().build();
                if (imgUri == null) {
                  profileUpdates =
                      new UserProfileChangeRequest.Builder()
                          .setDisplayName("Testing User")
                          .setPhotoUri(Uri.parse(defaultUserImg))
                          .build();

                  firebaseUser
                      .updateProfile(profileUpdates)
                      .addOnCompleteListener(
                          task1 -> {
                            if (task1.isSuccessful()) {
                              UserDto currentUser =
                                  UserDto.builder()
                                      .id(firebaseUser.getUid())
                                      .email(firebaseUser.getEmail())
                                      .displayName(firebaseUser.getDisplayName())
                                      .build();

                              callback.callbackRes(new Result.Success<>(currentUser));
                            }
                          });

                } else {
                  StorageReference storageReference =
                      storage.getReference().child(String.valueOf(System.currentTimeMillis()));
                  UploadTask uploadTask = storageReference.putFile(imgUri);
                  uploadTask
                      .continueWithTask(
                          new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                            @Override
                            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task)
                                throws Exception {
                              if (!task.isSuccessful()) {
                                throw task.getException();
                              }
                              return storageReference.getDownloadUrl();
                            }
                          })
                      .addOnCompleteListener(
                          new OnCompleteListener<Uri>() {
                            @Override
                            public void onComplete(@NonNull Task<Uri> task) {
                              if (task.isSuccessful()) {
                                Uri downloadUri = task.getResult();
                                UserProfileChangeRequest profileUpdates;
                                profileUpdates =
                                    new UserProfileChangeRequest.Builder()
                                        .setDisplayName("Testing User")
                                        .setPhotoUri(downloadUri)
                                        .build();
                                firebaseUser
                                    .updateProfile(profileUpdates)
                                    .addOnCompleteListener(
                                        task1 -> {
                                          if (task1.isSuccessful()) {
                                            UserDto currentUser =
                                                UserDto.builder()
                                                    .id(firebaseUser.getUid())
                                                    .email(firebaseUser.getEmail())
                                                    .displayName(firebaseUser.getDisplayName())
                                                    .build();

                                            callback.callbackRes(new Result.Success<>(currentUser));
                                          }
                                        });
                              } else {
                                Log.d(TAG, "The bug is that " + task.getException().getMessage());
                                callback.callbackRes(new Result.Error(task.getException()));
                              }
                            }
                          });
                }
              } else {
                Log.d(TAG, "The bug is that " + task.getException().getMessage());
                callback.callbackRes(new Result.Error(task.getException()));
              }
            });
  }

  public void signInUser(
      String email, String password, final FirebaseCallback<Result<UserDto>> callback) {
    firebaseAuth
        .signInWithEmailAndPassword(email, password)
        .addOnCompleteListener(
            task -> {
              if (task.isSuccessful()) {
                // Sign in success, update UI with the signed-in user's information
                firebaseUser = firebaseAuth.getCurrentUser();
                UserDto currentUser =
                    UserDto.builder()
                        .id(firebaseUser.getUid())
                        .email(firebaseUser.getEmail())
                        .displayName(firebaseUser.getDisplayName())
                        .imgUrl(firebaseAuth.getCurrentUser().getPhotoUrl().toString())
                        .build();

                callback.callbackRes(new Result.Success<>(currentUser));
              } else {
                Log.d(TAG, "The bug is that " + task.getException().getMessage());
                callback.callbackRes(new Result.Error(task.getException()));
              }
            });
  }
}
