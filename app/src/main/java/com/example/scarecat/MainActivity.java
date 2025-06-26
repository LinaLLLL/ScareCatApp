package com.example.scarecat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.util.Size;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.OptIn;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ExperimentalGetImage;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageProxy;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.common.util.concurrent.ListenableFuture;
import com.google.mlkit.vision.common.InputImage;

import java.util.concurrent.ExecutionException;

public class MainActivity extends AppCompatActivity {
    private static final int PERMISSION_REQUEST_CAMERA = 1001; //константа для идентификации запроса разрешения на камеру
    private PreviewView previewView;
    private CatDetector catDetector;
    private ListenableFuture<ProcessCameraProvider> cameraProviderFuture;
    private ImageButton btnFlipCamera;
    private CameraSelector cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA;

    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        previewView = findViewById(R.id.preview);

        catDetector = new CatDetector(this);

        btnFlipCamera = findViewById(R.id.btn_flip_camera);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) //запрос разрешения на камеру
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, PERMISSION_REQUEST_CAMERA);
        } else {
            startCamera();
        }
    }

    private void startCamera() {
        cameraProviderFuture = ProcessCameraProvider.getInstance(this);

        cameraProviderFuture.addListener(() -> {
            try {
                ProcessCameraProvider cameraProvider = cameraProviderFuture.get();

                Preview preview = new Preview.Builder()
                        .setTargetResolution(new Size(1280, 720))
                        .build();

                preview.setSurfaceProvider(previewView.getSurfaceProvider());

                ImageAnalysis imageAnalysis = new ImageAnalysis.Builder() // компонент, который будет получать видеокадры для анализа
                        .setTargetResolution(new Size(1280, 720))
                        .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                        .build();

                imageAnalysis.setAnalyzer(ContextCompat.getMainExecutor(this), this::analyzeImage);

//                CameraSelector cameraSelector = new CameraSelector.Builder() //выбираем заднюю камеру
//                        .requireLensFacing(CameraSelector.LENS_FACING_BACK)
//                        .build();

                cameraProvider.unbindAll(); //отвязываем предыдущие камеры если были
                cameraProvider.bindToLifecycle(this, cameraSelector, preview, imageAnalysis); // привязываем к жизненному циклу

            } catch (ExecutionException | InterruptedException e) {
                Log.e("MainActivity", "Ошибка запуска камеры", e);
            }
        }, ContextCompat.getMainExecutor(this));
    }

    public void switchCamera(View V) {
        // Переключаем камеру
        cameraSelector = (cameraSelector == CameraSelector.DEFAULT_BACK_CAMERA)
                ? CameraSelector.DEFAULT_FRONT_CAMERA
                : CameraSelector.DEFAULT_BACK_CAMERA;

        // Проверяем доступность камеры
        try {
            if (!cameraProviderFuture.get().hasCamera(cameraSelector)) {
                Toast.makeText(this, "Камера не доступна", Toast.LENGTH_SHORT).show();
                return;
            }
        } catch (Exception e) {
            Log.e("MainActivity", "Ошибка проверки камеры", e);
            return;
        }

        // Перезапускаем камеру
        startCamera();

        // Анимация кнопки (опционально)
        btnFlipCamera.animate().rotationBy(180f).setDuration(300).start();
    }

    @OptIn(markerClass = ExperimentalGetImage.class)
    private void analyzeImage(@NonNull ImageProxy imageProxy) { // метод вызывается на каждом кадре камеры
        ImageProxy proxyToClose = imageProxy; // для доступа из лямбд

        if (imageProxy == null || imageProxy.getImage() == null) { //проверка доступности изображения
            imageProxy.close();
            return;
        }
        //конвертация в нужный формат
        InputImage inputImage = InputImage.fromMediaImage(imageProxy.getImage(), imageProxy.getImageInfo().getRotationDegrees());

        catDetector.detectCats(inputImage, new CatDetector.DetectionCallback() {
            @Override
            public void onCatDetected(float confidence) {
                runOnUiThread(() -> Toast.makeText(MainActivity.this,
                        String.format("Кот обнаружен! Уверенность: %.0f%%", confidence * 100),
                        Toast.LENGTH_SHORT).show());
                proxyToClose.close();  // Закрываем после успешной обработки
            }
            @Override
            public void onNoCatDetected() {
                proxyToClose.close();  // Закрываем тоже
            }
            @Override
            public void onError(Exception e) {
                Log.e("MainActivity", "Ошибка распознавания", e);
                proxyToClose.close();  // Закрываем при ошибке
            }
        });
    }

    //При завершении активности освобождаем ресурсы catDetector
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (catDetector != null) {
            catDetector.release();
        }
    }
    //Обработка разрешений на камеру
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CAMERA && grantResults.length > 0
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            startCamera();
        }
    }
}