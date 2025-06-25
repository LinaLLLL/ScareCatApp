package com.example.scarecat;

import android.content.Context;
import android.media.MediaPlayer;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.label.ImageLabel;
import com.google.mlkit.vision.label.ImageLabeler;
import com.google.mlkit.vision.label.ImageLabeling;
import com.google.mlkit.vision.label.defaults.ImageLabelerOptions;

import java.util.List;

public class CatDetector {
    public interface DetectionCallback { //интерфейс обратного вызова (Callback)
        void onCatDetected(float confidence);
        void onNoCatDetected();
        void onError(Exception e);
    }

    private final ImageLabeler labeler; //распознавание
    private final MediaPlayer mediaPlayer; //воспроизведение звука
    //конструктор
    public CatDetector(Context context) {
        labeler = ImageLabeling.getClient(ImageLabelerOptions.DEFAULT_OPTIONS);
        mediaPlayer = MediaPlayer.create(context, R.raw.cat_sound);
    }

    public void detectCats(@NonNull InputImage image, DetectionCallback callback) {
        labeler.process(image)  //запуск ассинхронного процесса распознавания
                .addOnSuccessListener(labels -> { //получаем список меток
                    boolean catFound = false;
                    for (ImageLabel label : labels) {  //перебираем список и ищем с текстом "cat"
                        if ("Cat".equalsIgnoreCase(label.getText()) && label.getConfidence() > 0.5) { //проверяем уверенность больше 50%
                            catFound = true;

                            if (!mediaPlayer.isPlaying()) { //если кот найден, запускаем музыку
                                mediaPlayer.start();
                            }

                            callback.onCatDetected(label.getConfidence());
                            break;
                        }
                    }
                    if (!catFound) { // кот не найден
                        callback.onNoCatDetected();
                        if (mediaPlayer.isPlaying()) { //если музыка играла - выключаем
                            mediaPlayer.pause();
                            mediaPlayer.seekTo(0);
                        }
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("CatDetector", "Ошибка детекции", e);
                    callback.onError(e);
                });
    }
    //очистка ресурсов
    public void release() {
        labeler.close();
        mediaPlayer.release();
    }

}
