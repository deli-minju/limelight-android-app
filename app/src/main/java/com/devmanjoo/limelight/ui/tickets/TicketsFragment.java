package com.devmanjoo.limelight.ui.tickets;

import android.os.Bundle;
import android.view.*;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.devmanjoo.limelight.R;
import com.devmanjoo.limelight.data.*;
import com.devmanjoo.limelight.databinding.FragmentTicketsBinding;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class TicketsFragment extends Fragment {
    private FragmentTicketsBinding b;
    private MovieRepository repo;
    private TicketAdapter adapter;

    @Override public View onCreateView(@NonNull LayoutInflater inf, ViewGroup c, Bundle s) {
        b = FragmentTicketsBinding.inflate(inf, c, false);
        repo = new MovieRepository();
        adapter = new TicketAdapter(id -> cancel(id));

        b.rvTickets.setLayoutManager(new LinearLayoutManager(getContext()));
        b.rvTickets.setAdapter(adapter);

        b.btnLoginGo.setOnClickListener(v -> {
            BottomNavigationView bottomNav = requireActivity().findViewById(R.id.bottom_nav);
            if (bottomNav != null) {
                bottomNav.setSelectedItemId(R.id.profileFragment);
            }
        });

        checkLoginAndLoad();
        return b.getRoot();
    }

    private void checkLoginAndLoad() {
        repo.getProfileInfo(new MovieRepository.Cb<ProfileInfoResponse.ProfileData>() {
            @Override
            public void ok(ProfileInfoResponse.ProfileData data) {
                showLoginRequired(false);
                load();
            }
            @Override
            public void fail(Throwable t) {
                showLoginRequired(true);
            }
        });
    }

    private void showLoginRequired(boolean isRequired) {
        if (isRequired) {
            b.layoutLoginRequired.setVisibility(View.VISIBLE);
            b.rvTickets.setVisibility(View.GONE);
            b.tvEmpty.setVisibility(View.GONE); // 로그인 안했으면 빈 메시지도 숨김
        } else {
            b.layoutLoginRequired.setVisibility(View.GONE);
        }
    }

    private void load() {
        repo.getBookings(new MovieRepository.Cb<List<BookingInfo>>() {
            @Override public void ok(List<BookingInfo> data) {
                if (data != null && !data.isEmpty()) {
                    // 데이터가 있을 때: 리스트 보임, 빈 메시지 숨김
                    b.rvTickets.setVisibility(View.VISIBLE);
                    b.tvEmpty.setVisibility(View.GONE);

                    // 정렬 로직
                    Collections.sort(data, new Comparator<BookingInfo>() {
                        final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.KOREA);
                        final Date now = new Date();

                        @Override
                        public int compare(BookingInfo o1, BookingInfo o2) {
                            try {
                                Date d1 = sdf.parse(o1.start_time);
                                Date d2 = sdf.parse(o2.start_time);

                                boolean isPast1 = d1.before(now);
                                boolean isPast2 = d2.before(now);

                                // 상영예정이 상영종료보다 위로
                                if (!isPast1 && isPast2) return -1;
                                if (isPast1 && !isPast2) return 1;

                                // 같은 그룹 내 정렬
                                if (!isPast1) {
                                    // 상영예정: 임박한 순서 (오름차순)
                                    return d1.compareTo(d2);
                                } else {
                                    // 상영종료: 최근 본 순서 (내림차순)
                                    return d2.compareTo(d1);
                                }
                            } catch (Exception e) {
                                return 0;
                            }
                        }
                    });

                    adapter.submit(data);
                } else {
                    // 데이터가 없을 때: 리스트 숨김, 빈 메시지 보임
                    b.rvTickets.setVisibility(View.GONE);
                    b.tvEmpty.setVisibility(View.VISIBLE);
                    adapter.submit(null); // 어댑터 비우기
                }
            }
            @Override public void fail(Throwable t) { toast("내역 불러오기 실패"); }
        });
    }

    private void cancel(int bookingId) {
        repo.cancelBooking(bookingId, new MovieRepository.SimpleCb() {
            @Override public void ok() { toast("취소 완료"); load(); }
            @Override public void fail(Throwable t) { toast("취소 실패"); }
        });
    }
    private void toast(String m){ if(getContext()!=null) Toast.makeText(getContext(), m, Toast.LENGTH_SHORT).show(); }
}