package com.example.interestingtaste.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.interestingtaste.Dto.ReviewDto;
import com.example.interestingtaste.R;
import com.squareup.picasso.Picasso;

import java.util.List;

public class ReviewAdapter extends BaseAdapter {

  private Context context;

  private List<ReviewDto> reviews;

  private LayoutInflater inflater;

  public ReviewAdapter(Context context, List<ReviewDto> reviews) {
    this.context = context;
    this.reviews = reviews;
  }

  @Override
  public int getCount() {
    return reviews.size();
  }

  @Override
  public Object getItem(int position) {
    return reviews.get(position);
  }

  @Override
  public long getItemId(int position) {
    return position;
  }

  @Override
  public View getView(int position, View convertView, ViewGroup parent) {

    ReviewDto reviewDto = reviews.get(position);
    if (inflater == null) {
      inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }
    if (convertView == null) {
      convertView = inflater.inflate(R.layout.review_detail, null);
    }
    ImageView authorImg = convertView.findViewById(R.id.reviewAuthorImg);
    TextView authorDisplay = convertView.findViewById(R.id.reviewAuthor);
    TextView reviewContent = convertView.findViewById(R.id.reviewContent);
    TextView reviewRating = convertView.findViewById(R.id.reviewRating);

    Picasso.get()
        .load(reviewDto.getUser().getImgUrl())
        .placeholder(R.drawable.food_placeholder)
        .into(authorImg);
    authorDisplay.setText(String.valueOf(reviewDto.getUser().getEmail()));
    reviewContent.setText(String.valueOf(reviewDto.getFeedBack()));
    reviewRating.setText(String.valueOf(reviewDto.getRating()));
    return convertView;
  }
}
