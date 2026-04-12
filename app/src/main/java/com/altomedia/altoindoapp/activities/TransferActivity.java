package com.altomedia.altoindoapp.activities;

import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.altomedia.altoindoapp.databinding.ActivityTransferBinding;
import com.altomedia.altoindoapp.utils.FirebaseHelper;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

public class TransferActivity extends AppCompatActivity {
    private ActivityTransferBinding binding;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
