package com.devmanjoo.limelight.ui.home;

import android.os.Bundle;
import android.os.SystemClock;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.devmanjoo.limelight.R;
import com.devmanjoo.limelight.data.Movie;
import com.devmanjoo.limelight.data.MovieRepository;
import com.devmanjoo.limelight.data.ProfileInfoResponse;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.List;

public class HomeFragment extends Fragment {
    private MovieAdapter nowAdapter, soonAdapter, myListAdapter;
    private MovieRepository repo;
    private static final String TAG = "HomeFragment";

    // 디폴트 정렬 설정
    private String sortNow = "popular_desc"; // 무비차트: 좋아요순
    private String sortSoon = "release_asc"; // 상영예정: 개봉일 빠른순

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View v, Bundle b) {
        super.onViewCreated(v, b);
        repo = new MovieRepository();

        nowAdapter = new MovieAdapter(this::checkLoginAndLike, this::checkLoginAndReview, this::onBookClick);
        soonAdapter = new MovieAdapter(this::checkLoginAndLike, this::checkLoginAndReview, this::onBookClick);
        myListAdapter = new MovieAdapter(this::checkLoginAndLike, this::checkLoginAndReview, this::onBookClick);

        setupRecyclerView(v.findViewById(R.id.rvNow), nowAdapter);
        setupRecyclerView(v.findViewById(R.id.rvSoon), soonAdapter);
        setupRecyclerView(v.findViewById(R.id.rvMyList), myListAdapter);

        setupSpinners(v);
        loadAllData();
    }

    private void loadAllData() {
        reloadNow();
        reloadSoon();
        reloadMyList();
    }

    private void setupSpinners(View v) {
        // 옵션 목록
        String[] optionsNow = {"좋아요순", "한줄평순", "가나다순"};
        String[] optionsSoon = {"개봉일순", "좋아요순", "가나다순"};

        Spinner spinNow = v.findViewById(R.id.spinnerNow);
        Spinner spinSoon = v.findViewById(R.id.spinnerSoon);

        ArrayAdapter<String> adaptNow = new ArrayAdapter<>(requireContext(), R.layout.spinner_item, optionsNow);
        adaptNow.setDropDownViewResource(R.layout.spinner_dropdown);
        spinNow.setAdapter(adaptNow);

        ArrayAdapter<String> adaptSoon = new ArrayAdapter<>(requireContext(), R.layout.spinner_item, optionsSoon);
        adaptSoon.setDropDownViewResource(R.layout.spinner_dropdown);
        spinSoon.setAdapter(adaptSoon);

        // 무비차트 정렬 리스너 인덱스 매핑
        spinNow.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> p, View v, int pos, long id) {
                // 0:좋아요, 1:한줄평, 2:가나다
                String newSort = "popular_desc"; // 기본
                if (pos == 1) newSort = "review_desc";
                if (pos == 2) newSort = "title_asc";

                if (!sortNow.equals(newSort)) {
                    sortNow = newSort;
                    reloadNow();
                }
            }
            @Override public void onNothingSelected(AdapterView<?> p) {}
        });

        // 상영예정 정렬 리스너 인덱스 매핑
        spinSoon.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> p, View v, int pos, long id) {
                // 0:개봉일(빠른순), 1:좋아요, 2:가나다
                String newSort = "release_asc"; // 기본
                if (pos == 1) newSort = "popular_desc";
                if (pos == 2) newSort = "title_asc";

                if (!sortSoon.equals(newSort)) {
                    sortSoon = newSort;
                    reloadSoon();
                }
            }
            @Override public void onNothingSelected(AdapterView<?> p) {}
        });
    }

    private void setupRecyclerView(RecyclerView rv, MovieAdapter adapter) {
        rv.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        rv.setAdapter(adapter);
        rv.setNestedScrollingEnabled(false);
    }

    private void reloadNow() {
        repo.getMovies("now", sortNow, new MovieRepository.Cb<List<Movie>>() {
            @Override public void ok(List<Movie> movies) { nowAdapter.submitList(movies); }
            @Override public void fail(Throwable t) { }
        });
    }

    private void reloadSoon() {
        repo.getMovies("soon", sortSoon, new MovieRepository.Cb<List<Movie>>() {
            @Override public void ok(List<Movie> movies) { soonAdapter.submitList(movies); }
            @Override public void fail(Throwable t) { }
        });
    }

    private void reloadMyList() {
        View container = getView().findViewById(R.id.layoutMyListContainer);
        View emptyText = getView().findViewById(R.id.tvMyListEmpty);
        View rvList = getView().findViewById(R.id.rvMyList);

        if (container == null) return;

        repo.getProfileInfo(new MovieRepository.Cb<ProfileInfoResponse.ProfileData>() {
            @Override
            public void ok(ProfileInfoResponse.ProfileData data) {
                container.setVisibility(View.VISIBLE);
                repo.getMovies("mylist", "release_desc", new MovieRepository.Cb<List<Movie>>() {
                    @Override
                    public void ok(List<Movie> movies) {
                        if (movies.isEmpty()) {
                            emptyText.setVisibility(View.VISIBLE);
                            rvList.setVisibility(View.GONE);
                        } else {
                            emptyText.setVisibility(View.GONE);
                            rvList.setVisibility(View.VISIBLE);
                            myListAdapter.submitList(movies);
                        }
                    }
                    @Override public void fail(Throwable t) {}
                });
            }
            @Override
            public void fail(Throwable t) {
                // 로그아웃 상태면 MY LIST 영역 전체 숨김
                container.setVisibility(View.GONE);
            }
        });
    }

    private void checkLoginAndLike(Movie m) {
        repo.getProfileInfo(new MovieRepository.Cb<ProfileInfoResponse.ProfileData>() {
            @Override
            public void ok(ProfileInfoResponse.ProfileData data) {
                repo.toggleLike(m.id, new MovieRepository.SimpleCb() {
                    @Override public void ok() { loadAllData(); }
                    @Override public void fail(Throwable t) { toast("좋아요 실패"); }
                });
            }
            @Override public void fail(Throwable t) { promptLogin(); }
        });
    }

    private void checkLoginAndReview(Movie m) {
        repo.getProfileInfo(new MovieRepository.Cb<ProfileInfoResponse.ProfileData>() {
            @Override
            public void ok(ProfileInfoResponse.ProfileData data) {
                ReviewDialog.show(getParentFragmentManager(), m);
            }
            @Override public void fail(Throwable t) { promptLogin(); }
        });
    }

    private void promptLogin() {
        toast("로그인이 필요한 서비스입니다.");
        BottomNavigationView bottomNav = requireActivity().findViewById(R.id.bottom_nav);
        if (bottomNav != null) bottomNav.setSelectedItemId(R.id.profileFragment);
    }

    private long lastNavAt = 0;
    private void onBookClick(Movie m) {
        NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment);
        if (navController.getCurrentDestination()==null || navController.getCurrentDestination().getId() != R.id.homeFragment) return;

        long now = SystemClock.elapsedRealtime();
        if (now - lastNavAt < 700) return; // 0.7초 이내 연속 클릭 무시
        lastNavAt = now;

        navController.navigate(R.id.bookFragment);
        BottomNavigationView bottomNav = requireActivity().findViewById(R.id.bottom_nav);
        if (bottomNav != null) bottomNav.setSelectedItemId(R.id.bookFragment);
    }

    private void toast(String msg) {
        if (getContext() != null) Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT).show();
    }
}