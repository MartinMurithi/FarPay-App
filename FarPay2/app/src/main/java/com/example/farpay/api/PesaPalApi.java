package com.example.farpay.api;

import com.example.farpay.models.PaymentRequest;
import com.example.farpay.models.PaymentResponse;
import com.example.farpay.models.Transaction;

import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface PesaPalApi {

    /**
     * Matches: @app.post("/api/v1/payments/initiate")
     * Sends user details to your FastAPI to get the Pesapal redirect URL.
     */
    @POST("api/v1/payments/initiate")
    Call<PaymentResponse> initiatePayment(@Body PaymentRequest payload);

    /**
     * Matches: @app.get("/api/v1/payments/history")
     * Gets all transactions for the History screen.
     */
    @GET("api/v1/payments/history")
    Call<List<Transaction>> getTransactionHistory();

    /**
     * Matches: @app.get("/api/v1/payments/check-status/{merchant_ref}")
     * Checks status of a specific transaction in your DB.
     */
    @GET("api/v1/payments/check-status/{merchant_ref}")
    Call<Map<String, Object>> checkPaymentStatus(@Path("merchant_ref") String ref);

    /**
     * Matches: @app.get("/api/v1/register-ipn")
     * Used once to configure your ngrok URL with Pesapal.
     */
    @GET("api/v1/register-ipn")
    Call<Map<String, Object>> registerIpn(@Query("ngrok_url") String ngrokUrl);

    /**
     * Matches: @app.get("/api/v1/test-auth")
     * Quick check to see if your Python server can talk to Pesapal.
     */
    @GET("api/v1/test-auth")
    Call<Map<String, Object>> testAuth();

    /**
     * Matches: @app.get("/api/v1/bank/mock")
     * Simulates a bank delay.
     */
    @GET("api/v1/bank/mock")
    Call<Map<String, Object>> mockBankTransfer();
}