package com.sinan.hadisimvar.ui.prophets;

import android.os.Bundle;

import com.sinan.hadisimvar.R;
import com.sinan.hadisimvar.ui.base.BaseActivity;

public class ProphetsActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prophets);

        // Geri butonu eklendi (Layout'ta toolbar olmalı veya basitçe bir ImageView)
        // Basitlik için layout'u güncelleyip buraya listener ekleyeceğiz.
        findViewById(R.id.btnBack).setOnClickListener(v -> finish());
    }
}
