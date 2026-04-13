package com.altomedia.altoindoapp.activities;

import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
<<<<<<< HEAD
import com.altomedia.altoindoapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class TransferActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
=======
import com.altomedia.altoindoapp.databinding.ActivityTransferBinding;
import com.altomedia.altoindoapp.utils.FirebaseHelper;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

public class TransferActivity extends AppCompatActivity {
    private ActivityTransferBinding binding;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
>>>>>>> 255cf0eca7c683250211d8dc40b5a22fa4b71580

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
<<<<<<< HEAD
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
=======
        binding = ActivityTransferBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        binding.btnSendNow.setOnClickListener(v -> validateAndTransfer());
    }

    private void validateAndTransfer() {
        String targetId = binding.etTargetId.getText().toString();
        String amountStr = binding.etAmount.getText().toString();
        String pin = binding.etPin.getText().toString();

        if (targetId.isEmpty() || amountStr.isEmpty() || pin.isEmpty()) {
            Toast.makeText(this, "Mohon lengkapi data", Toast.LENGTH_SHORT).show();
            return;
        }

        long amount = Long.parseLong(amountStr);

        // 1. Cari ID Member tujuan di Firestore
        db.collection("users").whereEqualTo("member_id", targetId).get()
            .addOnCompleteListener(task -> {
                if (task.isSuccessful() && !task.getResult().isEmpty()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        String receiverUid = document.getId();
                        
                        // 2. Eksekusi Transfer via FirebaseHelper
                        FirebaseHelper.transferBalance(mAuth.getUid(), receiverUid, amount, new FirebaseHelper.TransactionCallback() {
                            @Override
                            public void onSuccess() {
                                Toast.makeText(TransferActivity.this, "Transfer Berhasil!", Toast.LENGTH_SHORT).show();
                                finish();
                            }

                            @Override
                            public void onFailure(String error) {
                                Toast.makeText(TransferActivity.this, "Gagal: " + error, Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                } else {
                    Toast.makeText(this, "ID Tujuan tidak ditemukan", Toast.LENGTH_SHORT).show();
                }
            });
    }
}
>>>>>>> 255cf0eca7c683250211d8dc40b5a22fa4b71580
