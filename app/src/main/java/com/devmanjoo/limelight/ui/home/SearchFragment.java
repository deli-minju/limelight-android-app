package com.devmanjoo.limelight.ui.home;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.devmanjoo.limelight.R;
import com.devmanjoo.limelight.data.Movie;
import com.devmanjoo.limelight.data.MovieRepository;
import com.devmanjoo.limelight.data.ProfileInfoResponse;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.List;

public class SearchFragment extends Fragment {
    private MovieRepository repo;
    private MovieAdapter adapter;
    private EditText etSearch;
    private TextView tvTitle, tvCount, tvNoResult;
    private RecyclerView rvResult;
    private String query = "";

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            query = getArguments().getString("query", "");
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_search, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View v, Bundle b) {
        super.onViewCreated(v, b);
        repo = new MovieRepository();

        etSearch = v.findViewById(R.id.etSearch);
        tvTitle = v.findViewById(R.id.tvSearchResult);
        tvCount = v.findViewById(R.id.tvCount);
        tvNoResult = v.findViewById(R.id.tvNoResult);
        rvResult = v.findViewById(R.id.rvResult);

        if (!query.isEmpty()) {
            etSearch.setText(query);
            tvTitle.setText("'" + query + "' 검색 결과");
            performSearch(query);
        } else {
            tvTitle.setText("검색 결과");
        }

        adapter = new MovieAdapter(this::checkLoginAndLike, this::checkLoginAndReview, this::onBookClick);

        // 2열 그리드 설정
        rvResult.setLayoutManager(new GridLayoutManager(getContext(), 2));
        rvResult.setAdapter(adapter);

        v.findViewById(R.id.btnSearch).setOnClickListener(view -> {
            String input = etSearch.getText().toString().trim();
            performSearch(input);
        });

        etSearch.setOnEditorActionListener((v1, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                performSearch(etSearch.getText().toString().trim());
                return true;
            }
            return false;
        });
    }

    private void performSearch(String input) {
        if (input.isEmpty()) {
            Toast.makeText(getContext(), "검색어를 입력해주세요.", Toast.LENGTH_SHORT).show();
            return;
        }
        this.query = input;
        tvTitle.setText("'" + query + "' 검색 결과");

        if (getActivity() != null) {
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            View currentFocus = getActivity().getCurrentFocus();
            if (currentFocus != null) {
                imm.hideSoftInputFromWindow(currentFocus.getWindowToken(), 0);
            }
        }

        repo.searchMovies(query, new MovieRepository.Cb<List<Movie>>() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void ok(List<Movie> movies) {
                tvCount.setText("(" + movies.size() + "건)");
                if (movies.isEmpty()) {
                    rvResult.setVisibility(View.GONE);
                    tvNoResult.setVisibility(View.VISIBLE);
                } else {
                    rvResult.setVisibility(View.VISIBLE);
                    tvNoResult.setVisibility(View.GONE);
                    adapter.submitList(movies);
                }
            }
            @Override
            public void fail(Throwable t) {
                String msg = t.getMessage();
                Toast.makeText(getContext(), "오류: " + msg, Toast.LENGTH_LONG).show();
            }
        });
    }

    // 기능 제한
    private void checkLoginAndLike(Movie m) {
        repo.getProfileInfo(new MovieRepository.Cb<ProfileInfoResponse.ProfileData>() {
            @Override
            public void ok(ProfileInfoResponse.ProfileData data) {
                repo.toggleLike(m.id, new MovieRepository.SimpleCb() {
                    @SuppressLint("NotifyDataSetChanged")
                    @Override public void ok() {
                        m.is_liked = !m.is_liked;
                        if(m.is_liked) m.like_count++; else m.like_count--;
                        adapter.notifyDataSetChanged();
                    }
                    @Override public void fail(Throwable t) { }
                });
            }
            @Override
            public void fail(Throwable t) { promptLogin(); }
        });
    }

    private void checkLoginAndReview(Movie m) {
        repo.getProfileInfo(new MovieRepository.Cb<ProfileInfoResponse.ProfileData>() {
            @Override
            public void ok(ProfileInfoResponse.ProfileData data) {
                ReviewDialog.show(getParentFragmentManager(), m);
            }
            @Override
            public void fail(Throwable t) { promptLogin(); }
        });
    }

    // 커스텀 로그인 팝업
    private void promptLogin() {
        if (getContext() == null) return;

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        View view = LayoutInflater.from(getContext()).inflate(R.layout.dialog_login_prompt, null);
        builder.setView(view);

        AlertDialog dialog = builder.create();

        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }

        Button btnCancel = view.findViewById(R.id.btnCancel);
        Button btnConfirm = view.findViewById(R.id.btnConfirm);

        btnCancel.setOnClickListener(v -> dialog.dismiss());

        btnConfirm.setOnClickListener(v -> {
            dialog.dismiss();
            BottomNavigationView bottomNav = requireActivity().findViewById(R.id.bottom_nav);
            if (bottomNav != null) bottomNav.setSelectedItemId(R.id.profileFragment);
        });

        dialog.show();
    }

    private void onBookClick(Movie m) {
        NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment);
        navController.navigate(R.id.bookFragment);
    }
}