package com.devmanjoo.limelight.ui.book;

import android.graphics.Color;
import android.view.*;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.devmanjoo.limelight.R;
import com.devmanjoo.limelight.data.ShowTime;
import java.util.*;

public class ShowTimeAdapter extends RecyclerView.Adapter<ShowTimeAdapter.VH> {
    public interface Click { void onSelect(ShowTime t, boolean isMorning); } // 조조 여부 전달
    private final List<ShowTime> items = new ArrayList<>();
    private final Click click;
    private int selectedPos = -1;

    public ShowTimeAdapter(Click c){ this.click=c; }

    public void submit(List<ShowTime> list){
        items.clear();
        if(list!=null) items.addAll(list);
        selectedPos = -1;
        notifyDataSetChanged();
    }

    @NonNull @Override public VH onCreateViewHolder(@NonNull ViewGroup p, int vType) {
        View v = LayoutInflater.from(p.getContext()).inflate(R.layout.item_showtime, p, false);
        return new VH(v);
    }

    @Override public void onBindViewHolder(@NonNull VH h, int pos) {
        ShowTime t = items.get(pos);

        // 조조 여부 판단 07:00 ~ 11:00
        boolean isMorning = false;
        try {
            // "09:00 ~ 11:00" 앞 2자리 파싱
            String startHourStr = t.time_display.substring(0, 2);
            int hour = Integer.parseInt(startHourStr);
            if (hour >= 7 && hour < 11) isMorning = true;
        } catch (Exception e) {}

        // 텍스트 설정
        if (isMorning) {
            h.time.setText("[조조] " + t.time_display);
            h.time.setTextColor(Color.parseColor("#cfff04"));
        } else {
            h.time.setText(t.time_display);
            h.time.setTextColor(Color.WHITE);
        }
        h.screen.setText(t.screen_name);

        // 선택 상태 & 마감 처리
        if (t.is_past) {
            h.itemView.setEnabled(false);
            h.itemView.setAlpha(0.3f);
            h.itemView.setBackgroundResource(R.drawable.bg_btn_gray_rounded);
            h.time.setText("[마감] " + t.time_display);
        } else {
            h.itemView.setEnabled(true);
            h.itemView.setAlpha(1.0f);

            if (selectedPos == pos) {
                h.itemView.setBackgroundResource(R.drawable.bg_btn_lime_rounded); // 선택됨
                h.time.setTextColor(Color.BLACK);
                h.screen.setTextColor(Color.BLACK);
            } else {
                h.itemView.setBackgroundResource(R.drawable.bg_btn_gray_rounded); // 기본 회색 배경
                if(!isMorning) h.time.setTextColor(Color.WHITE);
                h.screen.setTextColor(Color.parseColor("#AAAAAA"));
            }
        }

        boolean finalIsMorning = isMorning;
        h.itemView.setOnClickListener(v -> {
            if (!t.is_past) {
                int prev = selectedPos;
                selectedPos = h.getBindingAdapterPosition();
                notifyItemChanged(prev);
                notifyItemChanged(selectedPos);
                click.onSelect(t, finalIsMorning);
            }
        });
    }

    @Override public int getItemCount(){ return items.size(); }

    static class VH extends RecyclerView.ViewHolder {
        TextView time, screen;
        VH(View v){ super(v); time=v.findViewById(R.id.tvTime); screen=v.findViewById(R.id.tvScreen); }
    }
}