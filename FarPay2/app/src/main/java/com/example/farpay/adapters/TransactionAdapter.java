package com.example.farpay.adapters;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import com.example.farpay.R;
import com.example.farpay.databinding.ItemTransactionBinding;
import com.example.farpay.models.Transaction;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

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

        holder.binding.tvTransactionName.setText(item.getTransactionRef());
        holder.binding.tvAmount.setText("KES " + String.format("%.2f", item.getAmount()));

        holder.binding.tvTransactionDate.setText(formatDate(item.getCreatedAt()));

        String status = item.getTransactionStatus();
        holder.binding.tvStatus.setText(status);

        if ("COMPLETED".equalsIgnoreCase(status)) {
            holder.binding.tvStatus.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.success));
            holder.binding.tvStatus.setChipBackgroundColorResource(R.color.success_bg);
            holder.binding.ivTransactionIcon.setImageResource(R.drawable.ic_check_circle);
        } else {
            holder.binding.tvStatus.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.warning));
            holder.binding.tvStatus.setChipBackgroundColorResource(R.color.warning_bg);
            holder.binding.ivTransactionIcon.setImageResource(R.drawable.ic_transaction_icon);
        }
    }

    // Helper to turn DB timestamp into "MMM dd, hh:mm a"
    private String formatDate(String rawDate) {
        try {

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());
            sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
            Date date = sdf.parse(rawDate);

            SimpleDateFormat output = new SimpleDateFormat("MMM dd, hh:mm a", Locale.getDefault());
            return output.format(date);
        } catch (Exception e) {
            return rawDate; // Fallback to raw string if parsing fails
        }
    }

    @Override
    public int getItemCount() { return transactions.size(); }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ItemTransactionBinding binding;
        ViewHolder(ItemTransactionBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}