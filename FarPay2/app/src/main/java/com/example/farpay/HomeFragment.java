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

        // 1. Initialize RecyclerView
        adapter = new TransactionAdapter(transactionList);
        binding.rvHistory.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.rvHistory.setAdapter(adapter);

        // 2. Initial Fetch
        fetchHistory();

        // 3. Setup Swipe to Refresh (if you added it to XML)
        binding.swipeRefresh.setOnRefreshListener(this::fetchHistory);
    }

    private void fetchHistory() {
        binding.swipeRefresh.setRefreshing(true);

        PesaPalApi api = RetrofitClient.getClient().create(PesaPalApi.class);
        api.getTransactionHistory().enqueue(new Callback<List<Transaction>>() {
            @Override
            public void onResponse(Call<List<Transaction>> call, Response<List<Transaction>> response) { binding.swipeRefresh.setRefreshing(false);

                if (response.isSuccessful() && response.body() != null) {
                    transactionList.clear();
                    transactionList.addAll(response.body());
                    adapter.notifyDataSetChanged(); // Refresh the list on screen
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