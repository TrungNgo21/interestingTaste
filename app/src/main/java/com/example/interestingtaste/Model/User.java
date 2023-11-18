package com.example.interestingtaste.Model;

import com.example.interestingtaste.Dto.UserDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class User {
  private String id;
  @Builder.Default private String email = "testing@gmail.com";
  private String password;
  private String displayName;

  public UserDto toDto() {
    return UserDto.builder().id(id).displayName(displayName).email(email).build();
  }
}
