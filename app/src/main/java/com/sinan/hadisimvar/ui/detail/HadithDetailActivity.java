package com.sinan.hadisimvar.ui.detail;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.lifecycle.ViewModelProvider;

import com.sinan.hadisimvar.R;
import com.sinan.hadisimvar.data.local.entity.Hadith;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.sinan.hadisimvar.ui.base.BaseActivity;

public class HadithDetailActivity extends BaseActivity {

    private static final String TAG = "HadithDetailActivity";
    public static final String EXTRA_HADITH_ID = "extra_hadith_id";

    private HadithDetailViewModel viewModel;
    private int hadithId;
    private Hadith currentHadith;
    private SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hadith_detail);

        // Geri butonu setup
        findViewById(R.id.btnBack).setOnClickListener(v -> finish());

        // Get ID
        if (getIntent() != null) {
            hadithId = getIntent().getIntExtra(EXTRA_HADITH_ID, -1);
        }

        prefs = getSharedPreferences("settings_prefs", MODE_PRIVATE);

        if (hadithId == -1) {
            Toast.makeText(this, "Hata: Hadis bulunamadı.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        viewModel = new ViewModelProvider(this).get(HadithDetailViewModel.class);

        observeData();
        setupListeners();
    }

    private void observeData() {
        // Loading göster
        ProgressBar progressBar = findViewById(R.id.progressBar);
        if (progressBar != null) {
            progressBar.setVisibility(View.VISIBLE);
        }

        // Doğrudan ID ile hadis al - performans açısından çok daha iyi
        viewModel.getHadithById(hadithId).observe(this, hadith -> {
            if (progressBar != null) {
                progressBar.setVisibility(View.GONE);
            }

            if (hadith != null) {
                currentHadith = hadith;
                updateUI(hadith);
                Log.d(TAG, "Hadis yüklendi: " + hadith.getId());
            } else {
                Log.w(TAG, "Hadis bulunamadı: " + hadithId);
                Toast.makeText(this, "Hadis yüklenemedi.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateUI(Hadith hadith) {
        TextView tvContent = findViewById(R.id.tvHadithContent);
        TextView tvSource = findViewById(R.id.tvHadithSource);
        TextView tvAuthenticity = findViewById(R.id.tvAuthenticity);
        TextView tvCategory = findViewById(R.id.tvSourceCategory);
        TextView tvExplanation = findViewById(R.id.tvExplanation);
        View cardExplanation = findViewById(R.id.cardExplanation);
        ChipGroup chipGroup = findViewById(R.id.chipGroupTopics);

        tvContent.setText(hadith.content);
        if (prefs != null) {
            float fontSize = prefs.getFloat("font_size", 18f); // Default 18sp
            tvContent.setTextSize(fontSize);
        }
        tvSource.setText(hadith.source);

        // Sıhhat durumu
        if (hadith.authenticity != null && !hadith.authenticity.isEmpty()) {
            tvAuthenticity.setText(hadith.authenticity);
            tvAuthenticity.setVisibility(View.VISIBLE);
            // Renk ayarı - colors.xml'den alınan değerler
            int color;
            if (hadith.authenticity.equalsIgnoreCase("Zayıf")) {
                color = getResources().getColor(R.color.authenticity_weak, getTheme());
            } else if (hadith.authenticity.equalsIgnoreCase("Sahih")) {
                color = getResources().getColor(R.color.authenticity_sahih, getTheme());
            } else {
                color = getResources().getColor(R.color.authenticity_other, getTheme());
            }
            tvAuthenticity.setBackgroundTintList(ColorStateList.valueOf(color));
        } else {
            tvAuthenticity.setVisibility(View.GONE);
        }

        // Kategori
        if (hadith.sourceCategory != null && !hadith.sourceCategory.isEmpty()) {
            tvCategory.setText(hadith.sourceCategory);
            tvCategory.setVisibility(View.VISIBLE);
        } else {
            tvCategory.setVisibility(View.GONE);
        }

        // Şerh
        if (hadith.explanation != null && !hadith.explanation.isEmpty()) {
            tvExplanation.setText(hadith.explanation);
            cardExplanation.setVisibility(View.VISIBLE);
        } else {
            cardExplanation.setVisibility(View.GONE);
        }

        // Konular
        chipGroup.removeAllViews();
        if (hadith.topics != null && !hadith.topics.isEmpty()) {
            String[] topics = hadith.topics.split(",");
            for (String topic : topics) {
                Chip chip = new Chip(this);
                chip.setText(topic.trim());
                chip.setTextColor(getResources().getColor(R.color.primary_green, getTheme()));
                chip.setChipBackgroundColor(ColorStateList.valueOf(
                        getResources().getColor(R.color.secondary_green, getTheme())));
                chipGroup.addView(chip);
            }
            findViewById(R.id.tvTopicsLabel).setVisibility(View.VISIBLE);
        } else {
            findViewById(R.id.tvTopicsLabel).setVisibility(View.GONE);
        }
    }

    private void setupListeners() {
        findViewById(R.id.btnCopy).setOnClickListener(v -> {
            if (currentHadith != null) {
                String textToCopy = currentHadith.content + "\n\n(" + currentHadith.source + ")";
                ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("Hadis", textToCopy);
                clipboard.setPrimaryClip(clip);
                Toast.makeText(this, "Hadis kopyalandı", Toast.LENGTH_SHORT).show();
            }
        });

        findViewById(R.id.btnShare).setOnClickListener(v -> {
            if (currentHadith != null) {
                Intent intent = new Intent(this, com.sinan.hadisimvar.ui.share.ShareActivity.class);
                intent.putExtra(com.sinan.hadisimvar.ui.share.ShareActivity.EXTRA_CONTENT, currentHadith.content);
                intent.putExtra(com.sinan.hadisimvar.ui.share.ShareActivity.EXTRA_SOURCE, currentHadith.source);
                startActivity(intent);
            }
        });
    }
}
