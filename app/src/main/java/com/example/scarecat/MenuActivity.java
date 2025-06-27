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
    private static final int REQUEST_CODE_SELECT_SOUND = 123;
    private int selectedSoundResId = R.raw.cat_sound_first;;

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

    }

    //метод для перехода на страницу распознавания котов
    public void goDetectorActivity(View v){
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("sound_res_id", selectedSoundResId);
        startActivity(intent);
    }
    //метод для перехода на страницу выбора звука
    public void goSoundActivity(View v){
        Intent intent = new Intent(this, ChangeSoundActivity.class);
        startActivityForResult(intent, REQUEST_CODE_SELECT_SOUND);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_SELECT_SOUND && resultCode == RESULT_OK) {
            selectedSoundResId = data.getIntExtra("selected_music", R.raw.cat_sound_first);
        }
    }

}