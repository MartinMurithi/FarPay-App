package com.example.farpay.models;

import com.google.gson.annotations.SerializedName;

public class PaymentRequest {

    // These @SerializedName tags MUST match your Python PaymentCreate exactly
    @SerializedName("first_name")
    private String firstName;

    @SerializedName("last_name")
    private String lastName;

    @SerializedName("email")
    private String email;

    @SerializedName("amount")
    private float amount;

    @SerializedName("phone")
    private String phone;

    public PaymentRequest(String firstName, String lastName, String email, String phone, float amount) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phone = phone;
        this.amount = amount;
    }

    // Getters and Setters (Optional for Retrofit, but good practice)
    public String getFirstName() { return firstName; }
    public String getLastName() { return lastName; }
    public String getEmail() { return email; }
    public double getAmount() { return amount; }
    public String getPhone() { return phone; }
}