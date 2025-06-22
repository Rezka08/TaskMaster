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
import com.example.taskmaster.utils.ThemeManager;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity implements AddTaskFragment.MainActivity {
    private BottomNavigationView bottomNavigationView;
    private Handler mainHandler;
    private String currentFragmentTag = "HOME";
    private ThemeManager themeManager;

    // Fragment cache - but recreate when needed to avoid leaks
    private HomeFragment homeFragment;
    private CalendarFragment calendarFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Apply theme before super.onCreate() and setContentView()
        themeManager = new ThemeManager(this);
        themeManager.applyTheme();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        initViews();
        setupBottomNavigation();

        // Load home fragment by default
        if (savedInstanceState == null) {
            showHomeFragment();
            bottomNavigationView.setSelectedItemId(R.id.nav_home);
        }

        mainHandler = new Handler(Looper.getMainLooper());
    }

    private void initViews() {
        bottomNavigationView = findViewById(R.id.bottom_navigation);
    }

    private void setupBottomNavigation() {
        bottomNavigationView.setOnItemSelectedListener(item -> {
            try {
                if (item.getItemId() == R.id.nav_home) {
                    if (!currentFragmentTag.equals("HOME")) {
                        showHomeFragment();
                    }
                    return true;
                } else if (item.getItemId() == R.id.nav_add_task) {
                    showAddTaskFragment();
                    return true;
                } else if (item.getItemId() == R.id.nav_calendar) {
                    if (!currentFragmentTag.equals("CALENDAR")) {
                        showCalendarFragment();
                    }
                    return true;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return false;
        });
    }

    private void showHomeFragment() {
        try {
            // Always create new instance to avoid fragment already added issues
            homeFragment = new HomeFragment();
            replaceFragment(homeFragment, "HOME", false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showAddTaskFragment() {
        try {
            // Always create new instance for clean form
            AddTaskFragment addTaskFragment = new AddTaskFragment();
            replaceFragment(addTaskFragment, "ADD_TASK", true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showCalendarFragment() {
        try {
            // Reuse calendar fragment if exists and not detached
            if (calendarFragment == null || calendarFragment.isDetached()) {
                calendarFragment = new CalendarFragment();
            }
            replaceFragment(calendarFragment, "CALENDAR", false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void replaceFragment(Fragment fragment, String tag, boolean addToBackStack) {
        try {
            if (isFinishing() || isDestroyed()) {
                return;
            }

            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction transaction = fragmentManager.beginTransaction();

            // Replace the fragment
            transaction.replace(R.id.fragment_container, fragment, tag);

            if (addToBackStack) {
                transaction.addToBackStack(tag);
            }

            transaction.commitAllowingStateLoss();
            currentFragmentTag = tag;

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void navigateToHome() {
        try {
            // Simple navigation - just change bottom nav selection
            // This will trigger showHomeFragment() through the listener
            if (mainHandler != null) {
                mainHandler.post(() -> {
                    try {
                        // Clear back stack safely
                        clearBackStack();

                        // Select home tab
                        bottomNavigationView.setSelectedItemId(R.id.nav_home);

                        // Notify about data change
                        scheduleDataRefresh();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void clearBackStack() {
        try {
            FragmentManager fragmentManager = getSupportFragmentManager();
            while (fragmentManager.getBackStackEntryCount() > 0) {
                fragmentManager.popBackStackImmediate();
            }
        } catch (Exception e) {
            // If clearing fails, just continue
            e.printStackTrace();
        }
    }

    public void navigateToAddTask() {
        bottomNavigationView.setSelectedItemId(R.id.nav_add_task);
    }

    private void scheduleDataRefresh() {
        if (mainHandler != null) {
            mainHandler.postDelayed(() -> {
                refreshActiveFragment();
            }, 300);
        }
    }

    private void refreshActiveFragment() {
        try {
            Fragment activeFragment = getSupportFragmentManager().findFragmentById(R.id.fragment_container);

            if (activeFragment instanceof HomeFragment && activeFragment.isAdded()) {
                ((HomeFragment) activeFragment).refreshData();
            } else if (activeFragment instanceof CalendarFragment && activeFragment.isAdded()) {
                ((CalendarFragment) activeFragment).refreshData();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void onDataChanged() {
        scheduleDataRefresh();
    }

    @Override
    public void onBackPressed() {
        try {
            FragmentManager fragmentManager = getSupportFragmentManager();

            if (fragmentManager.getBackStackEntryCount() > 0) {
                fragmentManager.popBackStack();

                // Update bottom navigation
                mainHandler.postDelayed(() -> {
                    updateBottomNavigationSelection();
                }, 100);
            } else {
                super.onBackPressed();
            }
        } catch (Exception e) {
            e.printStackTrace();
            super.onBackPressed();
        }
    }

    private void updateBottomNavigationSelection() {
        try {
            Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.fragment_container);

            if (currentFragment instanceof HomeFragment) {
                bottomNavigationView.setSelectedItemId(R.id.nav_home);
                currentFragmentTag = "HOME";
            } else if (currentFragment instanceof AddTaskFragment) {
                bottomNavigationView.setSelectedItemId(R.id.nav_add_task);
                currentFragmentTag = "ADD_TASK";
            } else if (currentFragment instanceof CalendarFragment) {
                bottomNavigationView.setSelectedItemId(R.id.nav_calendar);
                currentFragmentTag = "CALENDAR";
            }
        } catch (Exception e) {
            e.printStackTrace();
            // Default to home if detection fails
            bottomNavigationView.setSelectedItemId(R.id.nav_home);
            currentFragmentTag = "HOME";
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        try {
            if (intent.hasExtra("navigate_to")) {
                String navigateTo = intent.getStringExtra("navigate_to");
                if ("home".equals(navigateTo)) {
                    navigateToHome();
                }
            }

            if (intent.hasExtra("data_changed") && intent.getBooleanExtra("data_changed", false)) {
                onDataChanged();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void navigateToTaskDetail(int taskId) {
        Intent intent = new Intent(this, com.example.taskmaster.activity.TaskDetailActivity.class);
        intent.putExtra("task_id", taskId);
        startActivityForResult(intent, 1001);
    }

    public void editTask(int taskId) {
        try {
            AddTaskFragment addTaskFragment = new AddTaskFragment();
            Bundle args = new Bundle();
            args.putInt("task_id", taskId);
            addTaskFragment.setArguments(args);

            bottomNavigationView.setSelectedItemId(R.id.nav_add_task);
            replaceFragment(addTaskFragment, "ADD_TASK", true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1001 && resultCode == RESULT_OK) {
            onDataChanged();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        scheduleDataRefresh();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (mainHandler != null) {
            mainHandler.removeCallbacksAndMessages(null);
            mainHandler = null;
        }

        // Clear fragment references
        homeFragment = null;
        calendarFragment = null;
    }
}