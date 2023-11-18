package com.example.interestingtaste.Repository;

import static android.content.ContentValues.TAG;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.interestingtaste.Dto.CuisinePlaceDto;
import com.example.interestingtaste.Dto.FoodDto;
import com.example.interestingtaste.Dto.ReviewDto;
import com.example.interestingtaste.Dto.UserDto;
import com.example.interestingtaste.Model.Food;
import com.example.interestingtaste.Model.Review;
import com.example.interestingtaste.Model.User;
import com.example.interestingtaste.Services.FirebaseCallback;
import com.example.interestingtaste.Services.Food.FoodService;
import com.example.interestingtaste.Shared.DateFormatter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firestore.v1.WriteResult;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import lombok.Builder;
import lombok.NoArgsConstructor;

@Builder
@NoArgsConstructor
public class FoodRepository {
  private final FirebaseFirestore db = FirebaseFirestore.getInstance();
  private final CollectionReference foodRef = db.collection("food");

  //  private final CollectionReference userRef = db.collection("user");
  //  private final CollectionReference categoryRef = db.collection("category");
  //  private final CollectionReference reviewsRef = db.collection("reviews");

  public void getAllFood(final FirebaseCallback<FoodDto> callback) {
    List<FoodDto> fetchedFood = new ArrayList<>();
    CuisinePlaceDto fetchedPlace = CuisinePlaceDto.builder().build();
    foodRef
        .get()
        .addOnCompleteListener(
            task -> {
              if (task.isSuccessful()) {
                for (QueryDocumentSnapshot document : task.getResult()) {
                  Food food = document.toObject(Food.class);
                  FoodDto foodDto = food.toDto();
                  foodDto.setId(document.getId());
                  fetchedFood.add(foodDto);
                }
                callback.callbackListRes(fetchedFood);
              } else {
                callback.callbackListRes(null);
              }
            });
  }

  public void getOneFood(String foodId, final FirebaseCallback<FoodDto> callback) {
    foodRef
        .document(foodId)
        .get()
        .addOnCompleteListener(
            task -> {
              if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                Food food = document.toObject(Food.class);
                FoodDto foodDto = food.toDto();
                foodDto.setId(document.getId());
                callback.callbackRes(foodDto);
              } else {
                Log.d(TAG, "Cannot get data");

                callback.callbackRes(null);
              }
            });
  }

  public void getFoodByFilter(
      String category, String query, final FirebaseCallback<FoodDto> callback) {
    if (category.equals("All")) {
      foodRef
          .get()
          .addOnCompleteListener(
              task -> {
                if (task.isSuccessful()) {
                  List<FoodDto> listQueryFood = new ArrayList<>();
                  for (QueryDocumentSnapshot document : task.getResult()) {
                    Food food = document.toObject(Food.class);
                    if (query == null || query.isEmpty()) {
                      FoodDto foodDto = food.toDto();
                      foodDto.setId(document.getId());
                      listQueryFood.add(foodDto);
                    } else {
                      if (food.getDisplayName().toLowerCase().contains(query.toLowerCase())) {
                        FoodDto foodDto = food.toDto();
                        foodDto.setId(document.getId());
                        listQueryFood.add(foodDto);
                      }
                    }
                  }
                  callback.callbackListRes(listQueryFood);
                } else {
                  callback.callbackListRes(null);
                }
              });
    } else {

      foodRef
          .whereEqualTo("category", category)
          .get()
          .addOnCompleteListener(
              task -> {
                if (task.isSuccessful()) {
                  List<FoodDto> listQueryFood = new ArrayList<>();
                  for (QueryDocumentSnapshot document : task.getResult()) {
                    Food food = document.toObject(Food.class);
                    if (query == null || query.isEmpty()) {
                      FoodDto foodDto = food.toDto();
                      foodDto.setId(document.getId());
                      listQueryFood.add(foodDto);
                    } else {
                      if (food.getDisplayName().toLowerCase().contains(query.toLowerCase())) {
                        FoodDto foodDto = food.toDto();
                        foodDto.setId(document.getId());
                        listQueryFood.add(foodDto);
                      }
                    }
                  }
                  callback.callbackListRes(listQueryFood);
                } else {
                  callback.callbackListRes(null);
                }
              });
    }
  }

  public void addFood(Food food, final FirebaseCallback<Food> callback) {
    foodRef
        .add(food)
        .addOnCompleteListener(
            task -> {
              if (task.isSuccessful()) {
                callback.callbackRes(food);
              } else {
                callback.callbackRes(null);
              }
            });
  }

  public void addReviewToFood(
      Review review, final FirebaseCallback<Review> callback, String foodId) {
    foodRef
        .document(foodId)
        .update("reviews", FieldValue.arrayUnion(review))
        .addOnCompleteListener(
            task -> {
              if (task.isSuccessful()) {

                callback.callbackRes(review);
              } else {
                callback.callbackRes(null);
              }
            });
  }
}
