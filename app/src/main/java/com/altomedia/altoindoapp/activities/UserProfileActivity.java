package com.altomedia.altoindoapp.activities;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.altomedia.altoindoapp.R;
import com.altomedia.altoindoapp.models.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class UserProfileActivity extends AppCompatActivity {
    private TextView tvMemberId, tvEmail;
    private EditText etFullName, etPhone, etAccountName, etAccountOwner, etAccountNumber;
    private Spinner spinnerAccountType;
    private Button btnSave, btnBack;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        tvMemberId = findViewById(R.id.tv_member_id);
        tvEmail = findViewById(R.id.tv_email);
        etFullName = findViewById(R.id.et_full_name);
        etPhone = findViewById(R.id.et_phone);
        spinnerAccountType = findViewById(R.id.spinner_account_type);
        etAccountName = findViewById(R.id.et_account_name);
        etAccountOwner = findViewById(R.id.et_account_owner);
        etAccountNumber = findViewById(R.id.et_account_number);
        btnSave = findViewById(R.id.btn_save);
        btnBack = findViewById(R.id.btn_back);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // Setup spinner
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.account_types, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerAccountType.setAdapter(adapter);

        loadUserData();

        btnSave.setOnClickListener(v -> saveProfile());
        btnBack.setOnClickListener(v -> finish());
    }

    private void loadUserData() {
        String uid = mAuth.getCurrentUser().getUid();
        db.collection("users").document(uid).get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                User user = documentSnapshot.toObject(User.class);
                tvMemberId.setText(user.member_id);
                tvEmail.setText(user.email);
                etFullName.setText(user.full_name);
                etPhone.setText(user.phone);
                etAccountName.setText(user.account_name);
                etAccountOwner.setText(user.account_owner);
                etAccountNumber.setText(user.account_number);

                // Set spinner selection
                String[] accountTypes = getResources().getStringArray(R.array.account_types);
                for (int i = 0; i < accountTypes.length; i++) {
                    if (accountTypes[i].equals(user.account_type)) {
                        spinnerAccountType.setSelection(i);
                        break;
                    }
                }
            }
        }).addOnFailureListener(e -> {
            Toast.makeText(this, "Gagal memuat data profil", Toast.LENGTH_SHORT).show();
        });
    }

    private void saveProfile() {
        String uid = mAuth.getCurrentUser().getUid();
        String fullName = etFullName.getText().toString().trim();
        String phone = etPhone.getText().toString().trim();
        String accountType = spinnerAccountType.getSelectedItem().toString();
        String accountName = etAccountName.getText().toString().trim();
        String accountOwner = etAccountOwner.getText().toString().trim();
        String accountNumber = etAccountNumber.getText().toString().trim();

        if (fullName.isEmpty() || phone.isEmpty()) {
            Toast.makeText(this, "Nama dan telepon wajib diisi", Toast.LENGTH_SHORT).show();
            return;
        }

        db.collection("users").document(uid).update(
            "full_name", fullName,
            "phone", phone,
            "account_type", accountType,
            "account_name", accountName,
            "account_owner", accountOwner,
            "account_number", accountNumber
        ).addOnSuccessListener(aVoid -> {
            Toast.makeText(this, "Profil berhasil disimpan", Toast.LENGTH_SHORT).show();
            finish();
        }).addOnFailureListener(e -> {
            Toast.makeText(this, "Gagal menyimpan profil", Toast.LENGTH_SHORT).show();
        });
    }
}