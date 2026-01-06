package com.sinan.hadisimvar.ui.landing;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.cardview.widget.CardView;

import com.sinan.hadisimvar.R;
import com.sinan.hadisimvar.ui.base.BaseActivity;
import com.sinan.hadisimvar.ui.home.MainActivity;
import com.sinan.hadisimvar.ui.prophets.ProphetsActivity;

public class LandingActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_landing);

        CardView cardHadiths = findViewById(R.id.cardHadiths);
        CardView cardProphets = findViewById(R.id.cardProphets);

        cardHadiths.setOnClickListener(v -> {
            Intent intent = new Intent(LandingActivity.this, MainActivity.class);
            startActivity(intent);
        });

        cardProphets.setOnClickListener(v -> {
            Intent intent = new Intent(LandingActivity.this, ProphetsActivity.class);
            startActivity(intent);
        });

    }
}
