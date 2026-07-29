package com.example.demo.dto.response;

import java.util.ArrayList;
import java.util.List;

/** One page of invoices for the staff Invoices screen. */
public class InvoicePageResponse {

    private List<InvoiceResponse> invoices = new ArrayList<>();

    private int currentPage;
    private int pageSize;
    private long totalElements;
    private int totalPages;
    private boolean first;
    private boolean last;

    public InvoicePageResponse() {
    }

    public List<InvoiceResponse> getInvoices() { return invoices; }
    public void setInvoices(List<InvoiceResponse> invoices) { this.invoices = invoices; }

    public int getCurrentPage() { return currentPage; }
    public void setCurrentPage(int currentPage) { this.currentPage = currentPage; }

    public int getPageSize() { return pageSize; }
    public void setPageSize(int pageSize) { this.pageSize = pageSize; }

    public long getTotalElements() { return totalElements; }
    public void setTotalElements(long totalElements) { this.totalElements = totalElements; }

    public int getTotalPages() { return totalPages; }
    public void setTotalPages(int totalPages) { this.totalPages = totalPages; }

    public boolean isFirst() { return first; }
    public void setFirst(boolean first) { this.first = first; }

    public boolean isLast() { return last; }
    public void setLast(boolean last) { this.last = last; }
}
