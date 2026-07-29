package com.example.demo.dto.response;

import java.math.BigDecimal;

import com.example.demo.entity.BookingDetail;

/**
 * One add-on line of a booking.
 */
public class BookingAddonResponse {

    private Long bookingDetailId;
    private Long addonId;
    private String addonName;
    private BigDecimal addonPrice;

    public BookingAddonResponse() {
    }

    /** Convert an entity to this DTO. */
    public static BookingAddonResponse fromEntity(BookingDetail detail) {

        BookingAddonResponse response = new BookingAddonResponse();

        response.setBookingDetailId(detail.getBookingDetailId());
        response.setAddonId(detail.getAddonId());
        response.setAddonName(detail.getAddonName());
        response.setAddonPrice(detail.getAddonPrice());

        return response;
    }

    public Long getBookingDetailId() {
        return bookingDetailId;
    }

    public void setBookingDetailId(Long bookingDetailId) {
        this.bookingDetailId = bookingDetailId;
    }

    public Long getAddonId() {
        return addonId;
    }

    public void setAddonId(Long addonId) {
        this.addonId = addonId;
    }

    public String getAddonName() {
        return addonName;
    }

    public void setAddonName(String addonName) {
        this.addonName = addonName;
    }

    public BigDecimal getAddonPrice() {
        return addonPrice;
    }

    public void setAddonPrice(BigDecimal addonPrice) {
        this.addonPrice = addonPrice;
    }
}
