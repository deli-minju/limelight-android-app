package com.devmanjoo.limelight.data;

public class ProfileInfoResponse {
    public String status;

    public ProfileData data;
    public static class ProfileData {
        public String nickname, email, profile_img, created_at;
        public int level_num, next_goal, booking_count, unique_movies, total_minutes;
        public String level_name, level_title, level_desc;
        public double percent;
        public long d_day;
    }
}