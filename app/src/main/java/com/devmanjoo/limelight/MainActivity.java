package com.devmanjoo.limelight;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import android.os.Bundle;
import android.view.Window;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsControllerCompat;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;
import com.devmanjoo.limelight.databinding.ActivityMainBinding;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.devmanjoo.limelight.R;

import com.devmanjoo.limelight.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {
    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityMainBinding b = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(b.getRoot());

        NavHostFragment navHost = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.nav_host_fragment);
        NavController navController = navHost.getNavController();
        NavigationUI.setupWithNavController(b.bottomNav, navController);

        Window w = getWindow();
        w.setStatusBarColor(ContextCompat.getColor(this, R.color.backgroundDark));
        WindowInsetsControllerCompat ic = ViewCompat.getWindowInsetsController(w.getDecorView());
        if (ic != null) ic.setAppearanceLightStatusBars(false);
    }
}