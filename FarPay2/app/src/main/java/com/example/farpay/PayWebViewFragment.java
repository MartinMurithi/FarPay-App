package com.example.farpay;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.farpay.databinding.FragmentPayWebViewBinding;

public class PayWebViewFragment extends Fragment {

    private FragmentPayWebViewBinding binding;
    private String paymentUrl;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            paymentUrl = getArguments().getString("payment_url");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentPayWebViewBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupWebView();
    }

    @SuppressLint("SetJavaScriptEnabled")
    private void setupWebView() {
        binding.webView.getSettings().setJavaScriptEnabled(true);
        binding.webView.getSettings().setDomStorageEnabled(true);
        binding.webView.getSettings().setLoadsImagesAutomatically(true);

        binding.webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageStarted(WebView view, String url, android.graphics.Bitmap favicon) {
                binding.webViewLoader.setVisibility(View.VISIBLE);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                binding.webViewLoader.setVisibility(View.GONE);

                if (url.contains("ngrok-free.app") || url.contains("api/v1/payments/callback")) {
                    Toast.makeText(getContext(), "Processing payment confirmation...", Toast.LENGTH_LONG).show();
                    if (getActivity() != null) {
                        getActivity().finish();
                    }
                }
            }
        });

        if (paymentUrl != null && !paymentUrl.isEmpty()) {
            binding.webView.loadUrl(paymentUrl);
        } else {
            Toast.makeText(getContext(), "Invalid Payment Link", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}