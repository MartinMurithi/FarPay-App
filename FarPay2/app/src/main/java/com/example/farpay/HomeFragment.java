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
import com.google.android.material.bottomnavigation.BottomNavigationView;

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

        // 1. RecyclerView inside the included "Recent Transactions" layout
        adapter = new TransactionAdapter(transactionList);
        binding.recentTransactions.rvRecentTransactions.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.recentTransactions.rvRecentTransactions.setAdapter(adapter);

        // 2. See All navigation
        binding.recentTransactions.tvSeeAll.setOnClickListener(v -> {
            BottomNavigationView nav = getActivity().findViewById(R.id.bottom_nav);
            if (nav != null) nav.setSelectedItemId(R.id.nav_history);
        });

        // 3. Setup Refresh
        binding.swipeRefresh.setOnRefreshListener(this::fetchHistory);

        // 4. Initial Load
        fetchHistory();
        setupPaymentMethods();
        setupQuickActions();
    }
    private void setupPaymentMethods() {
        binding.paymentMethods.methodPesapal.setOnClickListener(v -> openPayActivity("PesaPal"));
        binding.paymentMethods.methodCard.setOnClickListener(v -> openPayActivity("Card"));
        binding.paymentMethods.methodBank.setOnClickListener(v -> showComingSoonSheet("Bank Transfer"));

        // Highlight PesaPal
        binding.paymentMethods.methodPesapal.setStrokeColor(getResources().getColor(R.color.brand_accent));
        binding.paymentMethods.methodPesapal.setStrokeWidth(4);
    }

    private void setupQuickActions() {
        binding.quickActions.actionSend.setOnClickListener(v -> showComingSoonSheet("Send Money"));
        binding.quickActions.actionReceive.setOnClickListener(v -> showComingSoonSheet("Receive Money"));
        binding.quickActions.actionPay.setOnClickListener(v -> openPayActivity("Bill Pay"));
    }

    private void openPayActivity(String method) {
        Intent intent = new Intent(getActivity(), PayActivity.class);
        intent.putExtra("PAYMENT_METHOD", method);
        startActivity(intent);
    }

    private void showComingSoonSheet(String featureName) {
        com.google.android.material.bottomsheet.BottomSheetDialog bottomSheetDialog =
                new com.google.android.material.bottomsheet.BottomSheetDialog(requireContext());

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
                    List<Transaction> fullList = response.body();
                    transactionList.clear();

                    // --- 5 ITEM LIMIT FOR HOME SCREEN ---
                    if (fullList.size() > 5) {
                        transactionList.addAll(fullList.subList(0, 5));
                    } else {
                        transactionList.addAll(fullList);
                    }

                    adapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(getContext(), "Failed to load history", Toast.LENGTH_SHORT).show();
                }            }

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