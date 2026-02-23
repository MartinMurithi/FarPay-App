package com.example.farpay.models;

public class Transaction {
    private int id;
    private double amount;
    private String phone;
    private String transaction_ref;
    private String transaction_status;
    private String created_at;

    // Getters
    public double getAmount() { return amount; }
    public String getStatus() { return transaction_status; }
    public String getRef() { return transaction_ref; }

    public String getCreatedAt() { return  created_at;}

    public String getTransactionRef() { return transaction_ref; }

    public String getPhone() {return phone;}
}