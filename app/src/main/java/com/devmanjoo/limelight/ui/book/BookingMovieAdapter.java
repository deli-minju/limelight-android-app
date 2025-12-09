package com.devmanjoo.limelight.ui.book;

import android.graphics.Color;
import android.view.*;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.devmanjoo.limelight.R;
import com.devmanjoo.limelight.data.BookingMovie;
import java.util.*;

public class BookingMovieAdapter extends RecyclerView.Adapter<BookingMovieAdapter.VH> {
    public interface Click { void onSelect(BookingMovie m); }
    private final List<BookingMovie> items = new ArrayList<>();
    private final Click click;
    private int selectedPos = -1; // 선택된 위치 저장

    public BookingMovieAdapter(Click c){ this.click=c; }

    public void submit(List<BookingMovie> list){
        items.clear();
        if(list!=null) items.addAll(list);
        selectedPos = -1; // 리스트 갱신 시 선택 초기화
        notifyDataSetChanged();
    }

    public void setSelection(int pos) {
        int prev = selectedPos;
        selectedPos = pos;
        if (prev != -1) notifyItemChanged(prev);
        if (selectedPos != -1) notifyItemChanged(selectedPos);
    }

    @NonNull @Override public VH onCreateViewHolder(@NonNull ViewGroup p, int vType) {
        View v = LayoutInflater.from(p.getContext()).inflate(R.layout.item_booking_movie, p, false);
        return new VH(v);
    }

    @Override public void onBindViewHolder(@NonNull VH h, int pos) {
        BookingMovie m = items.get(pos);
        h.title.setText(m.title);
        Glide.with(h.poster.getContext())
                .load("http://devmanjoo.mycafe24.com/" + m.poster_img)
                .placeholder(R.drawable.ic_placeholder)
                .into(h.poster);

        // 선택 상태 시각화
        if (selectedPos == pos) {
            h.poster.setAlpha(1.0f);
            h.title.setTextColor(Color.parseColor("#cfff04"));
            h.itemView.setBackgroundResource(R.drawable.bg_border_lime);
        } else {
            h.poster.setAlpha(0.5f); // 선택 안되면 흐리게
            h.title.setTextColor(Color.WHITE);
            h.itemView.setBackground(null);
        }

        h.itemView.setOnClickListener(v -> {
            setSelection(h.getBindingAdapterPosition());
            click.onSelect(m);
        });
    }
    @Override public int getItemCount(){ return items.size(); }

    static class VH extends RecyclerView.ViewHolder {
        ImageView poster; TextView title;
        VH(View v){ super(v); poster=v.findViewById(R.id.ivPoster); title=v.findViewById(R.id.tvTitle); }
    }
}