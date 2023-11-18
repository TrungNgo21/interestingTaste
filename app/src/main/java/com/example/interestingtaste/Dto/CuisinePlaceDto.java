package com.example.interestingtaste.Dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class CuisinePlaceDto {
  private String id;
  private String displayName;
  private Double latitude;
  private Double longitude;
  private String displayPosition;
}
