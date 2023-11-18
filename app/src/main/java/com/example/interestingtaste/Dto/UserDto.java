package com.example.interestingtaste.Dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDto {
  private String id;
  private String email;
  private String displayName;
  private String imgUrl;
}
