package com.example.interestingtaste.Model;

import com.example.interestingtaste.Dto.ReviewDto;
import com.example.interestingtaste.Dto.UserDto;
import com.example.interestingtaste.Shared.DateFormatter;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@Builder
@Data
@NoArgsConstructor
public class Review {
  private String id;
  private UserDto userId;

  private Double rating;

  private String feedBack;

  @Builder.Default private Date createdDate = new Date();

  @Builder.Default private Date updatedDate = new Date();

  public ReviewDto toDto() {
    return ReviewDto.builder()
        .id(id)
        .rating(rating)
        .user(userId)
        .feedBack(feedBack)
        .createdDate(DateFormatter.toDateString(createdDate))
        .updatedDate(DateFormatter.toDateString(updatedDate))
        .build();
  }
}
