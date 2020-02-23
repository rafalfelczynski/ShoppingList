package ib.edu.shoppingList;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.SparseArray;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;

import java.io.IOException;

public class MyBarcodeScanner extends AppCompatActivity {

    public static final String BARCODE_KEY = "barcode";

    private CameraSource cameraSource;
    private SurfaceView surfaceView;
    private TextView textView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.my_barcode_layout);
        surfaceView = findViewById(R.id.camera);
        textView = findViewById(R.id.barcodeTextView);
        BarcodeDetector detector = new BarcodeDetector.Builder(this).build();
        cameraSource = new CameraSource.Builder(this, detector).setRequestedPreviewSize(MainActivity.SCREEN_HEIGHT/2, MainActivity.SCREEN_WIDTH).setAutoFocusEnabled(true).build();
        surfaceView.getLayoutParams().width = MainActivity.SCREEN_WIDTH;
        surfaceView.getLayoutParams().height = MainActivity.SCREEN_HEIGHT/2;
        surfaceView.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                if (ActivityCompat.checkSelfPermission(MyBarcodeScanner.this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                    try {
                        cameraSource.start(holder);
                    } catch (IOException e) {
                        Toast.makeText(MyBarcodeScanner.this, "Błąd aparatu, przeładuj apliakcje", Toast.LENGTH_LONG).show();
                    }
                }
            }
            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                cameraSource.stop();
            }
        });
        detector.setProcessor(new Detector.Processor<Barcode>() {
            @Override
            public void release() {
            }
            @Override
            public void receiveDetections(Detector.Detections<Barcode> detections) {
                final SparseArray<Barcode> barcodes = detections.getDetectedItems();
                if (barcodes.size() != 0) {
                    textView.post(new Runnable() {
                        @Override
                        public void run() {
                            Vibrator vibrator = (Vibrator) getApplicationContext().getSystemService(Context.VIBRATOR_SERVICE);
                            vibrator.vibrate(1000);
                            String code = barcodes.valueAt(0).displayValue;
                            textView.setText(code);
                            Intent resultIntent = new Intent();
                            resultIntent.putExtra(BARCODE_KEY, code);
                            setResult(Activity.RESULT_OK, resultIntent);
                            finish();
                        }
                    });
                }
            }
        });

    }


}





