package com.example.interestingtaste.Receiver;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import com.example.interestingtaste.R;

public class ConnectionReceiver extends BroadcastReceiver {
  static boolean isOnline = true;

  private static boolean getConnectivityStatusString(Context context) {
    ConnectivityManager cm =
        (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
    NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
    return activeNetwork != null;
  }

  @Override
  public void onReceive(Context context, Intent intent) {
    isOnline = getConnectivityStatusString(context);
    final Dialog dialog = new Dialog(context);
    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
    dialog.setContentView(R.layout.no_network);
    Window noNetwork = dialog.getWindow();
    if (noNetwork == null) {
      return;
    }
    noNetwork.setLayout(
        WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT);
    noNetwork.setBackgroundDrawable(new ColorDrawable(Color.WHITE));
    dialog.setCancelable(false);

    Button button = dialog.findViewById(R.id.retryButton);
    button.setOnClickListener(
        view -> {
          dialog.dismiss();
          onReceive(context, intent);
        });

    if (!isOnline) {
      dialog.show();
    } else {
      dialog.dismiss();
    }
  }
}
