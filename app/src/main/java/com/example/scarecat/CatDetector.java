package com.example.scarecat;

import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.objects.DetectedObject;
import com.google.mlkit.vision.objects.ObjectDetection;
import com.google.mlkit.vision.objects.ObjectDetector;
import com.google.mlkit.vision.objects.defaults.ObjectDetectorOptions;
public class CatDetector {
    private final ObjectDetector detector;

    public CatDetector(){
        ObjectDetectorOptions options = new ObjectDetectorOptions().Builder()
                .setDetectorMode(ObjectDetectorOptions.STREAM_MODE)
                .enableClassification() //включаем классификацию
                .buid();
    }
}
