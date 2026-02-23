package com.example.farpay.adapters;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import com.example.farpay.R;
import com.example.farpay.databinding.ItemTransactionBinding;
import com.example.farpay.models.Transaction;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class TransactionAdapter extends RecyclerView.Adapter<TransactionAdapter.ViewHolder> {
    private List<Transaction> transactions;

    public TransactionAdapter(List<Transaction> transactions) {
        this.transactions = transactions;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemTransactionBinding binding = ItemTransactionBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Transaction item = transactions.get(position);

        // 1. Format Amount
        NumberFormat fmt = NumberFormat.getNumberInstance(Locale.US);
        fmt.setMinimumFractionDigits(2);
        holder.binding.tvAmount.setText("KES " + fmt.format(item.getAmount()));

        // 2. Set Name/Reference
        // Note: You can change this to show the phone number or name from your DB
        holder.binding.tvTransactionName.setText(item.getPhone());

        // 3. Date & Method string
        holder.binding.tvTransactionDate.setText(item.getCreatedAt() + " • " + item.getTransactionRef());

        // 4. Status Chip Logic
        String status = item.getStatus();
        holder.binding.tvStatus.setText(status);

        if ("COMPLETED".equalsIgnoreCase(status)) {
            holder.binding.tvStatus.setChipBackgroundColorResource(R.color.success_bg);
            holder.binding.tvStatus.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.success));
        } else {
            holder.binding.tvStatus.setChipBackgroundColorResource(R.color.warning_bg); // Ensure this exists in colors.xml
            holder.binding.tvStatus.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.warning));
        }
    }

    @Override
    public int getItemCount() { return transactions != null ? transactions.size() : 0; }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ItemTransactionBinding binding;
        ViewHolder(ItemTransactionBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}