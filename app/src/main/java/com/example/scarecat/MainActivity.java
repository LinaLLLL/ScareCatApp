package com.example.scarecat;

import android.Manifest;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.media.Image;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.util.Size;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.OptIn;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ExperimentalGetImage;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageProxy;
import androidx.camera.core.Preview;
import androidx.camera.view.PreviewView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.core.content.ContextCompat;
import com.google.common.util.concurrent.ListenableFuture;

import java.util.concurrent.ExecutionException;

public class MainActivity extends AppCompatActivity {
    private static final int PERMISSION_REQUEST_CAMERA = 9515;
    private PreviewView previewView;
    ListenableFuture<ProcessCameraProvider> cameraProviderFuture;

    YUVtoRGB translator = new YUVtoRGB();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        previewView = findViewById(R.id.preview);

        if(ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.CAMERA},
                    PERMISSION_REQUEST_CAMERA);
        }
        else{
            initializeCamera();
        }
    }

    private boolean checkXiaomiPermissions() {
        if (Build.MANUFACTURER.equalsIgnoreCase("xiaomi")) {
            try {
                ApplicationInfo info = getPackageManager().getApplicationInfo(
                        getPackageName(),
                        PackageManager.GET_META_DATA
                );
                return info.metaData.getBoolean("com.miui.securitycenter.permission", false);
            } catch (Exception e) {
                Log.e("XiaomiCheck", "Ошибка проверки разрешений", e);
            }
        }
        return true;
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (Build.MANUFACTURER.equalsIgnoreCase("xiaomi") && !checkXiaomiPermissions()) {
            Toast.makeText(this,
                    "Включите 'Автозапуск' и 'Разрешения' для этого приложения в настройках Xiaomi",
                    Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults, int deviceId) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults, deviceId);
        if(requestCode == PERMISSION_REQUEST_CAMERA && grantResults.length > 0
                && grantResults[0] == PackageManager.PERMISSION_GRANTED){
            initializeCamera();
        }
    }

    private void initializeCamera(){
        cameraProviderFuture = ProcessCameraProvider.getInstance(this);

        cameraProviderFuture.addListener(new Runnable() {
            @Override
            public void run() {
                try {
                    ProcessCameraProvider provider = cameraProviderFuture.get();
                    // 1. Настройка Preview
                    Preview preview = new Preview.Builder().build();
                    preview.setSurfaceProvider(previewView.getSurfaceProvider());
                    // 2. Настройка ImageAnalysis
                    ImageAnalysis imageAnalysis = new ImageAnalysis.Builder()
                            .setTargetResolution(new Size(1024, 768))
                            .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                            .build();

                    imageAnalysis.setAnalyzer(ContextCompat.getMainExecutor(MainActivity.this),
                           new ImageAnalysis.Analyzer() {
                                @OptIn(markerClass = ExperimentalGetImage.class)
                                @Override
                                public void analyze(@NonNull ImageProxy image) {
                                    Image img = image.getImage();
                                    Bitmap bitmap = translator.translateYUV(img, MainActivity.this);
                                    previewView.setRotation(image.getImageInfo().getRotationDegrees());
                                    runOnUiThread(() -> {
                                        previewView.setRotation(image.getImageInfo().getRotationDegrees());
                                    });
                                    image.close();
                                }
                    });

                    // 3. Выбор камеры
                    CameraSelector selector = new CameraSelector.Builder()
                            .requireLensFacing(CameraSelector.LENS_FACING_BACK)
                            .build();

                    // 4. Привязка компонентов
                    provider.unbindAll();
                    provider.bindToLifecycle(
                            MainActivity.this,
                            selector,
                            preview,
                            imageAnalysis  // Не забываем добавить анализатор!
                    );

                } catch (Exception e) {
                    Log.e("CameraX_Error", "Инициализация камеры", e);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(
                                    MainActivity.this,
                                    "Ошибка камеры: " + e.getMessage(),
                                    Toast.LENGTH_LONG
                            ).show();
                        }
                    });
                }
            }
        }, ContextCompat.getMainExecutor(this));
    }
}