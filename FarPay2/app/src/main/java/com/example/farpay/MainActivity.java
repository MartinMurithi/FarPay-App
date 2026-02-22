package com.example.farpay;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.splashscreen.SplashScreen; // Ensure this import is correct
import java.util.concurrent.atomic.AtomicBoolean;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // MUST be called before super.onCreate()
        SplashScreen splashScreen = SplashScreen.installSplashScreen(this);

        super.onCreate(savedInstanceState);

        final AtomicBoolean keepShowingSplash = new AtomicBoolean(true);
        splashScreen.setKeepOnScreenCondition(keepShowingSplash::get);

        // This keeps the splash visible for 2 seconds
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            keepShowingSplash.set(false);
        }, 2000);

        // Inflation happens here. If the theme is wrong, it crashes here.
        setContentView(R.layout.fragment_home);
    }
}