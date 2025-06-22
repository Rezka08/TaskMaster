package com.example.taskmaster.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.taskmaster.R;
import com.example.taskmaster.fragment.HomeFragment;
import com.example.taskmaster.fragment.AddTaskFragment;
import com.example.taskmaster.fragment.CalendarFragment;
import com.example.taskmaster.fragment.EditTaskFragment; // IMPORT BARU
import com.example.taskmaster.utils.ThemeManager;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {
    private BottomNavigationView bottomNavigationView;
    private Handler mainHandler;
    private String currentFragmentTag = "HOME";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        new ThemeManager(this).applyTheme();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        initViews();
        setupBottomNavigation();
        mainHandler = new Handler(Looper.getMainLooper());

        if (savedInstanceState == null) {
            handleIntent(getIntent()); // Handle intent on first create
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        handleIntent(intent); // Handle intent for subsequent calls
    }

    private void handleIntent(Intent intent) {
        if (intent != null && intent.hasExtra("navigate_to")) {
            String navigateTo = intent.getStringExtra("navigate_to");
            if ("edit_task".equals(navigateTo)) {
                int taskId = intent.getIntExtra("task_id", -1);
                if (taskId != -1) {
                    navigateToEditTask(taskId);
                }
                // Hapus extra agar tidak dieksekusi lagi
                intent.removeExtra("navigate_to");
                intent.removeExtra("task_id");
            } else {
                showHomeFragment(); // Default ke home jika tidak ada navigasi spesifik
            }
        } else {
            if (findFragmentByTag("HOME") == null && findFragmentByTag("CALENDAR") == null && findFragmentByTag("ADD_TASK") == null) {
                showHomeFragment();
            }
        }
    }

    private Fragment findFragmentByTag(String tag) {
        return getSupportFragmentManager().findFragmentByTag(tag);
    }

    private void initViews() {
        bottomNavigationView = findViewById(R.id.bottom_navigation);
    }

    private void setupBottomNavigation() {
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_home) {
                showHomeFragment();
                return true;
            } else if (itemId == R.id.nav_add_task) {
                replaceFragment(new AddTaskFragment(), "ADD_TASK", true);
                return true;
            } else if (itemId == R.id.nav_calendar) {
                replaceFragment(new CalendarFragment(), "CALENDAR", false);
                return true;
            }
            return false;
        });
    }

    private void showHomeFragment() {
        replaceFragment(new HomeFragment(), "HOME", false);
    }

    private void replaceFragment(Fragment fragment, String tag, boolean addToBackStack) {
        if (isFinishing() || isDestroyed()) {
            return;
        }
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.fragment_container, fragment, tag);
        if (addToBackStack) {
            transaction.addToBackStack(tag);
        }
        transaction.commitAllowingStateLoss();
        currentFragmentTag = tag;
    }

    // Panggil method ini dari Add/Edit fragment setelah task disimpan/diupdate
    public void navigateToHome() {
        mainHandler.post(() -> {
            if (isFinishing()) return;
            // Bersihkan back stack agar tidak kembali ke halaman add/edit
            getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
            bottomNavigationView.setSelectedItemId(R.id.nav_home);
            refreshActiveFragment();
        });
    }

    public void onDataChanged() {
        refreshActiveFragment();
    }

    private void refreshActiveFragment() {
        Fragment activeFragment = getSupportFragmentManager().findFragmentByTag(currentFragmentTag);
        if (activeFragment instanceof HomeFragment) {
            ((HomeFragment) activeFragment).refreshData();
        } else if (activeFragment instanceof CalendarFragment) {
            ((CalendarFragment) activeFragment).refreshData();
        }
    }

    // METHOD BARU untuk navigasi ke EditTaskFragment
    public void navigateToEditTask(int taskId) {
        EditTaskFragment editFragment = new EditTaskFragment();
        Bundle args = new Bundle();
        args.putInt("task_id", taskId);
        editFragment.setArguments(args);

        // Ganti navigasi ke 'add task' di bottom nav agar visualnya konsisten
        bottomNavigationView.setSelectedItemId(R.id.nav_add_task);
        replaceFragment(editFragment, "EDIT_TASK", true);
    }

    // HAPUS method editTask(int taskId) yang lama

    @Override
    public void onBackPressed() {
        if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
            getSupportFragmentManager().popBackStack();
            // Saat kembali dari Add/Edit, pastikan bottom nav menunjuk ke Home
            mainHandler.postDelayed(() -> {
                if (getSupportFragmentManager().findFragmentById(R.id.fragment_container) instanceof HomeFragment) {
                    bottomNavigationView.setSelectedItemId(R.id.nav_home);
                }
            }, 100);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && data != null && data.getBooleanExtra("data_changed", false)) {
            onDataChanged();
        }
    }
}