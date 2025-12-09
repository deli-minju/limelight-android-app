package com.devmanjoo.limelight.data;

import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import androidx.annotation.NonNull;
import okhttp3.ResponseBody;
import okhttp3.MultipartBody;
import com.devmanjoo.limelight.BuildConfig;
import android.util.Log;

public class MovieRepository {
    // 보안 - PHP 파일과 약속한 비밀키
    private static final String API_KEY = BuildConfig.LIMELIGHT_KEY;

    public interface Cb<T> {
        void ok(T data);
        void fail(Throwable t);
    }
    public interface SimpleCb {
        void ok();
        void fail(Throwable t);
    }

    private final MovieService api;

    public MovieRepository() {
        api = ApiClient.get().create(MovieService.class);
    }

    private String mapSort(String s) {
        if ("title_asc".equals(s)) return "title_asc";
        if ("popular_desc".equals(s)) return "popular_desc";
        if ("review_desc".equals(s)) return "review_desc";
        if ("release_asc".equals(s)) return "release_asc";
        if ("release_desc".equals(s)) return "release_desc";
        return "release_desc";
    }

    public void getMovies(String scope, String sort, Cb<List<Movie>> cb) {
        api.getMovies(scope, mapSort(sort)).enqueue(new Callback<List<Movie>>() {
            @Override public void onResponse(@NonNull Call<List<Movie>> call, @NonNull Response<List<Movie>> res) {
                if (res.isSuccessful() && res.body() != null) {
                    cb.ok(res.body());
                } else {
                    cb.fail(new Exception("Response error"));
                }
            }
            @Override public void onFailure(@NonNull Call<List<Movie>> call, @NonNull Throwable t) {
                cb.fail(t);
            }
        });
    }

    public void toggleLike(int movieId, SimpleCb cb) {
        api.toggleLike(new MovieIdBody(movieId)).enqueue(new Callback<LikeResponse>() {
            @Override public void onResponse(@NonNull Call<LikeResponse> call, @NonNull Response<LikeResponse> res) {
                if (res.isSuccessful() && res.body()!=null && "success".equals(res.body().status)) cb.ok();
                else cb.fail(new Exception("Like failed"));
            }
            @Override public void onFailure(@NonNull Call<LikeResponse> call, @NonNull Throwable t) { cb.fail(t); }
        });
    }

    public void getReviews(int movieId, Cb<List<Review>> cb) {
        api.getReviews("list", movieId).enqueue(new Callback<List<Review>>() {
            @Override public void onResponse(@NonNull Call<List<Review>> call, @NonNull Response<List<Review>> res) {
                if (res.isSuccessful() && res.body() != null) cb.ok(res.body());
                else cb.fail(new Exception("Response error"));
            }
            @Override public void onFailure(@NonNull Call<List<Review>> call, @NonNull Throwable t) { cb.fail(t); }
        });
    }

    public void postReview(int movieId, String content, SimpleCb cb) {
        ReviewPost body = new ReviewPost();
        body.movie_id = movieId;
        body.content = content;
        api.postReview(body).enqueue(new Callback<SimpleResponse>() {
            @Override public void onResponse(@NonNull Call<SimpleResponse> call, @NonNull Response<SimpleResponse> res) {
                if (res.isSuccessful() && res.body() != null && "success".equals(res.body().status)) cb.ok();
                else cb.fail(new Exception("Post failed"));
            }
            @Override public void onFailure(@NonNull Call<SimpleResponse> call, @NonNull Throwable t) { cb.fail(t); }
        });
    }

    public void cancelBooking(int id, SimpleCb cb) {
        api.cancel(new CancelBody(id)).enqueue(new Callback<SimpleResponse>() {
            @Override public void onResponse(@NonNull Call<SimpleResponse> call, @NonNull Response<SimpleResponse> res) {
                if (res.isSuccessful() && res.body()!=null && "success".equals(res.body().status)) cb.ok();
                else cb.fail(new Exception("cancel fail"));
            }
            @Override public void onFailure(@NonNull Call<SimpleResponse> call, @NonNull Throwable t) { cb.fail(t); }
        });
    }

    public void getBookingMovies(int theaterId, String date, Cb<List<BookingMovie>> cb) {
        api.getBookingMovies("movies", theaterId, date).enqueue(new Callback<List<BookingMovie>>() {
            @Override public void onResponse(@NonNull Call<List<BookingMovie>> call, @NonNull Response<List<BookingMovie>> res) {
                if(res.isSuccessful() && res.body()!=null) cb.ok(res.body());
                else cb.fail(new Exception("Response error"));
            }
            @Override public void onFailure(@NonNull Call<List<BookingMovie>> call, @NonNull Throwable t) { cb.fail(t); }
        });
    }

    public void getTimes(int theaterId, String date, int movieId, Cb<List<ShowTime>> cb) {
        api.getTimes("times", theaterId, date, movieId).enqueue(new Callback<List<ShowTime>>() {
            @Override public void onResponse(@NonNull Call<List<ShowTime>> call, @NonNull Response<List<ShowTime>> res) {
                if(res.isSuccessful() && res.body()!=null) cb.ok(res.body());
                else cb.fail(new Exception("Response error"));
            }
            @Override public void onFailure(@NonNull Call<List<ShowTime>> call, @NonNull Throwable t) { cb.fail(t); }
        });
    }

