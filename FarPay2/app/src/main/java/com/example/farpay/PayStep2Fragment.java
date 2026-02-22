package com.example.farpay;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.farpay.R;
import com.example.farpay.databinding.FragmentPayStep2Binding;
import com.example.farpay.databinding.ItemPayMethodBinding;

import java.text.NumberFormat;
import java.util.Locale;

public class PayStep2Fragment extends Fragment {

    private FragmentPayStep2Binding binding;

    // Per-card bindings — resolved from included layouts
    private ItemPayMethodBinding pesapalBinding;
    private ItemPayMethodBinding visaBinding;
    private ItemPayMethodBinding bankBinding;

    // Currently selected method id ("pesapal" | "card" | "bank" | null)
    @Nullable
    private String selectedMethod = null;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentPayStep2Binding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        populateSummary();
        setupMethodCards();
        setupFooter();
    }

    // ─── Summary ─────────────────────────────────────────────────────────────

    private void populateSummary() {
        Bundle args = getArguments();
        if (args == null) return;

        String name   = args.getString("payerName", "—");
        String rawStr = args.getString("amount", "0");
        double raw    = 0;
        try { raw = Double.parseDouble(rawStr); } catch (NumberFormatException ignored) {}

        NumberFormat fmt = NumberFormat.getNumberInstance(Locale.US);
        fmt.setMinimumFractionDigits(2);
        String formatted = "KES " + fmt.format(raw);

        binding.tvSummaryName.setText(name);
        binding.tvSummaryAmount.setText(formatted);
        binding.tvSummaryTotal.setText(formatted);  // fee is Free → total == amount
    }

    // ─── Method cards ────────────────────────────────────────────────────────

    private void setupMethodCards() {
        // Bind included layouts to their respective ViewBinding instances
        pesapalBinding = ItemPayMethodBinding.bind(binding.cardPesapal.getRoot());
        visaBinding    = ItemPayMethodBinding.bind(binding.cardVisa.getRoot());
        bankBinding    = ItemPayMethodBinding.bind(binding.cardBank.getRoot());

        // ── PesaPal
        pesapalBinding.ivMethodIcon.setImageResource(R.drawable.ic_pesapal);
        pesapalBinding.tvMethodName.setText(R.string.method_pesapal);
        pesapalBinding.tvMethodDesc.setText(R.string.method_pesapal_desc);
        pesapalBinding.tvMethodBadge.setText(R.string.method_pesapal_badge);
        pesapalBinding.tvMethodBadge.setVisibility(View.VISIBLE);
        pesapalBinding.ivMethodBg.setBackground(
                ContextCompat.getDrawable(requireContext(), R.drawable.bg_action_circle_teal));
        pesapalBinding.methodRoot.setOnClickListener(v -> selectMethod("pesapal"));

        // ── Visa / Mastercard
        visaBinding.ivMethodIcon.setImageResource(R.drawable.ic_pay);
        visaBinding.tvMethodName.setText(R.string.method_card);
        visaBinding.tvMethodDesc.setText(R.string.method_card_desc);
        visaBinding.tvMethodBadge.setText(R.string.method_card_badge);
        visaBinding.tvMethodBadge.setVisibility(View.VISIBLE);
        visaBinding.brandLogos.setVisibility(View.VISIBLE);
        visaBinding.ivMethodBg.setBackground(
                ContextCompat.getDrawable(requireContext(), R.drawable.bg_action_circle_blue));
        visaBinding.methodRoot.setOnClickListener(v -> selectMethod("card"));

        // ── Bank Transfer
        bankBinding.ivMethodIcon.setImageResource(R.drawable.ic_bank);
        bankBinding.tvMethodName.setText(R.string.method_bank);
        bankBinding.tvMethodDesc.setText(R.string.method_bank_desc);
        bankBinding.ivMethodBg.setBackground(
                ContextCompat.getDrawable(requireContext(), R.drawable.bg_action_circle));
        bankBinding.methodRoot.setOnClickListener(v -> selectMethod("bank"));
    }

    private void selectMethod(@NonNull String method) {
        selectedMethod = method;

        Drawable selectedBg    = ContextCompat.getDrawable(requireContext(), R.drawable.bg_method_selected);
        Drawable defaultBg     = ContextCompat.getDrawable(requireContext(), R.drawable.bg_method_default);
        Drawable radioActive   = ContextCompat.getDrawable(requireContext(), R.drawable.bg_step_active);
        Drawable radioInactive = ContextCompat.getDrawable(requireContext(), R.drawable.bg_step_inactive);

        // Update each card's background and radio dot
        applyCardState(pesapalBinding, "pesapal".equals(method), selectedBg, defaultBg, radioActive, radioInactive);
        applyCardState(visaBinding,    "card".equals(method),    selectedBg, defaultBg, radioActive, radioInactive);
        applyCardState(bankBinding,    "bank".equals(method),    selectedBg, defaultBg, radioActive, radioInactive);

        // Show selected method strip in footer
        binding.rowSelectedMethod.setVisibility(View.VISIBLE);

        switch (method) {
            case "pesapal":
                binding.tvSelectedName.setText(R.string.method_pesapal);
                binding.ivSelectedIcon.setImageResource(R.drawable.ic_pesapal);
                break;
            case "card":
                binding.tvSelectedName.setText(R.string.method_card);
                binding.ivSelectedIcon.setImageResource(R.drawable.ic_pay);
                break;
            case "bank":
                binding.tvSelectedName.setText(R.string.method_bank);
                binding.ivSelectedIcon.setImageResource(R.drawable.ic_bank);
                break;
        }

        // Enable Pay Now button
        binding.btnPayNow.setEnabled(true);
    }

    private void applyCardState(@NonNull ItemPayMethodBinding cardBinding,
                                boolean isSelected,
                                @Nullable Drawable selectedBg,
                                @Nullable Drawable defaultBg,
                                @Nullable Drawable radioActive,
                                @Nullable Drawable radioInactive) {
        cardBinding.methodRoot.setBackground(isSelected ? selectedBg : defaultBg);
        cardBinding.radioDot.setBackground(isSelected ? radioActive : radioInactive);
        cardBinding.radioDot.setContentDescription(
                getString(isSelected ? R.string.cd_selected : R.string.cd_not_selected));
    }

    // ─── Footer ──────────────────────────────────────────────────────────────

    private void setupFooter() {
        // Pay Now — disabled until a method is selected
        binding.btnPayNow.setOnClickListener(v -> {
            // TODO: Integrate payment gateway
            // Pass selectedMethod, amount, payerName to your SDK
            // e.g. new PesaPalManager().startPayment(amount, email, phone, callbackUrl);
        });

        // Change — deselect current method and reset UI
        binding.tvChange.setOnClickListener(v -> clearSelection());
    }

    private void clearSelection() {
        selectedMethod = null;
        binding.btnPayNow.setEnabled(false);
        binding.rowSelectedMethod.setVisibility(View.GONE);

        Drawable defaultBg     = ContextCompat.getDrawable(requireContext(), R.drawable.bg_method_default);
        Drawable radioInactive = ContextCompat.getDrawable(requireContext(), R.drawable.bg_step_inactive);

        for (ItemPayMethodBinding b : new ItemPayMethodBinding[]{ pesapalBinding, visaBinding, bankBinding }) {
            b.methodRoot.setBackground(defaultBg);
            b.radioDot.setBackground(radioInactive);
            b.radioDot.setContentDescription(getString(R.string.cd_not_selected));
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
