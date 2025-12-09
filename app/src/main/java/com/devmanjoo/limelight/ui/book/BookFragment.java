package com.devmanjoo.limelight.ui.book;

import android.graphics.Color;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.*;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.devmanjoo.limelight.R;
import com.devmanjoo.limelight.data.*;
import com.devmanjoo.limelight.databinding.FragmentBookBinding;

import java.text.SimpleDateFormat;
import java.util.*;

public class BookFragment extends Fragment {
    private FragmentBookBinding b;
    private MovieRepository repo;
    private ShowTimeAdapter timeAdapter;
    private BookingMovieAdapter movieAdapter;
    private DateAdapter dateAdapter;
    private int selTheater = -1;
    private String selDate;
    private Integer selMovie;
    private Integer selShowtime;
    private boolean isMorningTime = false;
    private int adult=0, teen=0, pref=0, senior=0;

    private final List<Button> theaterButtons = new ArrayList<>();
    private List<Theater> theaterList = new ArrayList<>();

    @Override public View onCreateView(@NonNull LayoutInflater inf, ViewGroup c, Bundle s) {
        b = FragmentBookBinding.inflate(inf, c, false);
        repo = new MovieRepository();

        b.btnLoginGo.setOnClickListener(v -> {
            BottomNavigationView bottomNav = requireActivity().findViewById(R.id.bottom_nav);
            if (bottomNav != null) bottomNav.setSelectedItemId(R.id.profileFragment);
        });

        initAdapters();
        initDates();
        initCounters();

        b.btnBook.setOnClickListener(v -> book());
        checkLoginAndLoad();

        return b.getRoot();
    }

    private void checkLoginAndLoad() {
        repo.getProfileInfo(new MovieRepository.Cb<ProfileInfoResponse.ProfileData>() {
            @Override
            public void ok(ProfileInfoResponse.ProfileData data) {
                b.layoutLoginRequired.setVisibility(View.GONE);
                b.contentLayout.setVisibility(View.VISIBLE);
                loadTheaters();
            }
            @Override
            public void fail(Throwable t) {
                b.layoutLoginRequired.setVisibility(View.VISIBLE);
                b.contentLayout.setVisibility(View.GONE);
            }
        });
    }

    private void loadTheaters() {
        repo.getTheaters(new MovieRepository.Cb<List<Theater>>() {
            @Override
            public void ok(List<Theater> data) {
                theaterList = data;
                createTheaterButtons();
                if (!theaterList.isEmpty() && selTheater == -1) {
                    selectTheater(theaterList.get(0).id);
                }
            }
            @Override
            public void fail(Throwable t) { toast("극장 목록을 불러오지 못했습니다."); }
        });
    }

