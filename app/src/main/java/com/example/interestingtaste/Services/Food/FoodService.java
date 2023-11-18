package com.example.interestingtaste.Services.Food;

import static android.content.ContentValues.TAG;

import static androidx.core.content.ContextCompat.startActivity;
import static androidx.core.content.ContextCompat.startForegroundService;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.AbsListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewbinding.ViewBinding;

import com.example.interestingtaste.Activity.FoodDetailActivity;
import com.example.interestingtaste.Activity.MainActivity;
import com.example.interestingtaste.Adapter.AllFoodAdapter;
import com.example.interestingtaste.Adapter.ReviewAdapter;
import com.example.interestingtaste.Dto.FoodDto;
import com.example.interestingtaste.Dto.ReviewDto;
import com.example.interestingtaste.Dto.UserDto;
import com.example.interestingtaste.Model.Food;
import com.example.interestingtaste.Repository.FoodRepository;
import com.example.interestingtaste.Services.FirebaseCallback;
import com.example.interestingtaste.Shared.DateFormatter;
import com.example.interestingtaste.Shared.RatingCalculator;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.AbstractList;
import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.Nullable;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Builder
@NoArgsConstructor
public class FoodService {
  private final FoodRepository foodRepository = FoodRepository.builder().build();

  public void getAllFood(
      Context context, AbsListView viewList, @Nullable ShimmerFrameLayout shimmerFrameLayout) {
    foodRepository.getAllFood(
        new FirebaseCallback<FoodDto>() {
          @Override
          public void callbackListRes(List<FoodDto> listT) {
            AllFoodAdapter allFoodAdapter = new AllFoodAdapter(context, listT);
            viewList.setAdapter(allFoodAdapter);
            viewList.setVisibility(View.VISIBLE);
            viewList.setOnItemClickListener(
                (parent, view, position, id) -> {
                  Intent intent = new Intent(context, FoodDetailActivity.class);
                  intent.putExtra("foodId", listT.get(position).getId());
                  intent.putExtra("foodName", listT.get(position).getDisplayName());
                  intent.putExtra("image", listT.get(position).getImageUrl());
                  intent.putExtra("foodRating", RatingCalculator.getAvgRating(listT.get(position)));
                  intent.putExtra(
                      "storeName", listT.get(position).getDestination().getDisplayName());
                  intent.putExtra(
                      "storeLocation", listT.get(position).getDestination().getDisplayPosition());
                  intent.putExtra(
                      "latitude", listT.get(position).getDestination().getLatitude().toString());
                  intent.putExtra(
                      "longitude", listT.get(position).getDestination().getLongitude().toString());
                  intent.putExtra("updatedDate", listT.get(position).getUpdatedDate());
                  intent.putExtra("authorImg", listT.get(position).getAuthor().getImgUrl());
                  intent.putExtra("authorDisplay", listT.get(position).getAuthor().getEmail());
                  intent.putExtra("des", listT.get(position).getDescription());
                  intent.putExtra("foodPrice", listT.get(position).getPrice().toString());
                  context.startActivity(intent);
                });
            if (shimmerFrameLayout != null) {
              shimmerFrameLayout.stopShimmer();
              shimmerFrameLayout.setVisibility(View.INVISIBLE);
            }
          }

          @Override
          public void callbackRes(FoodDto foodDto) {}
        });
  }

  public void getOneFood(
      Context context,
      AbsListView viewList,
      @Nullable ShimmerFrameLayout shimmerFrameLayout,
      String id) {
    foodRepository.getOneFood(
        id,
        new FirebaseCallback<FoodDto>() {
          @Override
          public void callbackListRes(List<FoodDto> listT) {}

          @Override
          public void callbackRes(FoodDto foodDto) {
            if (shimmerFrameLayout != null) {
              shimmerFrameLayout.stopShimmer();
              shimmerFrameLayout.setVisibility(View.INVISIBLE);
            }
            ReviewAdapter reviewAdapter = new ReviewAdapter(context, foodDto.getReviews());
            viewList.setAdapter(reviewAdapter);
            viewList.setVisibility(View.VISIBLE);
          }
        });
  }

  public void getQueryFood(
      Context context,
      AbsListView viewList,
      @Nullable ShimmerFrameLayout shimmerFrameLayout,
      @Nullable RelativeLayout nothingFound,
      String category,
      String query) {
    foodRepository.getFoodByFilter(
        category,
        query,
        new FirebaseCallback<FoodDto>() {
          @Override
          public void callbackListRes(List<FoodDto> listT) {
            if (nothingFound != null) {
              if (listT == null || listT.isEmpty()) {
                nothingFound.setVisibility(View.VISIBLE);
              } else {
                nothingFound.setVisibility(View.INVISIBLE);
                AllFoodAdapter foodAdapter = new AllFoodAdapter(context, listT);
                viewList.setAdapter(foodAdapter);
                viewList.setVisibility(View.VISIBLE);
                viewList.setOnItemClickListener(
                    (parent, view, position, id) -> {
                      Intent intent = new Intent(context, FoodDetailActivity.class);
                      intent.putExtra("foodId", listT.get(position).getId());
                      intent.putExtra("foodName", listT.get(position).getDisplayName());
                      intent.putExtra("image", listT.get(position).getImageUrl());
                      intent.putExtra(
                          "foodRating", RatingCalculator.getAvgRating(listT.get(position)));
                      intent.putExtra(
                          "storeName", listT.get(position).getDestination().getDisplayName());
                      intent.putExtra(
                          "storeLocation",
                          listT.get(position).getDestination().getDisplayPosition());
                      intent.putExtra(
                          "latitude",
                          listT.get(position).getDestination().getLatitude().toString());
                      intent.putExtra(
                          "longitude",
                          listT.get(position).getDestination().getLongitude().toString());
                      intent.putExtra("updatedDate", listT.get(position).getUpdatedDate());
                      intent.putExtra("authorImg", listT.get(position).getAuthor().getImgUrl());
                      intent.putExtra("authorDisplay", listT.get(position).getAuthor().getEmail());
                      intent.putExtra("des", listT.get(position).getDescription());
                      intent.putExtra("foodPrice", listT.get(position).getPrice().toString());
                      context.startActivity(intent);
                    });
              }
            }
            if (shimmerFrameLayout != null) {
              shimmerFrameLayout.stopShimmer();
              shimmerFrameLayout.setVisibility(View.INVISIBLE);
            }
          }

          @Override
          public void callbackRes(FoodDto foodDto) {}
        });
  }

  public void createFood(Context context, Food food) {
    foodRepository.addFood(
        food,
        new FirebaseCallback<Food>() {
          @Override
          public void callbackListRes(List<Food> listT) {}

          @Override
          public void callbackRes(Food food) {
            if (food != null) {
              Toast.makeText(context, "Recommend successfully", Toast.LENGTH_LONG).show();
            } else {
              Toast.makeText(context, "Network error! Try again", Toast.LENGTH_LONG).show();
            }
          }
        });
  }
}
