package com.devmanjoo.limelight.ui.profile;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.*;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import com.devmanjoo.limelight.data.MovieRepository;
import com.devmanjoo.limelight.databinding.FragmentProfileBinding;
import com.devmanjoo.limelight.data.SimpleResponse;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import androidx.navigation.Navigation;
import com.devmanjoo.limelight.R;
import com.bumptech.glide.Glide;
import com.devmanjoo.limelight.data.ProfileInfoResponse;
import java.io.InputStream;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import android.net.Uri;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import org.apache.commons.io.IOUtils;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import androidx.core.content.ContextCompat;

public class ProfileFragment extends Fragment {
    private FragmentProfileBinding b;
    private MovieRepository repo;

    private final ActivityResultLauncher<String> pickImage =
            registerForActivityResult(new ActivityResultContracts.GetContent(), this::uploadProfile);

    @Override public View onCreateView(@NonNull LayoutInflater inf, ViewGroup c, Bundle s) {
        b = FragmentProfileBinding.inflate(inf, c, false);
        repo = new MovieRepository();

        b.btnChangePhoto.setOnClickListener(v -> pickImage.launch("image/*"));

        b.etId.setOnFocusChangeListener((v,f)-> b.lineId.setBackgroundResource(f ? R.color.navLime : R.color.textLight));
        b.etPw.setOnFocusChangeListener((v,f)-> b.linePw.setBackgroundResource(f ? R.color.navLime : R.color.textLight));

        b.btnLogin.setOnClickListener(v -> doLogin());
        b.tvSignup.setOnClickListener(v ->
                Navigation.findNavController(requireActivity(), R.id.nav_host_fragment)
                        .navigate(R.id.signupFragment));

        // 화면 밖 터치 시 포커스 해제
        b.getRoot().setOnTouchListener((v, e) -> {
            b.getRoot().clearFocus();
            return false;
        });

        b.tvLogout.setOnClickListener(v -> repo.logout(new MovieRepository.SimpleCb() {
            @Override public void ok() {
                b.profileGroup.setVisibility(View.GONE);
                b.loginGroup.setVisibility(View.VISIBLE);
                // 로그아웃 시 입력창 초기화
                b.etId.setText("");
                b.etPw.setText("");
                toast("로그아웃 되었습니다.");
            }
            @Override public void fail(Throwable t) { toast("로그아웃 실패"); }
        }));

        // 화면이 생성될 때 로그인 상태 확인
        checkLoginStatus();

        return b.getRoot();
    }

    // 로그인 상태 확인 메서드
    private void checkLoginStatus() {
        // 프로필 정보 요청
        repo.getProfileInfo(new MovieRepository.Cb<ProfileInfoResponse.ProfileData>() {
            @Override
            public void ok(ProfileInfoResponse.ProfileData data) {
                // 성공
                setProfileData(data);
                b.loginGroup.setVisibility(View.GONE);
                b.profileGroup.setVisibility(View.VISIBLE);
            }

            @Override
            public void fail(Throwable t) {
                // 실패
                b.loginGroup.setVisibility(View.VISIBLE);
                b.profileGroup.setVisibility(View.GONE);
            }
        });
    }

    private void doLogin() {
        String id = b.etId.getText().toString().trim();
        String pw = b.etPw.getText().toString().trim();
        if (TextUtils.isEmpty(id) || TextUtils.isEmpty(pw)) { toast("ID/PW 입력"); return; }

        repo.login(id, pw, new Callback<SimpleResponse>() {
            @Override
            public void onResponse(@NonNull Call<SimpleResponse> call, @NonNull Response<SimpleResponse> res) {
                if (res.isSuccessful() && res.body() != null) {
                    if ("success".equals(res.body().status)) {
                        loadProfile();
                    } else {
                        String msg = res.body().message;
                        toast(msg != null ? msg : "로그인 실패");
                    }
                } else {
                    toast("서버 오류: " + res.code());
                }
            }

            @Override
            public void onFailure(@NonNull Call<SimpleResponse> call, @NonNull Throwable t) {
                toast("오류 발생: " + t.getMessage());
                t.printStackTrace();
            }
        });
    }

    private void uploadProfile(Uri uri) {
        try (InputStream is = requireContext().getContentResolver().openInputStream(uri)) {
            byte[] bytes = IOUtils.toByteArray(is);
            RequestBody reqFile = RequestBody.create(bytes, MediaType.parse("image/*"));
            MultipartBody.Part body = MultipartBody.Part.createFormData("profile_image", "upload.jpg", reqFile);
            repo.uploadProfile(body, new MovieRepository.SimpleCb() {
                @Override public void ok() {
                    toast("프로필 사진이 변경되었습니다.");
                    loadProfile(); // 다시 정보를 불러오며 setProfileData가 실행되어 원형으로 뜸
                }
                @Override public void fail(Throwable t) { toast("변경 실패"); }
            });
        } catch (Exception e) { toast("이미지 읽기 실패"); }
    }

    private void loadProfile() {
        repo.getProfileInfo(new MovieRepository.Cb<ProfileInfoResponse.ProfileData>() {
            @Override public void ok(ProfileInfoResponse.ProfileData d) {
                b.loginGroup.setVisibility(View.GONE);
                b.profileGroup.setVisibility(View.VISIBLE);
                setProfileData(d);
            }
            @Override public void fail(Throwable t) { toast("프로필 불러오기 실패"); }
        });
    }

    // 프로필 데이터를 UI에 적용
    private void setProfileData(ProfileInfoResponse.ProfileData d) {
        String dDayPart = d.d_day + "일";
        String fullText = d.nickname + "님의 일상을 비춘 지 '" + dDayPart + "'이 지났어요.";
        SpannableStringBuilder ssb = new SpannableStringBuilder(fullText);
        int start = fullText.indexOf(dDayPart);
        int end = start + dDayPart.length();

        // Context가 null일 경우를 대비한 안전 장치
        if (getContext() != null) {
            int limeColor = ContextCompat.getColor(requireContext(), R.color.navLime);
            ssb.setSpan(new ForegroundColorSpan(limeColor), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        b.tvDday.setText(ssb);

        b.tvLevelBadge.setText("Lv." + d.level_num + " " + d.level_name);
        b.tvLevelTitle.setText(d.level_title);
        b.tvLevelDesc.setText(d.level_desc);
        int pct = (int)Math.round(d.percent);
        b.tvPercent.setText(pct + "%");
        b.progressBar.setProgress(pct);
        b.tvExpMsg.setText(d.next_goal > 0
                ? "레벨업까지 " + (d.next_goal - d.booking_count) + "편 남았습니다."
                : "최고 레벨을 달성했습니다!");

        int hours = d.total_minutes / 60;
        int mins  = d.total_minutes % 60;
        b.tvStatsTime.setText(hours + "시간 " + mins + "분");
        b.tvStatsCount.setText(d.unique_movies + "편");

        b.tvNickname.setText(d.nickname);
        b.tvEmail.setText(d.email);

        if (getContext() != null) {
            Glide.with(requireContext())
                    .load("http://devmanjoo.mycafe24.com/" + d.profile_img)
                    .placeholder(R.drawable.ic_placeholder)
                    .into(b.ivProfile);
        }
    }

    private void toast(String m){
        if (getContext() != null) Toast.makeText(getContext(), m, Toast.LENGTH_SHORT).show();
    }
}
