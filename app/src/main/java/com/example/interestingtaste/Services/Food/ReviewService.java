package com.example.interestingtaste.Services.Food;

import android.app.Dialog;
import android.content.Context;
import android.widget.AbsListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.interestingtaste.Activity.FoodDetailActivity;
import com.example.interestingtaste.Adapter.ReviewAdapter;
import com.example.interestingtaste.Dto.FoodDto;
import com.example.interestingtaste.Dto.ReviewDto;
import com.example.interestingtaste.Model.Review;
import com.example.interestingtaste.R;
import com.example.interestingtaste.Repository.FoodRepository;
import com.example.interestingtaste.Services.FirebaseCallback;
import com.example.interestingtaste.Shared.RatingCalculator;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

@Builder
@NoArgsConstructor
public class ReviewService {
  private final FoodRepository foodRepository = FoodRepository.builder().build();

  public void addReview(Review reviewDto, String foodId, Context context, Dialog reviewDialog) {
    foodRepository.addReviewToFood(
        reviewDto,
        new FirebaseCallback<Review>() {
          @Override
          public void callbackListRes(List<Review> listT) {}

          @Override
          public void callbackRes(Review reviewDto) {
            if (reviewDto != null) {
              reviewDialog.dismiss();
              foodRepository.getOneFood(
                  foodId,
                  new FirebaseCallback<FoodDto>() {
                    @Override
                    public void callbackListRes(List<FoodDto> listT) {}

                    @Override
                    public void callbackRes(FoodDto foodDto) {
                      if (foodDto != null) {
                        ReviewAdapter reviewAdapter =
                            new ReviewAdapter(context, foodDto.getReviews());
                        FoodDetailActivity foodDetailActivity = (FoodDetailActivity) context;
                        AbsListView listView =
                            foodDetailActivity.findViewById(R.id.foodDetailReviews);
                        TextView rating = foodDetailActivity.findViewById(R.id.foodDetailRating);
                        rating.setText(String.valueOf(RatingCalculator.getAvgRating(foodDto)));
                        listView.setAdapter(reviewAdapter);
                      } else {
                        Toast.makeText(context, "Network error! Try again", Toast.LENGTH_LONG)
                            .show();
                      }
                    }
                  });
              Toast.makeText(context, "Leave feedback successfully!", Toast.LENGTH_LONG).show();
            } else {
              Toast.makeText(context, "Network error! Try again", Toast.LENGTH_LONG).show();
            }
          }
        },
        foodId);
  }
}
