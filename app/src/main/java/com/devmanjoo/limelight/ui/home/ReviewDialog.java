package com.devmanjoo.limelight.ui.home;

import android.app.Dialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.devmanjoo.limelight.data.MovieRepository;
import com.devmanjoo.limelight.data.Review;
import com.devmanjoo.limelight.databinding.DialogReviewBinding;

import java.util.List;

public class ReviewDialog extends DialogFragment {
    private static final String ARG_ID = "movie_id";
    private static final String ARG_TITLE = "movie_title";

    public static void show(@NonNull androidx.fragment.app.FragmentManager fm, @NonNull com.devmanjoo.limelight.data.Movie m) {
        Bundle b = new Bundle();
        b.putInt(ARG_ID, m.id);
        b.putString(ARG_TITLE, m.title);
        ReviewDialog d = new ReviewDialog();
        d.setArguments(b);
        d.show(fm, "review");
    }

    @NonNull @Override public Dialog onCreateDialog(Bundle savedInstanceState) {
        DialogReviewBinding b = DialogReviewBinding.inflate(getLayoutInflater());
        MovieRepository repo = new MovieRepository();
        Bundle args = getArguments();
        if (args == null) return new Dialog(requireContext());

        int movieId = args.getInt(ARG_ID, -1);
        String title = args.getString(ARG_TITLE, "");

        b.tvTitle.setText(title);
        b.btnClose.setOnClickListener(v -> dismiss());

        ReviewListAdapter adapter = new ReviewListAdapter();

        b.rvReviews.setLayoutManager(new LinearLayoutManager(getContext()));
        b.rvReviews.setAdapter(adapter);

        load(repo, movieId, adapter);

        b.btnSubmit.setOnClickListener(v -> {
            String txt = b.etContent.getText().toString().trim();
            if (TextUtils.isEmpty(txt)) {
                Toast.makeText(getContext(),"내용을 입력하세요",Toast.LENGTH_SHORT).show();
                return;
            }
            repo.postReview(movieId, txt, new MovieRepository.SimpleCb() {
                @Override public void ok() {
                    b.etContent.setText("");
                    load(repo, movieId, adapter);
                    Toast.makeText(getContext(), "한줄평이 등록되었습니다.", Toast.LENGTH_SHORT).show();
                }
                @Override public void fail(Throwable t) {
                    Toast.makeText(getContext(),"등록 실패",Toast.LENGTH_SHORT).show();
                }
            });
        });

        // 배경 투명하게 설정 - 모서리 곡률 보여야 함
        AlertDialog dialog = new AlertDialog.Builder(requireContext())
                .setView(b.getRoot())
                .create();
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
        return dialog;
    }

    private void load(MovieRepository repo, int movieId, ReviewListAdapter adapter) {
        repo.getReviews(movieId, new MovieRepository.Cb<List<Review>>() {
            @Override public void ok(List<Review> data) { adapter.submit(data); }
            @Override public void fail(Throwable t) { }
        });
    }
}