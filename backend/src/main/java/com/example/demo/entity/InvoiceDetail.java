package com.example.demo.entity;

import java.math.BigDecimal;

import com.example.demo.entity.base.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "invoice_detail")
public class InvoiceDetail extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "invoice_detail_id")
    private Long invoiceDetailId;

    //====================================
    // Invoice Reference
    //====================================

    @Column(name = "invoice_id", nullable = false)
    private Long invoiceId;

    //====================================
    // Booking Detail Reference
    //====================================

    @Column(name = "booking_detail_id")
    private Long bookingDetailId;

    //====================================
    // Add-on Snapshot
    //====================================
    
    @Column(name = "addon_id")
    private Long addonId;

    @Column(name = "addon_name", nullable = false, length = 100)
    private String addonName;

    @Column(name = "addon_price", precision = 10, scale = 2)
    private BigDecimal addonPrice;

    public InvoiceDetail() {
    }

    public Long getInvoiceDetailId() {
        return invoiceDetailId;
    }

    public void setInvoiceDetailId(Long invoiceDetailId) {
        this.invoiceDetailId = invoiceDetailId;
    }

    public Long getInvoiceId() {
        return invoiceId;
    }

    public void setInvoiceId(Long invoiceId) {
        this.invoiceId = invoiceId;
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