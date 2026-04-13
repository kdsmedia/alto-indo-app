package com.altomedia.altoindoapp.activities;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.altomedia.altoindoapp.R;
import com.altomedia.altoindoapp.models.User;
import com.altomedia.altoindoapp.models.WithdrawRequest;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class WithdrawActivity extends AppCompatActivity {
    private TextView tvBalance;
    private EditText etAmount, etBankName, etAccountNumber, etAccountHolder;
    private Button btnSubmit, btnBack;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private long currentBalance = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_withdraw);

        tvBalance = findViewById(R.id.tv_balance);
        etAmount = findViewById(R.id.et_amount);
        etBankName = findViewById(R.id.et_bank_name);
        etAccountNumber = findViewById(R.id.et_account_number);
        etAccountHolder = findViewById(R.id.et_account_holder);
        btnSubmit = findViewById(R.id.btn_submit);
        btnBack = findViewById(R.id.btn_back);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        loadUserData();

        btnSubmit.setOnClickListener(v -> submitWithdrawRequest());
        btnBack.setOnClickListener(v -> finish());
    }

    private void loadUserData() {
        String uid = mAuth.getCurrentUser().getUid();
        db.collection("users").document(uid).get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                User user = documentSnapshot.toObject(User.class);
                currentBalance = user.balance_wallet;
                tvBalance.setText("Saldo Tersedia: Rp " + currentBalance);

                // Pre-fill from user account if available
                if (user.account_name != null && !user.account_name.isEmpty()) {
                    etBankName.setText(user.account_name);
                }
                if (user.account_number != null && !user.account_number.isEmpty()) {
                    etAccountNumber.setText(user.account_number);
                }
                if (user.account_owner != null && !user.account_owner.isEmpty()) {
                    etAccountHolder.setText(user.account_owner);
                }
            }
        }).addOnFailureListener(e -> {
            Toast.makeText(this, "Gagal memuat data", Toast.LENGTH_SHORT).show();
        });
    }

    private void submitWithdrawRequest() {
        String amountStr = etAmount.getText().toString().trim();
        String bankName = etBankName.getText().toString().trim();
        String accountNumber = etAccountNumber.getText().toString().trim();
        String accountHolder = etAccountHolder.getText().toString().trim();

        if (amountStr.isEmpty() || bankName.isEmpty() || accountNumber.isEmpty() || accountHolder.isEmpty()) {
            Toast.makeText(this, "Semua field wajib diisi", Toast.LENGTH_SHORT).show();
            return;
        }

        long amount = Long.parseLong(amountStr);
        if (amount <= 0) {
            Toast.makeText(this, "Jumlah penarikan harus lebih dari 0", Toast.LENGTH_SHORT).show();
            return;
        }

        if (amount > currentBalance) {
            Toast.makeText(this, "Saldo tidak mencukupi", Toast.LENGTH_SHORT).show();
            return;
        }

        String uid = mAuth.getCurrentUser().getUid();
        String id = db.collection("withdraw_requests").document().getId();
        String createdAt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());

        WithdrawRequest request = new WithdrawRequest(id, uid, amount, bankName, accountNumber, accountHolder, "pending", createdAt);

        db.collection("withdraw_requests").document(id).set(request).addOnSuccessListener(aVoid -> {
            Toast.makeText(this, "Permintaan penarikan berhasil diajukan", Toast.LENGTH_SHORT).show();
            finish();
        }).addOnFailureListener(e -> {
            Toast.makeText(this, "Gagal mengajukan permintaan", Toast.LENGTH_SHORT).show();
        });
    }
}