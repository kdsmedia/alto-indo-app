package com.altomedia.altoindoapp.activities;

import android.content.ContentValues;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.provider.MediaStore;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.altomedia.altoindoapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

public class TopUpActivity extends AppCompatActivity {
    private static final String QRIS_BASE = "00020101021126610014COM.GO-JEK.WWW01189360091439663050810210G9663050810303UMI51440014ID.CO.QRIS.WWW0215ID10254671365660303UMI5204549953033605802ID5917ALTOMEDIA, Grosir6008KARAWANG61054136162070703A016304D21A";

    private Button btnNominal10k, btnNominal50k, btnNominal100k, btnGenerate, btnSaveQr;
    private EditText etAmount, etSenderName, etSenderMessage;
    private LinearLayout qrContainer;
    private ImageView ivQr;
    private TextView tvPayTimer, tvTotalDisplay, tvTrxId;
    private CountDownTimer countDownTimer;
    private Bitmap currentQrBitmap;
    private String currentTrxId;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_topup);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        btnNominal10k = findViewById(R.id.btn_nominal_10k);
        btnNominal50k = findViewById(R.id.btn_nominal_50k);
        btnNominal100k = findViewById(R.id.btn_nominal_100k);
        btnGenerate = findViewById(R.id.btn_generate);
        btnSaveQr = findViewById(R.id.btn_save_qr);
        etAmount = findViewById(R.id.et_amount);
        etSenderName = findViewById(R.id.et_sender_name);
        etSenderMessage = findViewById(R.id.et_sender_message);
        qrContainer = findViewById(R.id.qr_container);
        ivQr = findViewById(R.id.iv_qr);
        tvPayTimer = findViewById(R.id.tv_pay_timer);
        tvTotalDisplay = findViewById(R.id.tv_total_display);
        tvTrxId = findViewById(R.id.tv_trx_id);

        btnNominal10k.setOnClickListener(v -> setNominal(10000, btnNominal10k));
        btnNominal50k.setOnClickListener(v -> setNominal(50000, btnNominal50k));
        btnNominal100k.setOnClickListener(v -> setNominal(100000, btnNominal100k));
        btnGenerate.setOnClickListener(v -> generateQr());
        btnSaveQr.setOnClickListener(v -> saveQrImage());
    }

    private void setNominal(int value, Button selectedButton) {
        etAmount.setText(String.valueOf(value));
        btnNominal10k.setSelected(false);
        btnNominal50k.setSelected(false);
        btnNominal100k.setSelected(false);
        selectedButton.setSelected(true);
    }

    private void generateQr() {
        String amountText = etAmount.getText().toString().trim();
        String name = etSenderName.getText().toString().trim();
        String message = etSenderMessage.getText().toString().trim();

        if (amountText.isEmpty()) {
            Toast.makeText(this, "Masukkan nominal topup", Toast.LENGTH_SHORT).show();
            return;
        }

        long amount;
        try {
            amount = Long.parseLong(amountText);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Nominal tidak valid", Toast.LENGTH_SHORT).show();
            return;
        }

        if (amount < 10000) {
            Toast.makeText(this, "Minimal Topup Rp 10.000", Toast.LENGTH_SHORT).show();
            return;
        }

        String qrText = buildQris(amountText);
        try {
            currentQrBitmap = createQrBitmap(qrText);
            ivQr.setImageBitmap(currentQrBitmap);
        } catch (WriterException e) {
            Toast.makeText(this, "Gagal membuat QR", Toast.LENGTH_SHORT).show();
            return;
        }

        tvTotalDisplay.setText("Rp " + String.format("%,d", amount).replace(',', '.'));
        currentTrxId = String.valueOf(System.currentTimeMillis() / 1000);
        tvTrxId.setText("TRX-ID: " + currentTrxId);
        qrContainer.setVisibility(android.view.View.VISIBLE);

        startTimer();
        saveTopupRequest(amount, name, message);
    }

    private String buildQris(String amountText) {
        String qrisTanpaCRC = QRIS_BASE.split("6304")[0];
        String tagNominal = "54" + String.format("%02d", amountText.length()) + amountText;
        String dataSiapCRC = qrisTanpaCRC + tagNominal + "6304";
        return dataSiapCRC + crc16(dataSiapCRC);
    }

    private static String crc16(String data) {
        int crc = 0xFFFF;
        for (int i = 0; i < data.length(); i++) {
            crc ^= data.charAt(i) << 8;
            for (int j = 0; j < 8; j++) {
                if ((crc & 0x8000) != 0) {
                    crc = (crc << 1) ^ 0x1021;
                } else {
                    crc <<= 1;
                }
            }
        }
        crc &= 0xFFFF;
        return String.format("%04X", crc);
    }

    private Bitmap createQrBitmap(String text) throws WriterException {
        QRCodeWriter writer = new QRCodeWriter();
        BitMatrix matrix = writer.encode(text, BarcodeFormat.QR_CODE, 400, 400);
        Bitmap bitmap = Bitmap.createBitmap(matrix.getWidth(), matrix.getHeight(), Bitmap.Config.ARGB_8888);
        for (int x = 0; x < matrix.getWidth(); x++) {
            for (int y = 0; y < matrix.getHeight(); y++) {
                bitmap.setPixel(x, y, matrix.get(x, y) ? 0xFF000000 : 0xFFFFFFFF);
            }
        }
        return bitmap;
    }

    private void startTimer() {
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }

        countDownTimer = new CountDownTimer(180000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                long seconds = millisUntilFinished / 1000;
                long minutes = seconds / 60;
                long remainder = seconds % 60;
                tvPayTimer.setText(String.format("Selesaikan dalam %02d:%02d", minutes, remainder));
            }

            @Override
            public void onFinish() {
                tvPayTimer.setText("Waktu habis");
                Toast.makeText(TopUpActivity.this, "Waktu habis.", Toast.LENGTH_SHORT).show();
            }
        }.start();
    }

    private void saveTopupRequest(long amount, String name, String message) {
        if (mAuth.getCurrentUser() == null) {
            return;
        }

        Map<String, Object> topup = new HashMap<>();
        topup.put("uid", mAuth.getCurrentUser().getUid());
        topup.put("amount", amount);
        topup.put("status", "pending");
        topup.put("created_at", FieldValue.serverTimestamp());
        topup.put("trx_id", currentTrxId);
        topup.put("sender_name", name.isEmpty() ? "Supporter" : name);
        topup.put("sender_message", message);

        db.collection("topups").document(currentTrxId)
            .set(topup)
            .addOnSuccessListener(aVoid -> Toast.makeText(TopUpActivity.this, "Topup request tersimpan.", Toast.LENGTH_SHORT).show())
            .addOnFailureListener(e -> Toast.makeText(TopUpActivity.this, "Gagal menyimpan request.", Toast.LENGTH_SHORT).show());
    }

    private void saveQrImage() {
        if (currentQrBitmap == null) {
            Toast.makeText(this, "QR belum dibuat", Toast.LENGTH_SHORT).show();
            return;
        }

        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.DISPLAY_NAME, "QRIS-ALTOMEDIA-" + System.currentTimeMillis() + ".png");
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/png");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            values.put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_PICTURES + "/ALTOINDO");
        }

        Uri uri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
        if (uri == null) {
            Toast.makeText(this, "Gagal menyimpan QR", Toast.LENGTH_SHORT).show();
            return;
        }

        try (OutputStream out = getContentResolver().openOutputStream(uri)) {
            if (out == null) throw new IOException("OutputStream not available");
            currentQrBitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
            Toast.makeText(this, "QR code disimpan ke galeri.", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            Toast.makeText(this, "Gagal menyimpan QR", Toast.LENGTH_SHORT).show();
        }
    }
}
