package com.sinan.hadisimvar.ui.topics;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;

import com.sinan.hadisimvar.R;
import com.sinan.hadisimvar.data.local.entity.Hadith;
import com.sinan.hadisimvar.ui.allhadiths.AllHadithsActivity;
import com.sinan.hadisimvar.ui.allhadiths.AllHadithsViewModel;
import com.sinan.hadisimvar.ui.base.BaseActivity;
import com.sinan.hadisimvar.databinding.ActivityTopicsBinding;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class TopicsActivity extends BaseActivity {

    private ActivityTopicsBinding binding;
    private AllHadithsViewModel viewModel;
    private TopicsAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityTopicsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Toolbar
        setSupportActionBar(binding.toolbar);
        binding.toolbar.setNavigationOnClickListener(v -> finish());

        setupRecyclerView();

        viewModel = new ViewModelProvider(this).get(AllHadithsViewModel.class);
        viewModel.getAllHadiths().observe(this, hadiths -> {
            if (hadiths != null) {
                // Extract unique topics
                Set<String> uniqueTopics = new HashSet<>();
                for (Hadith h : hadiths) {
                    if (h.topics != null && !h.topics.isEmpty()) {
                        String[] split = h.topics.split(",");
                        for (String t : split) {
                            uniqueTopics.add(t.trim());
                        }
                    }
                }
                // Sort list
                List<String> sortedTopics = new ArrayList<>(uniqueTopics);
                java.util.Collections.sort(sortedTopics);
                adapter.setTopics(sortedTopics);
            }
        });
    }

    private void setupRecyclerView() {
        adapter = new TopicsAdapter();
        adapter.setOnItemClickListener(topic -> {
            // Open AllHadithsActivity with a search query or filter
            // Since we implemented filter logic in ViewModel, we can pass intent extra
            // But AllHadithsActivity needs to handle it.
            // For now, let's just toast and maybe implement search later.
            // Or better: Pass the topic as a filter to AllHadithsActivity if it supported
            // string search
            // Our current filter impl supports substring match on source or category.
            // We should improve AllHadithsViewModel to search in topics too.

            // Let's assume we update AllHadiths logic to handle general search
            // For now, simple toast to show it works
            // android.widget.Toast.makeText(this, "Seçilen Konu: " + topic,
            // android.widget.Toast.LENGTH_SHORT).show();

            // TODO: Implement passing search query to AllHadithsActivity
            Intent intent = new Intent(TopicsActivity.this, AllHadithsActivity.class);
            intent.putExtra("EXTRA_FILTER_TOPIC", topic);
            startActivity(intent);
        });

        binding.recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        binding.recyclerView.setAdapter(adapter);
    }
}
