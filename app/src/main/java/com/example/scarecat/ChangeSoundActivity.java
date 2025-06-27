package com.example.scarecat;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.widget.Button;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;

public class ChangeSoundActivity extends AppCompatActivity {

    private MediaPlayer previewPlayer;  // для предпрослушивания
    private int selectedSoundResId = R.raw.cat_sound_first; // по умолчанию
    private Button selectedButton = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_sound);

        SoundItem[] soundItems = {
                new SoundItem("Спокойная мелодия", R.raw.cat_sound_first),
                new SoundItem("Сирена", R.raw.cat_sound_second),
                new SoundItem("Крик", R.raw.cat_sound_third)
        };

        LinearLayout musicList = findViewById(R.id.music_list_container);

        for (SoundItem item : soundItems) {
            Button soundButton = new Button(this);
            soundButton.setText(item.getName());
            soundButton.setTag(item.getResId());

            // Стилизация кнопки
            soundButton.setBackgroundResource(R.color.btn_color);
            soundButton.setTextColor(getResources().getColor(R.color.white));
            soundButton.setAllCaps(false);
            soundButton.setTextSize(16);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);
            params.setMargins(0, 20, 0, 16);
            soundButton.setLayoutParams(params);
            soundButton.setPadding(32, 32, 32, 32);

            // Обработчик нажатия — воспроизведение + возврат результата
            soundButton.setOnClickListener(v -> {
               playPreviewSound(item.getResId());
                selectedSoundResId = item.getResId();
                // Сброс предыдущей кнопки (если есть)
                if (selectedButton != null) {
                    selectedButton.setBackgroundResource(R.color.btn_color); // обычный цвет
                }

                // Установка нового выбранного
                selectedButton = soundButton;
                selectedButton.setBackgroundResource(R.color.btn_color_selected); // выбранный цвет
            });
            musicList.addView(soundButton);
        }

        // Кнопка подтверждения выбора
        findViewById(R.id.btn_conf).setOnClickListener(v -> {
            Intent result = new Intent();
            result.putExtra("selected_music", selectedSoundResId);
            setResult(RESULT_OK, result);
            finish();
        });
    }

    // Предпрослушивание звука
    private void playPreviewSound(int soundResId) {
        stopPreviewSound(); // остановить предыдущий, если был

        previewPlayer = MediaPlayer.create(this, soundResId);
        previewPlayer.setOnCompletionListener(mp -> stopPreviewSound());
        previewPlayer.start();
    }

    // Остановить и освободить ресурсы
    private void stopPreviewSound() {
        if (previewPlayer != null) {
            if (previewPlayer.isPlaying()) {
                previewPlayer.stop();
            }
            previewPlayer.release();
            previewPlayer = null;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopPreviewSound();
    }

    // Вложенный класс — элемент списка
    private static class SoundItem {
        private final String name;
        private final int resId;

        public SoundItem(String name, int resId) {
            this.name = name;
            this.resId = resId;
        }

        public String getName() {
            return name;
        }

        public int getResId() {
            return resId;
        }
    }
}
