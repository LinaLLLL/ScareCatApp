package com.example.scarecat;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class GalleryActivity extends AppCompatActivity {
    private View gridView;
    private TextView emptyText;
    private List<File> catImages = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_gallery);

        gridView = findViewById(R.id.gridView);
        emptyText = findViewById(R.id.emptyText);

        loadImages();
        //setupGridView();
    }
    private void loadImages() {
        File storageDir = new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), "CatDetector");
        if (storageDir.exists()) {
            File[] files = storageDir.listFiles((dir, name) -> name.endsWith(".jpg") || name.endsWith(".png"));
            if (files != null) {
                catImages = Arrays.asList(files);
                Collections.reverse(catImages); // Новые фото сначала
            }
        }
    }

//    private void setupGridView() {
//        if (catImages.isEmpty()) {
//            emptyText.setVisibility(View.VISIBLE);
//            gridView.setVisibility(View.GONE);
//        } else {
//            emptyText.setVisibility(View.GONE);
//            gridView.setVisibility(View.VISIBLE);
//            gridView.setAdapter(new GalleryAdapter(this, catImages));
//
//            gridView.setOnItemClickListener((parent, view, position, id) -> {
//                // Просмотр фото в полном размере
//                Intent intent = new Intent(this, FullImageActivity.class);
//                intent.putExtra("imagePath", catImages.get(position).getAbsolutePath());
//                startActivity(intent);
//            });
//        }
//    }
}