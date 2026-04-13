package com.altomedia.altoindoapp.activities;

import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.altomedia.altoindoapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class TransferActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transfer);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        findViewById(R.id.btn_transfer).setOnClickListener(v -> {
            String recipientId = ((android.widget.EditText) findViewById(R.id.et_recipient_id)).getText().toString();
            String amountStr = ((android.widget.EditText) findViewById(R.id.et_amount)).getText().toString();

            if (recipientId.isEmpty() || amountStr.isEmpty()) {
                Toast.makeText(this, "Masukkan ID penerima dan jumlah", Toast.LENGTH_SHORT).show();
                return;
            }

            long amount = Long.parseLong(amountStr);
            String senderUid = mAuth.getCurrentUser().getUid();

            // Cek saldo sender
            db.collection("users").document(senderUid).get().addOnSuccessListener(senderDoc -> {
                long senderBalance = senderDoc.getLong("balance_wallet");
                if (senderBalance < amount) {
                    Toast.makeText(this, "Saldo tidak cukup", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Cari recipient berdasarkan member_id
                db.collection("users").whereEqualTo("member_id", recipientId).get().addOnSuccessListener(querySnapshot -> {
                    if (querySnapshot.isEmpty()) {
                        Toast.makeText(this, "ID penerima tidak ditemukan", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    String recipientUid = querySnapshot.getDocuments().get(0).getId();

                    // Update saldo
                    db.collection("users").document(senderUid).update("balance_wallet", senderBalance - amount);
                    db.collection("users").document(recipientUid).update("balance_wallet",
                        querySnapshot.getDocuments().get(0).getLong("balance_wallet") + amount);

                    Toast.makeText(this, "Transfer berhasil", Toast.LENGTH_SHORT).show();
                    finish();
                });
            });
        });
    }
}