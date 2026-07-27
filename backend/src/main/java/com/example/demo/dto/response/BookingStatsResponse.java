package com.example.demo.dto.response;

public class BookingStatsResponse {

    private long total;
    private long todayBookings;

    private long pending;
    private long confirmed;
    private long cancelled;
    private long completed;

    public BookingStatsResponse() {
    }

    public long getTotal() {
        return total;
    }

    public void setTotal(long total) {
        this.total = total;
    }

    public long getTodayBookings() {
        return todayBookings;
    }

    public void setTodayBookings(long todayBookings) {
        this.todayBookings = todayBookings;
    }

    public long getPending() {
        return pending;
    }

    public void setPending(long pending) {
        this.pending = pending;
    }

    public long getConfirmed() {
        return confirmed;
    }

    public void setConfirmed(long confirmed) {
        this.confirmed = confirmed;
    }

    public long getCancelled() {
        return cancelled;
    }

    public void setCancelled(long cancelled) {
        this.cancelled = cancelled;
    }

    public long getCompleted() {
        return completed;
    }

    public void setCompleted(long completed) {
        this.completed = completed;
    }
}