package com.example.interestingtaste.Shared;

import android.text.TextUtils;
import android.util.Patterns;

public class AuthValidator {
  public static boolean isValidEmail(String email) {
    return !TextUtils.isEmpty(email) && Patterns.EMAIL_ADDRESS.matcher(email).matches();
  }

  public static boolean isValidPass(String password) {
    return password.length() > 5;
  }
}
