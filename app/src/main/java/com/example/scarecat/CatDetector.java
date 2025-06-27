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
    private MediaPlayer mediaPlayer; //воспроизведение звука
    private boolean isCatVisible = false;  // флаг видимости кота
    private long lastCatSeenTime = 0;
    private static final long CAT_TIMEOUT_MS = 1500; // сколько ждать, прежде чем выключать звук

    //конструктор
    public CatDetector(Context context, int soundResId) {
        labeler = ImageLabeling.getClient(ImageLabelerOptions.DEFAULT_OPTIONS);
        mediaPlayer = MediaPlayer.create(context, soundResId);
    }

    public void detectCats(@NonNull InputImage image, DetectionCallback callback) {
        labeler.process(image)  //запуск ассинхронного процесса распознавания
                .addOnSuccessListener(labels -> { //получаем список меток
                    boolean catFoundNow = false;

                    for (ImageLabel label : labels) {
                        if ("Cat".equalsIgnoreCase(label.getText()) && label.getConfidence() > 0.5) {
                            catFoundNow = true;
                            lastCatSeenTime = System.currentTimeMillis();
                            if (!mediaPlayer.isPlaying()) {
                                mediaPlayer.setLooping(true);
                                mediaPlayer.start();
                            }
                            callback.onCatDetected(label.getConfidence());
                            break;
                        }
                    }

                    if (!catFoundNow) {
                        long timeSinceSeen = System.currentTimeMillis() - lastCatSeenTime;

                        if (timeSinceSeen > CAT_TIMEOUT_MS) {
                            if (mediaPlayer.isPlaying()) {
                                mediaPlayer.pause();
                                mediaPlayer.seekTo(0);
                            }
                            callback.onNoCatDetected();
                        } else {
                            // кот временно пропал — подождем ещё
                            callback.onCatDetected(0); // можно использовать 0 как "промежуточный" флаг
                        }
                    }
                })
                .addOnFailureListener(e -> {
                    callback.onError(e);
                });
    }

    //очистка ресурсов
    public void release() {
        labeler.close();
        mediaPlayer.release();
    }

}