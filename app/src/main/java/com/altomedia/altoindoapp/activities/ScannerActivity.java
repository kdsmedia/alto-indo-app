package com.altomedia.altoindoapp.activities;

import android.content.Intent;
import android.os.Bundle;
<<<<<<< HEAD
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.altomedia.altoindoapp.R;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

public class ScannerActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scanner);

        // Start QR scanner
        IntentIntegrator integrator = new IntentIntegrator(this);
        integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE);
        integrator.setPrompt("Scan QR Code");
        integrator.setCameraId(0);  // Use a specific camera of the device
        integrator.setBeepEnabled(true);
        integrator.setBarcodeImageEnabled(true);
        integrator.initiateScan();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if(result != null) {
            if(result.getContents() == null) {
                Toast.makeText(this, "Cancelled", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this, "Scanned: " + result.getContents(), Toast.LENGTH_LONG).show();
                // Here you can process the scanned QR code
                // For example, if it's a payment QR, you can extract the data
                Intent intent = new Intent();
                intent.putExtra("scanned_data", result.getContents());
                setResult(RESULT_OK, intent);
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
        finish(); // Close scanner after scan
    }
}
=======
import androidx.appcompat.app.AppCompatActivity;
import com.journeyapps.barcodescanner.CaptureActivity;
import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;

public class ScannerActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        ScanOptions options = new ScanOptions();
        options.setPrompt("Scan QR Member ALTOINDO");
        options.setBeepEnabled(true);
        options.setOrientationLocked(true);
        options.setCaptureActivity(CaptureActivity.class);
        
        barcodeLauncher.launch(options);
    }

    private final androidx.activity.result.ActivityResultLauncher<ScanOptions> barcodeLauncher = registerForActivityResult(new ScanContract(),
            result -> {
                if(result.getContents() != null) {
                    // Kirim hasil scan (Member ID) kembali ke TransferActivity
                    Intent data = new Intent();
                    data.putExtra("SCANNED_ID", result.getContents());
                    setResult(RESULT_OK, data);
                    finish();
                } else {
                    finish();
                }
            });
}
>>>>>>> 255cf0eca7c683250211d8dc40b5a22fa4b71580
