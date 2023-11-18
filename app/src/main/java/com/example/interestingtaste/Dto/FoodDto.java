package com.example.interestingtaste.Dto;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class FoodDto {
  private String id;
  private UserDto author;
  private String displayName;
  private String category;
  private Double price;
  private String imageUrl;
  private CuisinePlaceDto destination;
  private String description;
  @Builder.Default private List<ReviewDto> reviews = new ArrayList<>();
  private String createdDate;
  private String updatedDate;
}
