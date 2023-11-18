package com.example.interestingtaste.Shared;

import android.content.ContentResolver;
import android.content.Context;
import android.net.Uri;
import android.webkit.MimeTypeMap;

public class FileExecutor {
  public static String getFileExtension(Uri imageUri, Context context) {
    ContentResolver contentResolver = context.getContentResolver();
    MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
    return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(imageUri));
  }
}
