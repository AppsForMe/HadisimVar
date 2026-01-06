package com.sinan.hadisimvar.ui.share;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import com.sinan.hadisimvar.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class ShareActivity extends AppCompatActivity {

    public static final String EXTRA_CONTENT = "extra_content";
    public static final String EXTRA_SOURCE = "extra_source";

    private View layoutCapture;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share);

        // Toolbar
        findViewById(R.id.toolbar).setOnClickListener(v -> finish());
        androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(v -> finish());

        layoutCapture = findViewById(R.id.layoutCapture);
        TextView tvContent = findViewById(R.id.tvShareContent);
        TextView tvSource = findViewById(R.id.tvShareSource);

        String content = getIntent().getStringExtra(EXTRA_CONTENT);
        String source = getIntent().getStringExtra(EXTRA_SOURCE);

        if (content != null)
            tvContent.setText(content);
        if (source != null)
            tvSource.setText(source);

        setupColorPickers();

        findViewById(R.id.btnShareFinal).setOnClickListener(v -> shareImage());
    }

    private void setupColorPickers() {
        findViewById(R.id.colorSafir)
                .setOnClickListener(v -> layoutCapture.setBackgroundResource(R.drawable.bg_gradient_safir));
        findViewById(R.id.colorMedine)
                .setOnClickListener(v -> layoutCapture.setBackgroundResource(R.drawable.bg_gradient_medine));
        findViewById(R.id.colorGece)
                .setOnClickListener(v -> layoutCapture.setBackgroundResource(R.drawable.bg_gradient_gece));
        findViewById(R.id.colorSahra)
                .setOnClickListener(v -> layoutCapture.setBackgroundResource(R.drawable.bg_gradient_sahra));
        findViewById(R.id.colorMermer)
                .setOnClickListener(v -> layoutCapture.setBackgroundResource(R.drawable.bg_gradient_mermer));
        findViewById(R.id.colorGunBatimi)
                .setOnClickListener(v -> layoutCapture.setBackgroundResource(R.drawable.bg_gradient_gunbatimi));
    }

    private void shareImage() {
        Bitmap bitmap = getBitmapFromView(layoutCapture);
        try {
            File cachePath = new File(getCacheDir(), "images");
            cachePath.mkdirs(); // don't forget to make the directory
            FileOutputStream stream = new FileOutputStream(cachePath + "/image.png"); // overwrites this image every
            // time
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
            stream.close();

            File imagePath = new File(getCacheDir(), "images");
            File newFile = new File(imagePath, "image.png");
            Uri contentUri = FileProvider.getUriForFile(this, getPackageName() + ".fileprovider", newFile);

            if (contentUri != null) {
                Intent shareIntent = new Intent();
                shareIntent.setAction(Intent.ACTION_SEND);
                shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION); // temp permission for receiving app to
                // read this file
                shareIntent.setDataAndType(contentUri, getContentResolver().getType(contentUri));
                shareIntent.putExtra(Intent.EXTRA_STREAM, contentUri);
                startActivity(Intent.createChooser(shareIntent, "Paylaş"));
            }

        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Resim oluşturulurken hata: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private Bitmap getBitmapFromView(View view) {
        Bitmap bitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        view.draw(canvas);
        return bitmap;
    }
}
