package com.devmanjoo.limelight.data;

import java.util.List;
import retrofit2.Call;
import retrofit2.http.*;
import okhttp3.ResponseBody;
import okhttp3.MultipartBody;

public interface MovieService {
    @FormUrlEncoded @POST("api/login_process.php")
    Call<SimpleResponse> login(@Field("userid") String id, @Field("password") String pw);

    @FormUrlEncoded
    @POST("api/register_process.php")
    Call<SimpleResponse> signUp(
            @Field("api_key") String apiKey,
            @Field("userid") String id,
            @Field("password") String pw,
            @Field("name") String name,
            @Field("nickname") String nick,
            @Field("email") String email,
            @Field("birth_year") String year,
            @Field("birth_month") String month,
            @Field("birth_day") String day
    );
    @GET("api/check_duplicate.php")
    Call<SimpleResponse> checkDuplicate(@Query("type") String type, @Query("value") String value);

    @GET("api/movies_list.php")
    Call<List<Movie>> getMovies(@Query("scope") String scope, @Query("order") String order);

    @POST("api/like_process.php")
    Call<LikeResponse> toggleLike(@Body MovieIdBody body);

    @GET("api/get_booking_options.php?type=theaters")
    Call<List<Theater>> getTheaters();

    @GET("api/get_booking_options.php")
    Call<List<BookingMovie>> getBookingMovies(@Query("type") String type,
                                              @Query("theater") int theaterId,
                                              @Query("date") String date);

    @GET("api/get_booking_options.php")
    Call<List<ShowTime>> getTimes(@Query("type") String type,
                                  @Query("theater") int theaterId,
                                  @Query("date") String date,
                                  @Query("movie") int movieId);

    @POST("api/booking_process.php")
    Call<SimpleResponse> book(@Body BookingRequest body);

    @POST("api/cancel_booking.php")
    Call<SimpleResponse> cancel(@Body CancelBody body);

    @GET("api/review_process.php")
    Call<List<Review>> getReviews(@Query("mode") String mode,
                                  @Query("movie_id") int id);

    @POST("api/review_process.php")
    Call<SimpleResponse> postReview(@Body ReviewPost body);

    @GET("api/profile_info.php")
    Call<ProfileInfoResponse> getProfileInfo();

    @GET("logout.php")
    Call<ResponseBody> logout();

    @Multipart @POST("api/upload_profile.php")
    Call<SimpleResponse> uploadProfile(@Part MultipartBody.Part profile_image);

    @GET("api/get_booking_history.php")
    Call<List<BookingInfo>> getBookingHistory();

    @GET("api/search_movies.php")
    Call<List<Movie>> searchMovies(@Query("q") String query);
}