package com.devmanjoo.limelight.ui.tickets;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.devmanjoo.limelight.R;
import com.devmanjoo.limelight.data.BookingInfo;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class TicketAdapter extends RecyclerView.Adapter<TicketAdapter.VH> {
    public interface CancelClick { void onCancel(int bookingId); }
    private final List<BookingInfo> items = new ArrayList<>();
    private final CancelClick cancel;
    public TicketAdapter(CancelClick c) { this.cancel = c; }

    public void submit(List<BookingInfo> list) {
        items.clear();
        if (list != null) items.addAll(list);
        notifyDataSetChanged();
    }

    @NonNull @Override
    public VH onCreateViewHolder(@NonNull ViewGroup p, int vType) {
        View v = LayoutInflater.from(p.getContext()).inflate(R.layout.item_ticket, p, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH h, int pos) {
        BookingInfo i = items.get(pos);

        // 영화 제목
        h.title.setText(i.title);

        // 시간 포맷팅 및 날짜 파싱
        String timeStr = "";
        Date startDate = null;

        // 날짜 포맷
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.KOREA);

        if (i.start_time != null && i.start_time.length() >= 16) {
            try {
                startDate = sdf.parse(i.start_time); // 날짜 객체로 변환
            } catch (Exception e) { e.printStackTrace(); }

            String datePart = i.start_time.substring(0, 10).replace("-", ".");
            String startPart = i.start_time.substring(11, 16);
            String endPart = (i.end_time != null && i.end_time.length() >= 16)
                    ? i.end_time.substring(11, 16)
                    : "??:??";
            timeStr = String.format("%s | %s ~ %s", datePart, startPart, endPart);
        }
        h.time.setText(timeStr);

        // 장소
        h.place.setText(String.format("%s %s", i.branch_name, i.screen_name));

        // 가격
        try {
            int price = Integer.parseInt(i.total_price);
            h.price.setText(String.format(Locale.KOREA, "%,d원", price));
        } catch (Exception e) {
            h.price.setText(i.total_price + "원");
        }

        // 이미지 로드
        Glide.with(h.poster.getContext())
                .load("http://devmanjoo.mycafe24.com/" + i.poster_img)
                .placeholder(R.drawable.ic_placeholder)
                .into(h.poster);

        // 취소 가능 여부 체크
        // 현재 시간과 상영 시작 시간 비교
        long currentTime = System.currentTimeMillis();
        boolean isCancellable = startDate != null && currentTime < startDate.getTime();

        if (isCancellable) {
            // 취소 가능
            h.btnCancel.setText("예매 취소");
            h.btnCancel.setEnabled(true);
            h.btnCancel.setTextColor(Color.BLACK);
            h.btnCancel.setBackgroundResource(R.drawable.bg_btn_lime_rounded);
            h.btnCancel.setOnClickListener(v -> cancel.onCancel(Integer.parseInt(i.booking_id)));
        } else {
            // 취소 불가
            h.btnCancel.setText("취소 불가");
            h.btnCancel.setEnabled(false);
            h.btnCancel.setTextColor(Color.parseColor("#888888"));
            h.btnCancel.setBackgroundResource(R.drawable.bg_btn_gray_rounded);
            // 클릭 이벤트 해제
            h.btnCancel.setOnClickListener(null);
        }
    }

    @Override public int getItemCount() { return items.size(); }

    static class VH extends RecyclerView.ViewHolder {
        ImageView poster;
        TextView title, time, place, price;
        Button btnCancel;

        VH(View v) {
            super(v);
            poster = v.findViewById(R.id.ivPoster);
            title  = v.findViewById(R.id.tvTitle);
            time   = v.findViewById(R.id.tvTime);
            place  = v.findViewById(R.id.tvPlace);
            price  = v.findViewById(R.id.tvPrice);
            btnCancel = v.findViewById(R.id.btnCancel);
        }
    }
}
