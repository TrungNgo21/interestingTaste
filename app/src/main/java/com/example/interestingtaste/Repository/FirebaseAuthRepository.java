package com.example.interestingtaste.Repository;

import static android.content.ContentValues.TAG;

import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;

import com.example.interestingtaste.Dto.UserDto;
import com.example.interestingtaste.Model.User;
import com.example.interestingtaste.R;
import com.example.interestingtaste.Services.FirebaseCallback;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.List;

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
      String email, String password, Uri imgUri, final FirebaseCallback<UserDto> callback) {
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
                          .setDisplayName("User Name")
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

                              callback.callbackRes(currentUser);
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

                              // Continue with the task to get the download URL
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
                                        .setDisplayName("User Name")
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

                                            callback.callbackRes(currentUser);
                                          }
                                        });
                              } else {
                                callback.callbackRes(null);
                              }
                            }
                          });
                }
              } else {
                callback.callbackRes(null);
              }
            });
  }

  public void signInUser(String email, String password, final FirebaseCallback<UserDto> callback) {
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

                callback.callbackRes(currentUser);
              } else {
                // If sign in fails, display a message to the user.
                Log.d(TAG, "The bug is that" + task.getException().getMessage());
                callback.callbackRes(null);
              }
            });
  }
}
