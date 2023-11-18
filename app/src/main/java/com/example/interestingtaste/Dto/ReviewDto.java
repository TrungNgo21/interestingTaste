package com.example.interestingtaste.Dto;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class ReviewDto {
  private String id;
  private UserDto user;

  private Double rating;

  private String feedBack;

  private String createdDate;

  private String updatedDate;
}
