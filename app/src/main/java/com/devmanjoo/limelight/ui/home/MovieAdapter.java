package com.devmanjoo.limelight.ui.home;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.devmanjoo.limelight.R;
import com.devmanjoo.limelight.data.Movie;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.VH> {
    public interface LikeClick { void onLike(Movie m); }
    public interface ReviewClick { void onReview(Movie m); }
    public interface BookClick { void onBook(Movie m); }

    private final LikeClick likeClick;
    private final ReviewClick reviewClick;
    private final BookClick bookClick;
    private final List<Movie> items = new ArrayList<>();

    public MovieAdapter(LikeClick l, ReviewClick r, BookClick b) {
        this.likeClick = l;
        this.reviewClick = r;
        this.bookClick = b;
    }

    public void submitList(List<Movie> list) {
        items.clear();
        if (list != null) items.addAll(list);
        notifyDataSetChanged();
    }

    @NonNull @Override
    public VH onCreateViewHolder(@NonNull ViewGroup p, int vType) {
        View v = LayoutInflater.from(p.getContext()).inflate(R.layout.item_movie, p, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH h, int pos) {
        Movie m = items.get(pos);

        h.tvTitle.setText(m.title);

        // 상영 상태 UI 처리
        if (m.d_day > 0) {
            h.tvDday.setVisibility(View.VISIBLE);
            h.tvDday.setText("D-" + m.d_day);
            h.tvReleaseDate.setVisibility(View.VISIBLE);
            h.tvReleaseDate.setText(m.release_date + " 개봉");
            h.btnReview.setVisibility(View.GONE);
        } else {
            h.tvDday.setVisibility(View.GONE);
            h.tvReleaseDate.setVisibility(View.GONE);
            h.btnReview.setVisibility(View.VISIBLE);
        }

        // 카운트 표시
        h.tvCounts.setText(String.format(Locale.getDefault(), "♥ %d  ·  ✎ %d", m.like_count, m.review_count));

        // 하트 색상
        if (m.is_liked) {
            h.btnLike.setImageResource(R.drawable.ic_heart_filled);
            h.btnLike.setColorFilter(null);
        } else {
            h.btnLike.setImageResource(R.drawable.ic_heart_empty);
            h.btnLike.setColorFilter(Color.WHITE);
        }

        Glide.with(h.ivPoster.getContext())
                .load("http://devmanjoo.mycafe24.com/" + m.poster_img)
                .placeholder(R.drawable.ic_placeholder)
                .into(h.ivPoster);

        h.btnLike.setOnClickListener(v -> likeClick.onLike(m));
        h.btnReview.setOnClickListener(v -> reviewClick.onReview(m));
    }

    @Override public int getItemCount() { return items.size(); }

    static class VH extends RecyclerView.ViewHolder {
        ImageView ivPoster;
        TextView tvTitle, tvDday, tvReleaseDate, btnReview, tvCounts;
        ImageButton btnLike;

        VH(@NonNull View v) {
            super(v);
            ivPoster = v.findViewById(R.id.ivPoster);
            tvTitle = v.findViewById(R.id.tvTitle);
            tvDday = v.findViewById(R.id.tvDday);
            tvReleaseDate = v.findViewById(R.id.tvReleaseDate);
            btnReview = v.findViewById(R.id.btnReview);
            tvCounts = v.findViewById(R.id.tvCounts); // 추가됨
            btnLike = v.findViewById(R.id.btnLike);
        }
    }
}