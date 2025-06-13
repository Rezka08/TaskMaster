package com.example.taskmaster.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.taskmaster.R;
import com.example.taskmaster.fragment.HomeFragment;
import com.example.taskmaster.fragment.AddTaskFragment;
import com.example.taskmaster.fragment.CalendarFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity implements AddTaskFragment.MainActivity {
    private BottomNavigationView bottomNavigationView;
    private Handler refreshHandler;

    // Fragment references for refresh management
    private HomeFragment homeFragment;
    private CalendarFragment calendarFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initViews();
        setupBottomNavigation();
        initializeFragments();

        // Load home fragment by default
        if (savedInstanceState == null) {
            loadHomeFragment();
            bottomNavigationView.setSelectedItemId(R.id.nav_home);
        }

        refreshHandler = new Handler(Looper.getMainLooper());
    }

    private void initViews() {
        bottomNavigationView = findViewById(R.id.bottom_navigation);
    }

    private void initializeFragments() {
        // Pre-create fragments to maintain state and allow refresh
        homeFragment = new HomeFragment();
        calendarFragment = new CalendarFragment();
    }

    private HomeFragment getHomeFragment() {
        // Always create new instance to avoid fragment conflicts
        homeFragment = new HomeFragment();
        return homeFragment;
    }

    private CalendarFragment getCalendarFragment() {
        if (calendarFragment == null || !calendarFragment.isAdded()) {
            calendarFragment = new CalendarFragment();
        }
        return calendarFragment;
    }

    private void setupBottomNavigation() {
        bottomNavigationView.setOnItemSelectedListener(item -> {
            if (item.getItemId() == R.id.nav_home) {
                loadHomeFragment();
                return true;
            } else if (item.getItemId() == R.id.nav_add_task) {
                loadAddTaskFragment();
                return true;
            } else if (item.getItemId() == R.id.nav_calendar) {
                loadCalendarFragment();
                return true;
            }
            return false;
        });
    }

    private void loadHomeFragment() {
        try {
            FragmentManager fragmentManager = getSupportFragmentManager();
            Fragment currentFragment = fragmentManager.findFragmentById(R.id.fragment_container);

            // Only load if not already showing home fragment
            if (!(currentFragment instanceof HomeFragment)) {
                FragmentTransaction transaction = fragmentManager.beginTransaction();
                transaction.replace(R.id.fragment_container, getHomeFragment(), "HOME");
                transaction.commitAllowingStateLoss();
            }
        } catch (Exception e) {
            // Handle any fragment transaction exceptions gracefully
            e.printStackTrace();
        }
    }

    private void loadAddTaskFragment() {
        try {
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction transaction = fragmentManager.beginTransaction();

            // Always create new AddTaskFragment for clean form
            AddTaskFragment addTaskFragment = new AddTaskFragment();
            transaction.replace(R.id.fragment_container, addTaskFragment, "ADD_TASK");
            transaction.addToBackStack("ADD_TASK");
            transaction.commitAllowingStateLoss();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadCalendarFragment() {
        try {
            FragmentManager fragmentManager = getSupportFragmentManager();
            Fragment currentFragment = fragmentManager.findFragmentById(R.id.fragment_container);

            // Only load if not already showing calendar fragment
            if (!(currentFragment instanceof CalendarFragment)) {
                FragmentTransaction transaction = fragmentManager.beginTransaction();
                transaction.replace(R.id.fragment_container, getCalendarFragment(), "CALENDAR");
                transaction.commitAllowingStateLoss();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void navigateToHome() {
        // Navigate to home fragment and select home in bottom navigation
        bottomNavigationView.setSelectedItemId(R.id.nav_home);

        // Clear back stack safely
        FragmentManager fragmentManager = getSupportFragmentManager();
        try {
            // Clear all back stack entries one by one to avoid conflicts
            while (fragmentManager.getBackStackEntryCount() > 0) {
                fragmentManager.popBackStackImmediate();
            }
        } catch (Exception e) {
            // If there's an issue with back stack, clear it forcefully
            for (int i = 0; i < fragmentManager.getBackStackEntryCount(); i++) {
                fragmentManager.popBackStack();
            }
        }

        // Use handler to ensure back stack is cleared before loading home
        if (refreshHandler != null) {
            refreshHandler.post(() -> {
                loadHomeFragmentSafely();
                scheduleDataRefresh();
            });
        }
    }

    private void loadHomeFragmentSafely() {
        try {
            FragmentManager fragmentManager = getSupportFragmentManager();
            Fragment currentFragment = fragmentManager.findFragmentById(R.id.fragment_container);

            // Force replace with new home fragment to avoid conflicts
            FragmentTransaction transaction = fragmentManager.beginTransaction();

            // Remove current fragment if exists
            if (currentFragment != null) {
                transaction.remove(currentFragment);
            }

            // Add fresh home fragment
            homeFragment = new HomeFragment(); // Create new instance
            transaction.replace(R.id.fragment_container, homeFragment, "HOME");
            transaction.commitAllowingStateLoss();

        } catch (Exception e) {
            // Fallback: just use bottom navigation to switch
            bottomNavigationView.setSelectedItemId(R.id.nav_home);
        }
    }

    // Method to navigate to add task
    public void navigateToAddTask() {
        bottomNavigationView.setSelectedItemId(R.id.nav_add_task);
        loadAddTaskFragment();
    }

    /**
     * Schedule data refresh for active fragments
     */
    private void scheduleDataRefresh() {
        if (refreshHandler != null) {
            refreshHandler.postDelayed(() -> {
                refreshActiveFragments();
            }, 500); // Increased delay to ensure fragment transition is complete
        }
    }

    /**
     * Refresh data in currently active fragments
     */
    private void refreshActiveFragments() {
        Fragment activeFragment = getSupportFragmentManager().findFragmentById(R.id.fragment_container);

        if (activeFragment instanceof HomeFragment) {
            ((HomeFragment) activeFragment).refreshData();
        } else if (activeFragment instanceof CalendarFragment) {
            ((CalendarFragment) activeFragment).refreshData();
        }
    }

    /**
     * Call this method when data changes (task created, updated, deleted)
     */
    public void onDataChanged() {
        scheduleDataRefresh();
    }

    @Override
    public void onBackPressed() {
        FragmentManager fragmentManager = getSupportFragmentManager();

        // If there are fragments in back stack, pop them safely
        if (fragmentManager.getBackStackEntryCount() > 0) {
            try {
                fragmentManager.popBackStack();

                // Update bottom navigation based on current fragment
                if (refreshHandler != null) {
                    refreshHandler.postDelayed(() -> {
                        updateBottomNavigationSelection();
                    }, 100);
                }
            } catch (Exception e) {
                // Fallback to home if back navigation fails
                navigateToHomeSafely();
            }
        } else {
            // If we're at home fragment, exit app
            super.onBackPressed();
        }
    }

    private void updateBottomNavigationSelection() {
        try {
            FragmentManager fragmentManager = getSupportFragmentManager();
            Fragment currentFragment = fragmentManager.findFragmentById(R.id.fragment_container);

            if (currentFragment instanceof HomeFragment) {
                bottomNavigationView.setSelectedItemId(R.id.nav_home);
            } else if (currentFragment instanceof AddTaskFragment) {
                bottomNavigationView.setSelectedItemId(R.id.nav_add_task);
            } else if (currentFragment instanceof CalendarFragment) {
                bottomNavigationView.setSelectedItemId(R.id.nav_calendar);
            }
        } catch (Exception e) {
            // Default to home if detection fails
            bottomNavigationView.setSelectedItemId(R.id.nav_home);
        }
    }

    private void navigateToHomeSafely() {
        try {
            bottomNavigationView.setSelectedItemId(R.id.nav_home);
            loadHomeFragment();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        // Handle intent data if needed
        if (intent.hasExtra("navigate_to")) {
            String navigateTo = intent.getStringExtra("navigate_to");
            if ("home".equals(navigateTo)) {
                navigateToHome();
            }
        }

        // Refresh data when returning from other activities
        if (intent.hasExtra("data_changed") && intent.getBooleanExtra("data_changed", false)) {
            onDataChanged();
        }
    }

    // Method to navigate to specific task detail
    public void navigateToTaskDetail(int taskId) {
        Intent intent = new Intent(this, com.example.taskmaster.activity.TaskDetailActivity.class);
        intent.putExtra("task_id", taskId);
        startActivityForResult(intent, 1001); // Use startActivityForResult to detect changes
    }

    // Method to edit task
    public void editTask(int taskId) {
        // Switch to add task fragment in edit mode
        AddTaskFragment addTaskFragment = new AddTaskFragment();

        // You might want to pass the task ID and load it in the fragment
        Bundle args = new Bundle();
        args.putInt("task_id", taskId);
        addTaskFragment.setArguments(args);

        bottomNavigationView.setSelectedItemId(R.id.nav_add_task);

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.fragment_container, addTaskFragment, "ADD_TASK");
        transaction.addToBackStack("ADD_TASK");
        transaction.commit();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Refresh data when returning from activities that might have changed data
        if (requestCode == 1001 && resultCode == RESULT_OK) {
            onDataChanged();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Refresh data when activity becomes visible again
        scheduleDataRefresh();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (refreshHandler != null) {
            refreshHandler.removeCallbacksAndMessages(null);
        }
    }
}