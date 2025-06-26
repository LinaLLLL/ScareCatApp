package com.example.scarecat;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MenuActivity extends AppCompatActivity {
    Button btn_detector;
    Button btn_sound;
    Button btn_gallery;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_menu);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        btn_detector = findViewById(R.id.btn_detector);
        btn_sound = findViewById(R.id.btn_sound);
        btn_gallery = findViewById(R.id.btn_gallery);

    }
    //метод для перехода на страницу распознавания котов
    public void goDetectorActivity(View v){
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
    //метод для перехода на страницу выбора звука
    public void goSoundActivity(View v){
        Intent intent = new Intent(this, ChangeSoundActivity.class);
        startActivity(intent);
    }
    //метод для перехода на страницу галереи
    public void goGalleryActivity(View v){
        Intent intent = new Intent(this, GalleryActivity.class);
        startActivity(intent);
    }

}