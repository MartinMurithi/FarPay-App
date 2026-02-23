package com.example.farpay;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.example.farpay.R;
import com.example.farpay.databinding.FragmentPayStep1Binding;

public class PayStep1Fragment extends Fragment {

    private FragmentPayStep1Binding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentPayStep1Binding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        setupQuickAmountChips();
        binding.btnContinue.setOnClickListener(v -> onContinue());
    }

    // ─── Quick-select chips ──────────────────────────────────────────────────
    private void setupQuickAmountChips() {
        binding.chip500.setOnClickListener(v -> fillAmount("500"));
        binding.chip1000.setOnClickListener(v -> fillAmount("1000"));
        binding.chip2500.setOnClickListener(v -> fillAmount("2500"));
        binding.chip5000.setOnClickListener(v -> fillAmount("5000"));
        binding.chip10000.setOnClickListener(v -> fillAmount("10000"));
    }

    private void fillAmount(String value) {
        binding.etAmount.setText(value);
        binding.etAmount.setSelection(value.length());
    }

    // ─── Navigation ──────────────────────────────────────────────────────────

    private void onContinue() {
        if (!validate()) return;

        // 1. Extract all the data from the UI
        String firstName   = binding.etFirstName.getText().toString().trim();
        String lastName    = binding.etLastName.getText().toString().trim();
        String email       = binding.etEmail.getText().toString().trim();
        String phone       = binding.etPhone.getText().toString().trim();
        String amountStr   = binding.etAmount.getText().toString().trim();
        String description = binding.etDescription.getText().toString().trim();

        float amountValue = 0;
        try {
            amountValue = Float.parseFloat(amountStr);
        } catch (NumberFormatException ignored) {}

        // 2. Create the Bundle with keys that match Step 2's expectations
        Bundle args = new Bundle();
        args.putString("fname",       firstName);
        args.putString("lname",       lastName);
        args.putString("email",       email);
        args.putString("phone",       phone);
        args.putFloat("amount",       amountValue);
        args.putString("description", description);

        // 3. Navigate
        NavHostFragment.findNavController(this)
                .navigate(R.id.action_step1_to_step2, args);
    }

    // ─── Validation ──────────────────────────────────────────────────────────

    private boolean validate() {
        boolean ok = true;

        // First name
        if (TextUtils.isEmpty(binding.etFirstName.getText())) {
            binding.tilFirstName.setError(getString(R.string.error_field_required));
            ok = false;
        } else {
            binding.tilFirstName.setError(null);
        }

        // Last name
        if (TextUtils.isEmpty(binding.etLastName.getText())) {
            binding.tilLastName.setError(getString(R.string.error_field_required));
            ok = false;
        } else {
            binding.tilLastName.setError(null);
        }

        // Email
        String email = binding.etEmail.getText() != null
                ? binding.etEmail.getText().toString().trim() : "";
        if (TextUtils.isEmpty(email) || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.tilEmail.setError(getString(R.string.error_invalid_email));
            ok = false;
        } else {
            binding.tilEmail.setError(null);
        }

        // Phone
        String phone = binding.etPhone.getText() != null
                ? binding.etPhone.getText().toString().trim() : "";
        if (TextUtils.isEmpty(phone) || phone.replaceAll("\\s", "").length() < 9) {
            binding.tilPhone.setError(getString(R.string.error_invalid_phone));
            ok = false;
        } else {
            binding.tilPhone.setError(null);
        }

        // Amount
        String amountStr = binding.etAmount.getText() != null
                ? binding.etAmount.getText().toString().trim() : "";
        double amount = 0;
        try { amount = Double.parseDouble(amountStr); } catch (NumberFormatException ignored) {}

        if (amount <= 0) {
            binding.etAmount.setError(getString(R.string.error_enter_amount));
            ok = false;
        } else if (amount < 1) {
            binding.etAmount.setError(getString(R.string.error_amount_min));
            ok = false;
        } else {
            binding.etAmount.setError(null);
        }

        return ok;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}