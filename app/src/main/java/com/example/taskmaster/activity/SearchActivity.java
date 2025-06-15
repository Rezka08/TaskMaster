package com.example.taskmaster.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.taskmaster.R;
import com.example.taskmaster.adapter.SearchTaskAdapter;
import com.example.taskmaster.callback.DatabaseListCallback;
import com.example.taskmaster.model.Task;
import com.example.taskmaster.viewmodel.TaskViewModel;
import java.util.ArrayList;
import java.util.List;

public class SearchActivity extends AppCompatActivity {
    private TaskViewModel taskViewModel;
    private RecyclerView rvSearchResults;
    private SearchTaskAdapter searchTaskAdapter;
    private EditText etSearch;
    private ImageView ivBack, ivClear;
    private TextView tvResultsCount;
    private View layoutEmptyState;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.search), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        initViews();
        setupRecyclerView();
        setupViewModel();
        setupClickListeners();
        setupSearch();
    }

    private void initViews() {
        rvSearchResults = findViewById(R.id.rv_search_results);
        etSearch = findViewById(R.id.et_search);
        ivBack = findViewById(R.id.iv_back);
        ivClear = findViewById(R.id.iv_clear);
        tvResultsCount = findViewById(R.id.tv_results_count);
        layoutEmptyState = findViewById(R.id.layout_empty_state);

        // Focus on search field
        etSearch.requestFocus();
    }

    private void setupRecyclerView() {
        searchTaskAdapter = new SearchTaskAdapter();
        searchTaskAdapter.setOnItemClickListener(task -> {
            // Navigate to Task Detail
            Intent intent = new Intent(this, TaskDetailActivity.class);
            intent.putExtra("task_id", task.getId());
            startActivityForResult(intent, 1001); // Use startActivityForResult to detect changes
        });

        rvSearchResults.setLayoutManager(new LinearLayoutManager(this));
        rvSearchResults.setAdapter(searchTaskAdapter);
    }

    private void setupViewModel() {
        taskViewModel = new ViewModelProvider(this).get(TaskViewModel.class);
    }

    private void setupClickListeners() {
        ivBack.setOnClickListener(v -> finish());

        ivClear.setOnClickListener(v -> {
            etSearch.setText("");
            etSearch.requestFocus();
        });
    }

    private void setupSearch() {
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String query = s.toString().trim();

                // Show/hide clear button
                ivClear.setVisibility(query.isEmpty() ? View.GONE : View.VISIBLE);

                if (query.isEmpty()) {
                    // Show empty state when no query
                    showEmptyState(true, "Start typing to search tasks...");
                    tvResultsCount.setText("");
                } else if (query.length() >= 2) {
                    // Search when query has at least 2 characters
                    searchTasks(query);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void searchTasks(String query) {
        taskViewModel.searchTasks(query, new DatabaseListCallback<Task>() {
            @Override
            public void onSuccess(List<Task> tasks) {
                runOnUiThread(() -> {
                    if (tasks != null && !tasks.isEmpty()) {
                        searchTaskAdapter.setTasks(tasks);
                        showEmptyState(false, "");
                        tvResultsCount.setText(tasks.size() + " result" + (tasks.size() == 1 ? "" : "s") + " found");
                    } else {
                        showEmptyState(true, "No tasks found for \"" + query + "\"");
                        tvResultsCount.setText("0 results found");
                    }
                });
            }

            @Override
            public void onError(String error) {
                runOnUiThread(() -> {
                    showEmptyState(true, "Error searching tasks");
                    tvResultsCount.setText("Search error");
                });
            }
        });
    }

    private void showEmptyState(boolean show, String message) {
        if (show) {
            layoutEmptyState.setVisibility(View.VISIBLE);
            rvSearchResults.setVisibility(View.GONE);

            TextView tvEmptyMessage = layoutEmptyState.findViewById(R.id.tv_empty_message);
            if (tvEmptyMessage != null) {
                tvEmptyMessage.setText(message);
            }
        } else {
            layoutEmptyState.setVisibility(View.GONE);
            rvSearchResults.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Refresh search results when returning from TaskDetailActivity if data changed
        if (requestCode == 1001 && resultCode == RESULT_OK) {
            if (data != null && data.getBooleanExtra("data_changed", false)) {
                // Refresh search results
                String query = etSearch.getText().toString().trim();
                if (!query.isEmpty() && query.length() >= 2) {
                    searchTasks(query);
                }
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Refresh search results if there's a query
        String query = etSearch.getText().toString().trim();
        if (!query.isEmpty() && query.length() >= 2) {
            searchTasks(query);
        }
    }
}