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
    private Fragment currentFragment;
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
            loadFragment(getHomeFragment(), "HOME");
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
        if (homeFragment == null) {
            homeFragment = new HomeFragment();
        }
        return homeFragment;
    }

    private CalendarFragment getCalendarFragment() {
        if (calendarFragment == null) {
            calendarFragment = new CalendarFragment();
        }
        return calendarFragment;
    }

    private void setupBottomNavigation() {
        bottomNavigationView.setOnItemSelectedListener(item -> {
            Fragment fragment = null;
            String tag = "";

            if (item.getItemId() == R.id.nav_home) {
                fragment = getHomeFragment();
                tag = "HOME";
            } else if (item.getItemId() == R.id.nav_add_task) {
                fragment = new AddTaskFragment(); // Always create new for clean form
                tag = "ADD_TASK";
            } else if (item.getItemId() == R.id.nav_calendar) {
                fragment = getCalendarFragment();
                tag = "CALENDAR";
            }

            if (fragment != null) {
                loadFragment(fragment, tag);
                return true;
            }
            return false;
        });
    }

    private void loadFragment(Fragment fragment, String tag) {
        // Check if fragment is already loaded
        if (currentFragment != null && currentFragment.getClass().equals(fragment.getClass()) && currentFragment.isAdded()) {
            return;
        }

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();

        // Use replace instead of add to avoid fragment stack issues
        transaction.replace(R.id.fragment_container, fragment, tag);

        // Only add to back stack if it's not the home fragment
        if (!tag.equals("HOME")) {
            transaction.addToBackStack(tag);
        }

        transaction.commitAllowingStateLoss(); // Use commitAllowingStateLoss to prevent IllegalStateException
        currentFragment = fragment;
    }

    @Override
    public void navigateToHome() {
        // Navigate to home fragment and select home in bottom navigation
        bottomNavigationView.setSelectedItemId(R.id.nav_home);

        // Clear back stack
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);

        // Load home fragment and refresh data
        loadFragment(getHomeFragment(), "HOME");
        scheduleDataRefresh();
    }

    // Method to navigate to add task
    public void navigateToAddTask() {
        bottomNavigationView.setSelectedItemId(R.id.nav_add_task);
        loadFragment(new AddTaskFragment(), "ADD_TASK");
    }

    /**
     * Schedule data refresh for active fragments
     */
    private void scheduleDataRefresh() {
        if (refreshHandler != null) {
            refreshHandler.postDelayed(() -> {
                refreshActiveFragments();
            }, 300); // Small delay to ensure fragment transition is complete
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

        // If there are fragments in back stack, pop them
        if (fragmentManager.getBackStackEntryCount() > 0) {
            fragmentManager.popBackStack();

            // Update bottom navigation based on current fragment
            refreshHandler.postDelayed(() -> {
                Fragment currentFragment = fragmentManager.findFragmentById(R.id.fragment_container);
                if (currentFragment instanceof HomeFragment) {
                    bottomNavigationView.setSelectedItemId(R.id.nav_home);
                } else if (currentFragment instanceof AddTaskFragment) {
                    bottomNavigationView.setSelectedItemId(R.id.nav_add_task);
                } else if (currentFragment instanceof CalendarFragment) {
                    bottomNavigationView.setSelectedItemId(R.id.nav_calendar);
                }
            }, 100);
        } else {
            // If we're at home fragment, exit app
            super.onBackPressed();
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
        loadFragment(addTaskFragment, "ADD_TASK");
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