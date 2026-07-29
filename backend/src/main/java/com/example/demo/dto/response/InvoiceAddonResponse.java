package com.example.demo.dto.response;

import java.math.BigDecimal;

import com.example.demo.entity.InvoiceDetail;

/** One add-on line on an invoice. */
public class InvoiceAddonResponse {

    private Long invoiceDetailId;
    private Long addonId;
    private String addonName;
    private BigDecimal addonPrice;

    public InvoiceAddonResponse() {
    }

    public static InvoiceAddonResponse fromEntity(InvoiceDetail detail) {

        InvoiceAddonResponse response = new InvoiceAddonResponse();

        response.setInvoiceDetailId(detail.getInvoiceDetailId());
        response.setAddonId(detail.getAddonId());
        response.setAddonName(detail.getAddonName());
        response.setAddonPrice(detail.getAddonPrice());

        return response;
    }

    public Long getInvoiceDetailId() { return invoiceDetailId; }
    public void setInvoiceDetailId(Long invoiceDetailId) { this.invoiceDetailId = invoiceDetailId; }

    public Long getAddonId() { return addonId; }
    public void setAddonId(Long addonId) { this.addonId = addonId; }

    public String getAddonName() { return addonName; }
    public void setAddonName(String addonName) { this.addonName = addonName; }

    public BigDecimal getAddonPrice() { return addonPrice; }
    public void setAddonPrice(BigDecimal addonPrice) { this.addonPrice = addonPrice; }
}
