package com.example.farpay;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import com.example.farpay.R;
import com.example.farpay.databinding.ActivityPayBinding;

public class PayActivity extends AppCompatActivity {

    private ActivityPayBinding binding;
    private NavController navController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPayBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Wire NavController
        NavHostFragment navHost = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.pay_nav_host);
        if (navHost == null) return;
        navController = navHost.getNavController();

        // Update step indicator as user navigates
        navController.addOnDestinationChangedListener((controller, destination, arguments) -> {
            if (destination.getId() == R.id.payStep1Fragment) {
                showStep(1);
            } else if (destination.getId() == R.id.payStep2Fragment) {
                showStep(2);
            }
        });

        // Back button — pop back stack or finish
        binding.btnBack.setOnClickListener(v -> {
            if (!navController.popBackStack()) {
                finish();
            }
        });
    }

    /**
     * Updates the header step text and the stepper strip visuals.
     * Called from the NavController destination change listener.
     */
    public void showStep(int step) {
        // Update header label
        binding.tvStepIndicator.setText(getString(R.string.pay_step_indicator, step));

        // Find stepper views (included in activity_pay.xml)
        View root = binding.getRoot();
        View step1Bg    = root.findViewById(R.id.step1_bg);
        TextView step1Num   = root.findViewById(R.id.step1_num);
        ImageView step1Check = root.findViewById(R.id.step1_check);
        View step2Bg    = root.findViewById(R.id.step2_bg);
        TextView step2Label = root.findViewById(R.id.step2_label);
        View connector  = root.findViewById(R.id.step_connector);

        if (step1Bg == null) return; // stepper not yet inflated

        if (step == 2) {
            // Step 1 — done (navy + checkmark)
            step1Bg.setBackgroundResource(R.drawable.bg_step_done);
            if (step1Num != null)   step1Num.setVisibility(View.GONE);
            if (step1Check != null) step1Check.setVisibility(View.VISIBLE);
            // Step 2 — active (teal)
            if (step2Bg != null)    step2Bg.setBackgroundResource(R.drawable.bg_step_active);
            if (step2Label != null) step2Label.setTextColor(getColor(R.color.text_primary));
            // Connector — filled teal
            if (connector != null)  connector.setBackgroundColor(getColor(R.color.brand_accent));
        } else {
            // Step 1 — active (teal, number visible)
            step1Bg.setBackgroundResource(R.drawable.bg_step_active);
            if (step1Num != null)   step1Num.setVisibility(View.VISIBLE);
            if (step1Check != null) step1Check.setVisibility(View.GONE);
            // Step 2 — inactive (grey)
            if (step2Bg != null)    step2Bg.setBackgroundResource(R.drawable.bg_step_inactive);
            if (step2Label != null) step2Label.setTextColor(getColor(R.color.text_hint));
            // Connector — unfilled grey
            if (connector != null)  connector.setBackgroundColor(getColor(R.color.grey_200));
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        return navController.navigateUp() || super.onSupportNavigateUp();
    }
}