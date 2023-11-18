package com.example.interestingtaste.Shared;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DateFormatter {
  public static String toDateString(Date date) {
    SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
    return formatter.format(date);
  }
}
