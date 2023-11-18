package com.example.interestingtaste.Adapter;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.interestingtaste.Dto.FoodDto;
import com.example.interestingtaste.Dto.ReviewDto;
import com.example.interestingtaste.R;
import com.example.interestingtaste.Shared.RatingCalculator;
import com.squareup.picasso.Picasso;

import java.util.List;

public class AllFoodAdapter extends BaseAdapter {
  private final Context context;

  private final List<FoodDto> foodDtoList;

  private LayoutInflater inflater;

  public AllFoodAdapter(Context context, List<FoodDto> foodDtoList) {
    this.context = context;
    this.foodDtoList = foodDtoList;
  }

  @Override
  public int getCount() {
    return foodDtoList.size();
  }

  @Override
  public Object getItem(int position) {
    return foodDtoList.get(position);
  }

  @Override
  public long getItemId(int position) {
    return position;
  }

  @Override
  public View getView(int i, View convertView, ViewGroup parent) {

    if (inflater == null) {
      inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }
    if (convertView == null) {
      convertView = inflater.inflate(R.layout.activity_food_general, null);
    }
    ImageView foodImage = convertView.findViewById(R.id.foodImg);
    TextView foodName = convertView.findViewById(R.id.foodName);
    TextView foodRating = convertView.findViewById(R.id.foodRating);
    TextView storeName = convertView.findViewById(R.id.storeName);
    TextView location = convertView.findViewById(R.id.location);
    Picasso.get()
        .load(foodDtoList.get(i).getImageUrl())
        .placeholder(R.drawable.food_placeholder)
        .into(foodImage);
    foodName.setText(foodDtoList.get(i).getDisplayName());
    Log.d("average rating", RatingCalculator.getAvgRating(foodDtoList.get(i)));
    foodRating.setText(RatingCalculator.getAvgRating(foodDtoList.get(i)));
    storeName.setText(foodDtoList.get(i).getDestination().getDisplayName());
    location.setText(foodDtoList.get(i).getDestination().getDisplayPosition());

    return convertView;
  }
}
