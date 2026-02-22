package com.example.farpay;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.splashscreen.SplashScreen;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;

import com.example.farpay.databinding.ActivityMainBinding;

import java.util.concurrent.atomic.AtomicBoolean;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // 1. Splash Screen
        SplashScreen splashScreen = SplashScreen.installSplashScreen(this);
        super.onCreate(savedInstanceState);

        final AtomicBoolean keepShowingSplash = new AtomicBoolean(true);
        splashScreen.setKeepOnScreenCondition(keepShowingSplash::get);
        new Handler(Looper.getMainLooper()).postDelayed(() -> keepShowingSplash.set(false), 2000);

        // 2. View Binding
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // 3. Setup Navigation Controller
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.nav_host_fragment);

        if (navHostFragment != null) {
            NavController navController = navHostFragment.getNavController();

            // Setup the BottomNav with the NavController for automatic fragment swapping
            NavigationUI.setupWithNavController(binding.bottomNav, navController);

            // 4. Custom Listener for the "Pay" exception
            binding.bottomNav.setOnItemSelectedListener(item -> {
                int itemId = item.getItemId();

                if (itemId == R.id.nav_pay) {
                    // Start PayActivity instead of swapping a fragment
                    Intent intent = new Intent(MainActivity.this, PayActivity.class);
                    startActivity(intent);
                    return false; // False = Don't highlight the 'Pay' icon
                }

                // For all other tabs (Home, History, Account), do the default fragment swap
                return NavigationUI.onNavDestinationSelected(item, navController);
            });
        }
    }
}