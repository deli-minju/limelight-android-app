package com.devmanjoo.limelight.ui.profile;

import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.devmanjoo.limelight.R;
import com.devmanjoo.limelight.data.MovieRepository;
import com.devmanjoo.limelight.databinding.FragmentSignupBinding;

import java.util.regex.Pattern;

public class SignupFragment extends Fragment {
    private FragmentSignupBinding b;
    private MovieRepository repo;

    private boolean isIdChecked = false;
    private boolean isNickChecked = false;

    // 이메일 정규식 패턴 xxx@xxx.xxx 형태 강제
    private static final String EMAIL_PATTERN = "^[A-Za-z0-9_.-]+@[A-Za-z0-9-]+\\.[A-Za-z0-9-]+";

    @Override public View onCreateView(@NonNull LayoutInflater inf, ViewGroup c, Bundle s) {
        b = FragmentSignupBinding.inflate(inf, c, false);
        repo = new MovieRepository();

        setupFocusListeners();

        // 아이디 입력 감지
        b.etId.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override public void afterTextChanged(Editable s) {
                isIdChecked = false;
                b.btnCheckId.setText("중복확인");
                b.btnCheckId.setBackgroundResource(R.drawable.bg_btn_gray_rounded);
                b.btnCheckId.setTextColor(Color.WHITE);
            }
        });

        // 닉네임 입력 감지
        b.etNickname.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override public void afterTextChanged(Editable s) {
                isNickChecked = false;
                b.btnCheckNick.setText("중복확인");
                b.btnCheckNick.setBackgroundResource(R.drawable.bg_btn_gray_rounded);
                b.btnCheckNick.setTextColor(Color.WHITE);
            }
        });

        // 비밀번호 일치 확인
        TextWatcher passwordWatcher = new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override public void afterTextChanged(Editable s) {
                checkPasswordMatch();
            }
        };
        b.etPw.addTextChangedListener(passwordWatcher);
        b.etPwConfirm.addTextChangedListener(passwordWatcher);

        // 이메일 형식 실시간 확인
        b.etEmail.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override public void afterTextChanged(Editable s) {
                String email = s.toString().trim();
                if (email.isEmpty()) {
                    b.lineEmail.setBackgroundResource(R.color.textLight);
                    return;
                }

                // 정규식 검사
                if (Pattern.matches(EMAIL_PATTERN, email)) {
                    b.lineEmail.setBackgroundColor(Color.parseColor("#cfff04")); // 성공
                } else {
                    b.lineEmail.setBackgroundColor(Color.parseColor("#F33F3F")); // 실패
                }
            }
        });

        b.btnCheckId.setOnClickListener(v -> checkDuplicate("userid", b.etId.getText().toString().trim()));
        b.btnCheckNick.setOnClickListener(v -> checkDuplicate("nickname", b.etNickname.getText().toString().trim()));
        b.btnSignUp.setOnClickListener(v -> submit());

        b.tvLogin.setOnClickListener(v -> Navigation.findNavController(requireActivity(), R.id.nav_host_fragment).navigate(R.id.profileFragment));

        b.getRoot().setOnTouchListener((v, e) -> {
            b.getRoot().clearFocus();
            return false;
        });

        return b.getRoot();
    }

    private void checkPasswordMatch() {
        String pw = b.etPw.getText().toString();
        String pw2 = b.etPwConfirm.getText().toString();

        if (pw2.isEmpty()) {
            b.tvPwMsg.setText("");
            return;
        }

        if (pw.equals(pw2)) {
            b.tvPwMsg.setText("비밀번호가 일치합니다.");
            b.tvPwMsg.setTextColor(Color.parseColor("#cfff04"));
        } else {
            b.tvPwMsg.setText("비밀번호가 일치하지 않습니다.");
            b.tvPwMsg.setTextColor(Color.parseColor("#F33F3F"));
        }
    }

    private void setupFocusListeners() {
        b.etId.setOnFocusChangeListener((v,f)-> b.lineId.setBackgroundResource(f ? R.color.navLime : R.color.textLight));
        b.etPw.setOnFocusChangeListener((v,f)-> b.linePw.setBackgroundResource(f ? R.color.navLime : R.color.textLight));
        b.etPwConfirm.setOnFocusChangeListener((v,f)-> b.linePwConfirm.setBackgroundResource(f ? R.color.navLime : R.color.textLight));
        b.etNickname.setOnFocusChangeListener((v,f)-> b.lineNickname.setBackgroundResource(f ? R.color.navLime : R.color.textLight));
        b.etEmail.setOnFocusChangeListener((v,f)-> {
            if(!f) b.lineEmail.setBackgroundResource(R.color.textLight);
        });
    }

    private void checkDuplicate(String type, String value) {
        if (value.isEmpty()) {
            toast("내용을 입력해주세요.");
            return;
        }

        repo.checkDuplicate(type, value, new MovieRepository.SimpleCb() {
            @Override
            public void ok() {
                toast("사용 가능합니다.");
                if (type.equals("userid")) {
                    isIdChecked = true;
                    b.btnCheckId.setText("확인완료");
                    b.btnCheckId.setBackgroundResource(R.drawable.bg_btn_lime_rounded);
                    b.btnCheckId.setTextColor(Color.BLACK);
                } else {
                    isNickChecked = true;
                    b.btnCheckNick.setText("확인완료");
                    b.btnCheckNick.setBackgroundResource(R.drawable.bg_btn_lime_rounded);
                    b.btnCheckNick.setTextColor(Color.BLACK);
                }
            }

            @Override
            public void fail(Throwable t) {
                toast("이미 사용 중입니다.");
            }
        });
    }

    private void submit() {
        String id = b.etId.getText().toString().trim();
        String pw = b.etPw.getText().toString().trim();
        String pw2 = b.etPwConfirm.getText().toString().trim();
        String name = b.etName.getText().toString().trim();
        String nick = b.etNickname.getText().toString().trim();
        String email = b.etEmail.getText().toString().trim();
        String y = b.etYear.getText().toString().trim();
        String m = b.etMonth.getText().toString().trim();
        String d = b.etDay.getText().toString().trim();

        if (id.isEmpty() || pw.isEmpty() || name.isEmpty() || nick.isEmpty() || email.isEmpty() || y.isEmpty() || m.isEmpty() || d.isEmpty()) {
            toast("모든 항목을 입력하세요.");
            return;
        }

        if (!isIdChecked) { toast("아이디 중복 확인을 해주세요."); return; }
        if (!isNickChecked) { toast("닉네임 중복 확인을 해주세요."); return; }
        if (!pw.equals(pw2)) { toast("비밀번호가 일치하지 않습니다."); return; }

        // 이메일 형식 검사
        if (!Pattern.matches(EMAIL_PATTERN, email)) {
            toast("올바른 이메일 형식을 입력해주세요.");
            return;
        }

        repo.signUp(id, pw, name, nick, email, y, m, d, new MovieRepository.SimpleCb() {
            @Override
            public void ok() {
                toast("회원가입 완료! 로그인 해주세요.");
                Navigation.findNavController(requireActivity(), R.id.nav_host_fragment)
                        .navigate(R.id.profileFragment);
            }

            @Override
            public void fail(Throwable t) {
                toast("가입 실패: " + t.getMessage());
            }
        });
    }

    private void toast(String m){ Toast.makeText(getContext(), m, Toast.LENGTH_SHORT).show(); }
}