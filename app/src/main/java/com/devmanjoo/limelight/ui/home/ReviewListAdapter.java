package com.devmanjoo.limelight.ui.home;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.devmanjoo.limelight.R;
import com.devmanjoo.limelight.data.Review;
import java.util.ArrayList;
import java.util.List;

public class ReviewListAdapter extends RecyclerView.Adapter<ReviewListAdapter.VH> {
    private final List<Review> items = new ArrayList<>();

    public void submit(List<Review> list) {
        items.clear();
        if (list != null) items.addAll(list);
        notifyDataSetChanged();
    }

    @NonNull @Override public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_review, parent, false);
        return new VH(v);
    }

    @Override public void onBindViewHolder(@NonNull VH h, int pos) {
        Review r = items.get(pos);
        h.user.setText(r.nickname);
        h.date.setText(r.created_at);
        h.content.setText(r.content);
    }

    @Override public int getItemCount() { return items.size(); }

    static class VH extends RecyclerView.ViewHolder {
        TextView user, date, content;
        VH(@NonNull View v) {
            super(v);
            user = v.findViewById(R.id.tvUser);
            date = v.findViewById(R.id.tvDate);
            content = v.findViewById(R.id.tvContent);
        }
    }
}