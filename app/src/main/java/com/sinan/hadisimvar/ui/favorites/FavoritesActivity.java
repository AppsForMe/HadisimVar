package com.sinan.hadisimvar.ui.favorites;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.sinan.hadisimvar.R;
import com.sinan.hadisimvar.data.local.entity.Hadith;
import com.sinan.hadisimvar.databinding.ActivityFavoritesBinding;

import com.sinan.hadisimvar.ui.base.BaseActivity;

public class FavoritesActivity extends BaseActivity {

    private ActivityFavoritesBinding binding;
    private FavoritesViewModel viewModel;
    private FavoritesAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityFavoritesBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Geri butonu setup
        binding.btnBack.setOnClickListener(v -> finish());

        viewModel = new ViewModelProvider(this).get(FavoritesViewModel.class);

        setupRecyclerView();
        observeData();
    }

    private void setupRecyclerView() {
        adapter = new FavoritesAdapter(new FavoritesAdapter.OnItemClickListener() {
            @Override
            public void onDeleteClick(Hadith hadith) {
                viewModel.removeFromFavorites(hadith);
            }

            @Override
            public void onItemClick(Hadith hadith) {
                android.content.Intent intent = new android.content.Intent(FavoritesActivity.this,
                        com.sinan.hadisimvar.ui.detail.HadithDetailActivity.class);
                intent.putExtra(com.sinan.hadisimvar.ui.detail.HadithDetailActivity.EXTRA_HADITH_ID, hadith.getId());
                startActivity(intent);
            }
        });
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        binding.recyclerView.setAdapter(adapter);
    }

    private void observeData() {
        viewModel.getFavoriteHadiths().observe(this, hadiths -> {
            if (hadiths != null && !hadiths.isEmpty()) {
                adapter.setHadithList(hadiths);
                binding.tvEmpty.setVisibility(View.GONE);
                binding.recyclerView.setVisibility(View.VISIBLE);
            } else {
                binding.tvEmpty.setVisibility(View.VISIBLE);
                binding.recyclerView.setVisibility(View.GONE);
            }
        });
    }
}
