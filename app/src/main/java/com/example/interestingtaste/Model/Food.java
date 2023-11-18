package com.example.interestingtaste.Model;

import com.example.interestingtaste.Dto.CuisinePlaceDto;
import com.example.interestingtaste.Dto.FoodDto;
import com.example.interestingtaste.Dto.ReviewDto;
import com.example.interestingtaste.Dto.UserDto;
import com.example.interestingtaste.Shared.DateFormatter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Food {
  private UserDto author;
  private String id;
  private String displayName;
  private String category;
  private Double price;
  private String imageUrl;
  private CuisinePlaceDto destination;
  private String description;
  @Builder.Default private List<Review> reviews = new ArrayList<>();
  @Builder.Default private Date createdDate = new Date();
  @Builder.Default private Date updatedDate = new Date();

  public FoodDto toDto() {
    return FoodDto.builder()
        .id(id)
        .author(author)
        .destination(destination)
        .category(category)
        .price(price)
        .displayName(displayName)
        .reviews(reviews.stream().map(Review::toDto).collect(Collectors.toList()))
        .createdDate(DateFormatter.toDateString(createdDate))
        .updatedDate(DateFormatter.toDateString(updatedDate))
        .description(description)
        .imageUrl(imageUrl)
        .build();
  }
}
