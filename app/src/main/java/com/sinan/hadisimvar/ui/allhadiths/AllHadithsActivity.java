package com.sinan.hadisimvar.ui.allhadiths;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.sinan.hadisimvar.databinding.ActivityAllHadithsBinding;
import com.sinan.hadisimvar.ui.favorites.FavoritesAdapter; // Favori adapterini yeniden kullanabiliriz veya kopyalayabiliriz. Basitlik için yeniden kullanalim ama layout delete butonu iceriyor. En iyisi AllHadithsAdapter yazmak.
import com.sinan.hadisimvar.R;

import com.sinan.hadisimvar.ui.base.BaseActivity;

// AdMob imports
import com.sinan.hadisimvar.ads.AdHelper;

public class AllHadithsActivity extends BaseActivity {

    private ActivityAllHadithsBinding binding;
    private AllHadithsViewModel viewModel;
    private AllHadithsAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAllHadithsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Geri butonu setup
        binding.btnBack.setOnClickListener(v -> finish());

        viewModel = new ViewModelProvider(this).get(AllHadithsViewModel.class);

        setupRecyclerView();
        setupObservers();
        setupSpinner(); // Initial call to set up filters and trigger initial data load

        // ═══════════════════════════════════════════════════════════════
        // 💰 ADMOB BANNER
        // ═══════════════════════════════════════════════════════════════
        AdHelper.loadBannerAd(this, binding.adBannerContainer);

        // Intent handling is now inside setupSpinner to wait for data load
    }

    private void setupObservers() {
        viewModel.getFilteredHadiths().observe(this, hadiths -> {
            if (hadiths != null && !hadiths.isEmpty()) {
                adapter.setHadithList(hadiths);
                binding.tvEmpty.setVisibility(android.view.View.GONE);
                binding.recyclerView.setVisibility(android.view.View.VISIBLE);
            } else {
                binding.tvEmpty.setVisibility(android.view.View.VISIBLE);
                binding.recyclerView.setVisibility(android.view.View.GONE);
            }
        });
    }

    private void setupSpinner() {
        android.widget.Spinner spinner = binding.spinnerTopics;
        viewModel.getUniqueTopics().observe(this, topics -> {
            if (topics != null) {
                android.widget.ArrayAdapter<String> adapter = new android.widget.ArrayAdapter<>(
                        this,
                        R.layout.simple_spinner_item_custom, // Custom layout or default
                        topics);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinner.setAdapter(adapter);

                // If we have an intent filter, try to select it
                if (getIntent() != null && getIntent().hasExtra("EXTRA_FILTER_TOPIC")) {
                    String initialTopic = getIntent().getStringExtra("EXTRA_FILTER_TOPIC");
                    int position = adapter.getPosition(initialTopic);
                    if (position >= 0) {
                        spinner.setSelection(position);
                    }
                }
            }
        });

        spinner.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(android.widget.AdapterView<?> parent, android.view.View view, int position,
                    long id) {
                String selected = (String) parent.getItemAtPosition(position);
                viewModel.setFilter(selected);
            }

            @Override
            public void onNothingSelected(android.widget.AdapterView<?> parent) {
            }
        });
    }

    private void setupRecyclerView() {
        adapter = new AllHadithsAdapter();
        adapter.setOnItemClickListener(hadith -> {
            android.content.Intent intent = new android.content.Intent(this,
                    com.sinan.hadisimvar.ui.detail.HadithDetailActivity.class);
            intent.putExtra(com.sinan.hadisimvar.ui.detail.HadithDetailActivity.EXTRA_HADITH_ID, hadith.getId());
            startActivity(intent);
        });
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        binding.recyclerView.setAdapter(adapter);
    }
}
