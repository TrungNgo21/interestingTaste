package com.example.interestingtaste.Shared;

import com.example.interestingtaste.Dto.FoodDto;
import com.example.interestingtaste.Dto.ReviewDto;

import java.text.DecimalFormat;

public class RatingCalculator {

  private static final DecimalFormat dfZero = new DecimalFormat("0.0");

  public static String getAvgRating(FoodDto foodDto) {
    if (foodDto.getReviews().isEmpty()) {
      return "0.0";
    }

    Double finalRating =
        foodDto.getReviews().stream().map(ReviewDto::getRating).reduce(0D, Double::sum)
            / foodDto.getReviews().size();
    return String.valueOf(dfZero.format(finalRating));
  }
}
