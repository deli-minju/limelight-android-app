package com.devmanjoo.limelight.data;
public class BookingRequest {
    public int showtime_id;
    public int adult;
    public int teen;
    public int pref; // 우대
    public int senior;
    public int total_price;

    public BookingRequest(int showtimeId, int adult, int teen, int pref, int senior, int total) {
        this.showtime_id = showtimeId;
        this.adult = adult;
        this.teen = teen;
        this.pref = pref;
        this.senior = senior;
        this.total_price = total;
    }
}
