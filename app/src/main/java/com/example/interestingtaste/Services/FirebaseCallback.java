package com.example.interestingtaste.Services;

import java.util.List;

public interface FirebaseCallback<T> {
  void callbackListRes(List<T> listT);

  void callbackRes(T t);
}
