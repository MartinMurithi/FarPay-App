package com.example.farpay;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.farpay.adapters.TransactionAdapter;
import com.example.farpay.api.PesaPalApi;
import com.example.farpay.api.RetrofitClient;
import com.example.farpay.databinding.FragmentHistoryBinding;
import com.example.farpay.models.Transaction;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HistoryFragment extends Fragment {

    private FragmentHistoryBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentHistoryBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.rvHistory.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.swipeRefresh.setOnRefreshListener(this::fetchTransactions);

        fetchTransactions();
    }

    @Override
    public void onResume() {
        super.onResume();
        fetchTransactions(); // Refresh automatically when user returns to this tab
    }

    private void fetchTransactions() {
        binding.swipeRefresh.setRefreshing(true);
        PesaPalApi api = RetrofitClient.getClient().create(PesaPalApi.class);

        api.getTransactionHistory().enqueue(new Callback<List<Transaction>>() {
            @Override
            public void onResponse(Call<List<Transaction>> call, Response<List<Transaction>> response) {
                binding.swipeRefresh.setRefreshing(false);
                if (response.isSuccessful() && response.body() != null) {
                    List<Transaction> history = response.body();

                    if (history.isEmpty()) {
                        binding.rvHistory.setVisibility(View.GONE);
                        binding.tvEmptyState.setVisibility(View.VISIBLE);
                    } else {
                        binding.rvHistory.setVisibility(View.VISIBLE);
                        binding.tvEmptyState.setVisibility(View.GONE);
                        binding.rvHistory.setAdapter(new TransactionAdapter(history));
                    }
                } else {
                    Toast.makeText(getContext(), "Server Error: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Transaction>> call, Throwable t) {
                binding.swipeRefresh.setRefreshing(false);
                Toast.makeText(getContext(), "Network Failure", Toast.LENGTH_SHORT).show();
                Toast.makeText(getContext(), "Connection Error: Is the server/ngrok running?", Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}