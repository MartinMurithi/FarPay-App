package com.example.farpay.models;

public class PaymentResponse {
    private String reference;
    private String redirect_url;
    private String status;

    // Getters
    public String getRedirectUrl() { return redirect_url; }
    public String getReference() { return reference; }
    public String getStatus() { return status; }
}