    public void book(BookingRequest req, SimpleCb cb) {
        api.book(req).enqueue(new Callback<SimpleResponse>() {
            @Override public void onResponse(@NonNull Call<SimpleResponse> call, @NonNull Response<SimpleResponse> res) {
                if(res.isSuccessful() && res.body()!=null && "success".equals(res.body().status)) cb.ok();
                else cb.fail(new Exception("book fail"));
            }
            @Override public void onFailure(@NonNull Call<SimpleResponse> call, @NonNull Throwable t) { cb.fail(t); }
        });
    }

    public void login(String id, String pw, Callback<SimpleResponse> cb) { api.login(id, pw).enqueue(cb); }

    // 회원가입 시 API_KEY를 함께 전달
    public void signUp(String id, String pw, String name, String nick, String email,
                       String year, String month, String day, SimpleCb cb) {
        // 첫 번째 파라미터로 API_KEY 전달
        api.signUp(API_KEY, id, pw, name, nick, email, year, month, day).enqueue(new Callback<SimpleResponse>() {
            @Override public void onResponse(@NonNull Call<SimpleResponse> c, @NonNull Response<SimpleResponse> r) {
                if(r.isSuccessful() && r.body()!=null && "success".equals(r.body().status)) cb.ok();
                else cb.fail(new Exception(r.body() != null ? r.body().message : "가입 실패"));
            }
            @Override public void onFailure(@NonNull Call<SimpleResponse> c, @NonNull Throwable t) { cb.fail(t); }
        });
    }

    public void checkDuplicate(String type, String value, SimpleCb cb) {
        api.checkDuplicate(type, value).enqueue(new Callback<SimpleResponse>() {
            @Override public void onResponse(@NonNull Call<SimpleResponse> c, @NonNull Response<SimpleResponse> r) {
                if(r.isSuccessful() && r.body()!=null && "available".equals(r.body().status)) cb.ok();
                else cb.fail(new Exception("duplicate"));
            }
            @Override public void onFailure(@NonNull Call<SimpleResponse> c, @NonNull Throwable t) { cb.fail(t); }
        });
    }

    public void getProfileInfo(Cb<ProfileInfoResponse.ProfileData> cb){
        api.getProfileInfo().enqueue(new Callback<ProfileInfoResponse>() {
            @Override public void onResponse(@NonNull Call<ProfileInfoResponse> c, @NonNull Response<ProfileInfoResponse> res){
                if(res.isSuccessful() && res.body()!=null && "success".equals(res.body().status))
                    cb.ok(res.body().data);
                else cb.fail(new Exception("profile error"));
            }
            @Override public void onFailure(@NonNull Call<ProfileInfoResponse> c, @NonNull Throwable t){ cb.fail(t); }
        });
    }

    public void logout(SimpleCb cb) {
        api.logout().enqueue(new Callback<ResponseBody>() {
            @Override public void onResponse(@NonNull Call<ResponseBody> c, @NonNull Response<ResponseBody> r) { cb.ok(); }
            @Override public void onFailure(@NonNull Call<ResponseBody> c, @NonNull Throwable t) { cb.fail(t); }
        });
    }

    public void uploadProfile(MultipartBody.Part body, SimpleCb cb){
        api.uploadProfile(body).enqueue(new Callback<SimpleResponse>() {
            @Override public void onResponse(@NonNull Call<SimpleResponse> c, @NonNull Response<SimpleResponse> r) {
                if (r.isSuccessful() && r.body()!=null && "success".equals(r.body().status)) cb.ok();
                else cb.fail(new Exception("upload fail"));
            }
            @Override public void onFailure(@NonNull Call<SimpleResponse> c, @NonNull Throwable t) { cb.fail(t); }
        });
    }

    public void getBookings(Cb<List<BookingInfo>> cb) {
        api.getBookingHistory().enqueue(new Callback<List<BookingInfo>>() {
            @Override public void onResponse(@NonNull Call<List<BookingInfo>> c, @NonNull Response<List<BookingInfo>> r) {
                if (r.isSuccessful() && r.body()!=null) cb.ok(r.body()); else cb.fail(new Exception("history error"));
            }
            @Override public void onFailure(@NonNull Call<List<BookingInfo>> c, @NonNull Throwable t) { cb.fail(t); }
        });
    }

    public void getTheaters(Cb<List<Theater>> cb) {
        api.getTheaters().enqueue(new Callback<List<Theater>>() {
            @Override public void onResponse(@NonNull Call<List<Theater>> call, @NonNull Response<List<Theater>> res) {
                if (res.isSuccessful() && res.body() != null) cb.ok(res.body());
                else cb.fail(new Exception("Theater load fail"));
            }
            @Override public void onFailure(@NonNull Call<List<Theater>> call, @NonNull Throwable t) { cb.fail(t); }
        });
    }

    public void searchMovies(String query, Cb<List<Movie>> cb) {
        api.searchMovies(query).enqueue(new Callback<List<Movie>>() {
            @Override
            public void onResponse(@NonNull Call<List<Movie>> call, @NonNull Response<List<Movie>> res) {
                if (res.isSuccessful() && res.body() != null) cb.ok(res.body());
                else cb.fail(new Exception("Search failed"));
            }
            @Override
            public void onFailure(@NonNull Call<List<Movie>> call, @NonNull Throwable t) { cb.fail(t); }
        });
    }
}