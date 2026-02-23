package com.example.farpay;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.farpay.api.PesaPalApi;
import com.example.farpay.api.RetrofitClient;
import com.example.farpay.databinding.FragmentPayStep2Binding;
import com.example.farpay.databinding.ItemPayMethodBinding;
import com.example.farpay.models.PaymentRequest;
import com.example.farpay.models.PaymentResponse;

import java.text.NumberFormat;
import java.util.Locale;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PayStep2Fragment extends Fragment {

    private FragmentPayStep2Binding binding;
    private ItemPayMethodBinding pesapalBinding;
    private ItemPayMethodBinding visaBinding;
    private ItemPayMethodBinding bankBinding;

    private String fName, lName, email, phone;
    private double amount;

    @Nullable
    private String selectedMethod = null;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            // Ensure these match the keys sent from Fragment 1 exactly
            fName = getArguments().getString("fname", "");
            lName = getArguments().getString("lname", "");
            email = getArguments().getString("email", "");
            phone = getArguments().getString("phone", "");

            // If NavGraph uses float, we catch it as a float then convert to double
            amount = getArguments().getFloat("amount", 0.0f);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentPayStep2Binding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        populateSummary();
        setupMethodCards();
        setupFooter();
    }

    private void populateSummary() {
        NumberFormat fmt = NumberFormat.getNumberInstance(Locale.US);
        fmt.setMinimumFractionDigits(2);
        String formatted = "KES " + fmt.format(amount);

        binding.tvSummaryName.setText(fName + " " + lName);
        binding.tvSummaryAmount.setText(formatted);
        binding.tvSummaryTotal.setText(formatted);
    }

    private void setupMethodCards() {
        // 1. INITIALIZE the bindings first
        pesapalBinding = ItemPayMethodBinding.bind(binding.cardPesapal.getRoot());
//        visaBinding    = ItemPayMethodBinding.bind(binding.cardVisa.getRoot());
        bankBinding    = ItemPayMethodBinding.bind(binding.cardBank.getRoot());

        // Setup PesaPal
        pesapalBinding.ivMethodIcon.setImageResource(R.drawable.ic_pesapal);
        pesapalBinding.tvMethodName.setText("PesaPal");
        pesapalBinding.methodRoot.setOnClickListener(v -> selectMethod("pesapal"));

        // Setup Bank
        bankBinding.ivMethodIcon.setImageResource(R.drawable.ic_bank);
        bankBinding.tvMethodName.setText("Bank Transfer");
        bankBinding.methodRoot.setOnClickListener(v -> selectMethod("bank"));
    }

    private void selectMethod(@NonNull String method) {
        selectedMethod = method;

        // Visual feedback
        updateCardVisuals(method);

        binding.rowSelectedMethod.setVisibility(View.VISIBLE);
        binding.tvSelectedName.setText(method.toUpperCase());
        binding.btnPayNow.setEnabled(true);
    }

    private void updateCardVisuals(String method) {
        Drawable selectedBg = ContextCompat.getDrawable(requireContext(), R.drawable.bg_method_selected);
        Drawable defaultBg = ContextCompat.getDrawable(requireContext(), R.drawable.bg_method_default);
        Drawable radioActive = ContextCompat.getDrawable(requireContext(), R.drawable.bg_step_active);
        Drawable radioInactive = ContextCompat.getDrawable(requireContext(), R.drawable.bg_step_inactive);

        if (pesapalBinding != null) {
            applyCardState(pesapalBinding, "pesapal".equals(method), selectedBg, defaultBg, radioActive, radioInactive);
        }
        if (bankBinding != null) {
            applyCardState(bankBinding, "bank".equals(method), selectedBg, defaultBg, radioActive, radioInactive);
        }
    }

    private void applyCardState(ItemPayMethodBinding cardBinding, boolean isSelected, Drawable sel, Drawable def, Drawable radA, Drawable radI) {
        cardBinding.methodRoot.setBackground(isSelected ? sel : def);
        cardBinding.radioDot.setBackground(isSelected ? radA : radI);
    }

    private void setupFooter() {
        binding.btnPayNow.setOnClickListener(v -> onPayNowClicked());
        binding.tvChange.setOnClickListener(v -> clearSelection());
    }

    private void onPayNowClicked() {
        if (selectedMethod == null) {
            Toast.makeText(getContext(), "Please select a payment method", Toast.LENGTH_SHORT).show();
            return;
        }

        PesaPalApi api = RetrofitClient.getClient().create(PesaPalApi.class);
        setLoading(true);

        if (selectedMethod.equals("pesapal") || selectedMethod.equals("card")) {
            // --- PESAPAL FLOW ---
            // Ensure your PaymentRequest constructor uses (String, String, String, String, double/float)
            PaymentRequest request = new PaymentRequest(fName, lName, email, phone, (float) amount);

            api.initiatePayment(request).enqueue(new Callback<PaymentResponse>() {
                @Override
                public void onResponse(Call<PaymentResponse> call, Response<PaymentResponse> response) {
                    setLoading(false);
                    if (response.isSuccessful() && response.body() != null) {
                        Bundle bundle = new Bundle();
                        bundle.putString("payment_url", response.body().getRedirectUrl());
                        Navigation.findNavController(requireView())
                                .navigate(R.id.action_payStep2_to_webView, bundle);
                    } else {
                        showError("FastAPI Error: " + response.code() + " - " + response.message());
                    }
                }

                @Override
                public void onFailure(Call<PaymentResponse> call, Throwable t) {
                    setLoading(false);
                    showError("Network Error: Check your ngrok connection");
                }
            });

        } else if (selectedMethod.equals("bank")) {
            // --- BANK API FLOW ---
            binding.btnPayNow.setText("Verifying with Bank...");

            api.mockBankTransfer().enqueue(new Callback<Map<String, Object>>() {
                @Override
                public void onResponse(Call<Map<String, Object>> call, Response<Map<String, Object>> response) {
                    setLoading(false);
                    if (response.isSuccessful() && response.body() != null) {
                        // This triggers after the Python time.sleep(3) finishes
                        Object messageObj = response.body().get("message");
                        String msg = (messageObj != null) ? messageObj.toString() : "Bank Transfer Successful";

                        Toast.makeText(getContext(), "🏦 " + msg, Toast.LENGTH_LONG).show();

                        // Exit the Payment Activity and return home
                        if (getActivity() != null) {
                            getActivity().finish();
                        }
                    } else {
                        showError("Bank gateway is currently unavailable");
                    }
                }

                @Override
                public void onFailure(Call<Map<String, Object>> call, Throwable t) {
                    setLoading(false);
                    showError("Bank Server Connection Failed");
                }
            });
        }
    }
    private void setLoading(boolean isLoading) {
        binding.progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        binding.btnPayNow.setEnabled(!isLoading);
        binding.btnPayNow.setText(isLoading ? "Processing..." : "Pay Now");
    }

    private void showError(String error) {
        Toast.makeText(getContext(), error, Toast.LENGTH_LONG).show();
    }

    private void clearSelection() {
        selectedMethod = null;
        binding.btnPayNow.setEnabled(false);
        binding.rowSelectedMethod.setVisibility(View.GONE);
        updateCardVisuals(null);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
        pesapalBinding = null;
        bankBinding = null;
    }
}