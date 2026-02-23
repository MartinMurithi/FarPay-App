package com.example.farpay;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.farpay.adapters.TransactionAdapter;
import com.example.farpay.api.PesaPalApi;
import com.example.farpay.api.RetrofitClient;
import com.example.farpay.databinding.FragmentHomeBinding;
import com.example.farpay.models.Transaction;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    private TransactionAdapter adapter;
    private List<Transaction> transactionList = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // --- 1. Initialize RecyclerView (Existing) ---
        adapter = new TransactionAdapter(transactionList);
        binding.rvHistory.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.rvHistory.setAdapter(adapter);

        // --- 2. Setup Swipe to Refresh (Existing) ---
        binding.swipeRefresh.setOnRefreshListener(this::fetchHistory);

        // --- 3. Initial Fetch (Existing) ---
        fetchHistory();

        // --- 4. NEW: Payment Method Click Listeners ---
        setupPaymentMethods();

        // --- 5. NEW: Quick Action Buttons (Send/Receive) ---
        setupQuickActions();
    }

    private void setupPaymentMethods() {
        // PesaPal - The Main Working Feature
        binding.paymentMethods.methodPesapal.setOnClickListener(v -> openPayActivity("PesaPal"));

        // Visa/Mastercard
        binding.paymentMethods.methodCard.setOnClickListener(v -> openPayActivity("Card"));

        // Bank Transfer (Coming Soon)
        binding.paymentMethods.methodBank.setOnClickListener(v -> showComingSoonSheet("Bank Transfer"));

        // Visual Polish: Highlight PesaPal by default
        binding.paymentMethods.methodPesapal.setStrokeColor(getResources().getColor(R.color.brand_accent));
        binding.paymentMethods.methodPesapal.setStrokeWidth(4);
    }

    private void setupQuickActions() {
        // 1. Send Action
        binding.quickActions.actionSend.setOnClickListener(v -> showComingSoonSheet("Send Money"));

        // 2. Receive Action
        binding.quickActions.actionReceive.setOnClickListener(v -> showComingSoonSheet("Receive Money"));

        // 3. Pay Bills Action
        binding.quickActions.actionPay.setOnClickListener(v -> showComingSoonSheet("Pay Bills"));
    }

    private void openPayActivity(String method) {
        Intent intent = new Intent(getActivity(), PayActivity.class);
        intent.putExtra("PAYMENT_METHOD", method);
        startActivity(intent);
    }

    private void showComingSoonSheet(String featureName) {
        com.google.android.material.bottomsheet.BottomSheetDialog bottomSheetDialog =
                new com.google.android.material.bottomsheet.BottomSheetDialog(requireContext());

        // Use the layout we created earlier
        View view = getLayoutInflater().inflate(R.layout.layout_coming_soon, null);
        TextView title = view.findViewById(R.id.tvTitle);
        if (title != null) title.setText(featureName + " Coming Soon!");

        view.findViewById(R.id.btnGotIt).setOnClickListener(v -> bottomSheetDialog.dismiss());
        bottomSheetDialog.setContentView(view);
        bottomSheetDialog.show();
    }

    private void fetchHistory() {
        binding.swipeRefresh.setRefreshing(true);
        PesaPalApi api = RetrofitClient.getClient().create(PesaPalApi.class);
        api.getTransactionHistory().enqueue(new Callback<List<Transaction>>() {
            @Override
            public void onResponse(Call<List<Transaction>> call, Response<List<Transaction>> response) {
                binding.swipeRefresh.setRefreshing(false);
                if (response.isSuccessful() && response.body() != null) {
                    transactionList.clear();
                    transactionList.addAll(response.body());
                    adapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(getContext(), "Failed to load history", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Transaction>> call, Throwable t) {
                binding.swipeRefresh.setRefreshing(false);
                Toast.makeText(getContext(), "Check your internet/ngrok", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
