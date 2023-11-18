package com.example.interestingtaste.Shared;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class SharePreferenceUnity {
  public static SharedPreferences preferences(Context context) {
    return PreferenceManager.getDefaultSharedPreferences(context);
  }

  public static Boolean hasSendToast(Context context) {
    return preferences(context).getBoolean("Login", false);
  }

  public static void setSendToast(Context context, Boolean bool) {
    preferences(context).edit().putBoolean("Login", bool).apply();
  }
}