    private void createTheaterButtons() {
        b.layoutTheaters.removeAllViews();
        theaterButtons.clear();
        for (Theater t : theaterList) {
            AppCompatButton btn = new AppCompatButton(getContext());
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, dpToPx(40));
            params.setMarginEnd(dpToPx(8));
            btn.setLayoutParams(params);
            btn.setMinWidth(dpToPx(80));
            btn.setText(t.name);
            btn.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
            btn.setPadding(dpToPx(12), 0, dpToPx(12), 0);
            btn.setOnClickListener(v -> selectTheater(t.id));
            b.layoutTheaters.addView(btn);
            theaterButtons.add(btn);
        }
    }

    private void selectTheater(int theaterId) {
        selTheater = theaterId;
        updateTheaterButtonUI();
        loadMovies();
    }

    private void updateTheaterButtonUI() {
        for (int i = 0; i < theaterList.size(); i++) {
            Theater t = theaterList.get(i);
            Button btn = theaterButtons.get(i);
            if (t.id == selTheater) {
                btn.setBackgroundResource(R.drawable.bg_btn_lime_rounded);
                btn.setTextColor(Color.BLACK);
            } else {
                btn.setBackgroundResource(R.drawable.bg_btn_gray_rounded);
                btn.setTextColor(Color.WHITE);
            }
        }
    }

    private void initAdapters() {
        movieAdapter = new BookingMovieAdapter(m -> {
            selMovie = m.id;
            loadTimes();
        });
        b.rvMovies.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        b.rvMovies.setAdapter(movieAdapter);

        // 시간 선택 리스트 가로 스크롤
        timeAdapter = new ShowTimeAdapter((t, isMorning) -> {
            selShowtime = t.id;
            isMorningTime = isMorning;
            calcPrice();
        });
        b.rvTimes.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        b.rvTimes.setAdapter(timeAdapter);
    }

    private void initDates() {
        List<DateItem> dates = new ArrayList<>();
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.KOREA);
        SimpleDateFormat dayFormat = new SimpleDateFormat("d", Locale.KOREA);
        SimpleDateFormat dayOfWeekFormat = new SimpleDateFormat("E", Locale.KOREA);
        selDate = sdf.format(cal.getTime());
        for (int i = 0; i < 7; i++) {
            String fullDate = sdf.format(cal.getTime());
            String day = dayFormat.format(cal.getTime());
            String dayOfWeek = dayOfWeekFormat.format(cal.getTime());
            dates.add(new DateItem(fullDate, day, dayOfWeek, i == 0));
            cal.add(Calendar.DATE, 1);
        }
        dateAdapter = new DateAdapter(dates, date -> { selDate = date; loadMovies(); });
        b.rvDates.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        b.rvDates.setAdapter(dateAdapter);
    }

    // 인원 선택 버튼 로직
    private void initCounters() {
        b.cntAdult.btnMinus.setOnClickListener(v -> { if(checkReady() && adult>0) { adult--; updateCount(); }});
        b.cntAdult.btnPlus.setOnClickListener(v -> { if(checkReady()) { adult++; updateCount(); }});

        b.cntTeen.btnMinus.setOnClickListener(v -> { if(checkReady() && teen>0) { teen--; updateCount(); }});
        b.cntTeen.btnPlus.setOnClickListener(v -> { if(checkReady()) { teen++; updateCount(); }});

        b.cntPref.btnMinus.setOnClickListener(v -> { if(checkReady() && pref>0) { pref--; updateCount(); }});
        b.cntPref.btnPlus.setOnClickListener(v -> { if(checkReady()) { pref++; updateCount(); }});

        b.cntSenior.btnMinus.setOnClickListener(v -> { if(checkReady() && senior>0) { senior--; updateCount(); }});
        b.cntSenior.btnPlus.setOnClickListener(v -> { if(checkReady()) { senior++; updateCount(); }});
    }

    // 모든 항목이 선택되었는지 확인하는 헬퍼
    private boolean checkReady() {
        if (selShowtime == null) {
            toast("상영 시간을 먼저 선택해주세요.");
            return false;
        }
        return true;
    }

    private void updateCount() {
        int totalCnt = adult + teen + pref + senior;
        if (totalCnt > 8) {
            toast("최대 8명까지 예매 가능합니다.");
            return;
        }
        b.cntAdult.tvCount.setText(String.valueOf(adult));
        b.cntTeen.tvCount.setText(String.valueOf(teen));
        b.cntPref.tvCount.setText(String.valueOf(pref));
        b.cntSenior.tvCount.setText(String.valueOf(senior));
        calcPrice();
    }

    private void loadMovies() {
        if (selTheater == -1) return;

        // 영화 로드 시 하위 항목 초기화
        selMovie = null;
        resetTimeAndCount(); // 시간과 인원 초기화

        movieAdapter.submit(null); // 초기화

        repo.getBookingMovies(selTheater, selDate, new MovieRepository.Cb<List<BookingMovie>>() {
            @Override public void ok(List<BookingMovie> data) {
                movieAdapter.submit(data);
                if (data.isEmpty()) toast("상영 정보가 없습니다.");
            }
            @Override public void fail(Throwable t) { toast("영화 불러오기 실패"); }
        });
    }

    private void loadTimes() {
        if (selMovie == null) return;

        // 시간 로드 시 인원 초기화
        resetTimeAndCount();

        repo.getTimes(selTheater, selDate, selMovie, new MovieRepository.Cb<List<ShowTime>>() {
            @Override public void ok(List<ShowTime> data) { timeAdapter.submit(data); }
            @Override public void fail(Throwable t) { toast("시간 불러오기 실패"); }
        });
    }

    // 인원 및 시간 선택 초기화
    private void resetTimeAndCount() {
        selShowtime = null;
        timeAdapter.submit(Collections.emptyList()); // 시간 목록 비우기

        adult=0; teen=0; pref=0; senior=0;
        b.cntAdult.tvCount.setText("0");
        b.cntTeen.tvCount.setText("0");
        b.cntPref.tvCount.setText("0");
        b.cntSenior.tvCount.setText("0");
        b.tvTotal.setText("0원");
    }

    private void calcPrice() {
        if (selShowtime == null) {
            b.tvTotal.setText("0원");
            return;
        }
        int priceAdult = 15000, priceTeen = 12000, pricePref = 5000, priceSenior = 7000;
        if (isMorningTime) {
            priceAdult -= 4000;
            priceTeen -= 4000;
        }
        long total = (long)adult * priceAdult + (long)teen * priceTeen + (long)pref * pricePref + (long)senior * priceSenior;
        b.tvTotal.setText(String.format(Locale.KOREA, "%,d원", total));
    }

    private void book() {
        if (selShowtime == null) { toast("상영 시간을 선택하세요"); return; }
        int totalCnt = adult + teen + pref + senior;
        if (totalCnt <= 0) { toast("인원을 선택하세요"); return; }

        String priceStr = b.tvTotal.getText().toString().replace("원","").replace(",","").trim();
        int totalPrice = Integer.parseInt(priceStr);

        BookingRequest req = new BookingRequest(selShowtime, adult, teen, pref, senior, totalPrice);

        // 성공 시 다이얼로그 호출
        repo.book(req, new MovieRepository.SimpleCb() {
            @Override public void ok() {
                showSuccessDialog();
            }
            @Override public void fail(Throwable t) { toast("예매 실패: " + t.getMessage()); }
        });
    }

    // 성공 팝업
    private void showSuccessDialog() {
        if (getContext() == null) return;

        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(getContext());
        View view = LayoutInflater.from(getContext()).inflate(R.layout.dialog_success, null);
        builder.setView(view);
        builder.setCancelable(false); // 바깥 클릭해도 안 닫히게

        android.app.AlertDialog dialog = builder.create();

        // 배경 투명하게
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        }

        // 확인 버튼 클릭 시 이동
        Button btnConfirm = view.findViewById(R.id.btnConfirm);
        btnConfirm.setOnClickListener(v -> {
            dialog.dismiss();
            moveToTickets(); // 내역 탭으로 이동
        });

        dialog.show();
    }

    // 내역 탭 이동 헬퍼
    private void moveToTickets() {
        if (getActivity() != null) {
            BottomNavigationView bottomNav = getActivity().findViewById(R.id.bottom_nav);
            if (bottomNav != null) {
                bottomNav.setSelectedItemId(R.id.ticketsFragment);
            }
        }
    }

    private void toast(String msg){ if(getContext()!=null) Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT).show(); }
    private int dpToPx(int dp) { return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, getResources().getDisplayMetrics()); }

    private static class DateAdapter extends RecyclerView.Adapter<DateAdapter.VH> {
        interface OnDateClick { void onClick(String fullDate); }
        private List<DateItem> items;
        private OnDateClick listener;
        private int selectedPos = 0;

        DateAdapter(List<DateItem> items, OnDateClick listener) {
            this.items = items;
            this.listener = listener;
        }

        @NonNull @Override public VH onCreateViewHolder(@NonNull ViewGroup p, int v) {
            View view = LayoutInflater.from(p.getContext()).inflate(R.layout.item_date, p, false);
            return new VH(view);
        }

        @Override public void onBindViewHolder(@NonNull VH h, int pos) {
            DateItem item = items.get(pos);
            h.tvDayOfWeek.setText(item.dayOfWeek);
            h.tvDay.setText(item.day);

            boolean isSelected = (pos == selectedPos);
            h.itemView.setBackgroundResource(isSelected ? R.drawable.bg_btn_lime_rounded : R.drawable.bg_btn_gray_rounded);
            int textColor = isSelected ? Color.BLACK : Color.WHITE;
            h.tvDayOfWeek.setTextColor(textColor);
            h.tvDay.setTextColor(textColor);

            h.itemView.setOnClickListener(v -> {
                int currentPos = h.getBindingAdapterPosition();
                if (currentPos != RecyclerView.NO_POSITION) {
                    int prev = selectedPos;
                    selectedPos = currentPos;
                    notifyItemChanged(prev);
                    notifyItemChanged(selectedPos);
                    listener.onClick(items.get(currentPos).fullDate);
                }
            });
        }

        @Override public int getItemCount() { return items.size(); }

        static class VH extends RecyclerView.ViewHolder {
            TextView tvDayOfWeek, tvDay;
            VH(View v) {
                super(v);
                tvDayOfWeek = v.findViewById(R.id.tvDayOfWeek);
                tvDay = v.findViewById(R.id.tvDay);
            }
        }
    }

    private static class DateItem {
        String fullDate, day, dayOfWeek;
        boolean isSelected;
        DateItem(String f, String d, String w, boolean s) { fullDate=f; day=d; dayOfWeek=w; isSelected=s; }
    }
}