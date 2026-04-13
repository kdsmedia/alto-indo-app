package com.altomedia.altoindoapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.altomedia.altoindoapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

public class LoginActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        findViewById(R.id.btn_login).setOnClickListener(v -> {
            String input = ((EditText) findViewById(R.id.et_email)).getText().toString().trim();
            String pass = ((EditText) findViewById(R.id.et_password)).getText().toString().trim();

            if (input.isEmpty() || pass.isEmpty()) {
                Toast.makeText(this, "Masukkan ID/Email dan password", Toast.LENGTH_SHORT).show();
                return;
            }

            resolveEmailAndSignIn(input, pass);
        });

        findViewById(R.id.tv_go_to_register).setOnClickListener(v -> startActivity(new Intent(this, RegisterActivity.class)));
    }

    private void resolveEmailAndSignIn(String input, String pass) {
        if (input.equals(AdminGuard.ADMIN_MEMBER_ID)) {
            signInAdmin(AdminGuard.ADMIN_EMAIL, pass, input);
            return;
        }

        if (input.contains("@")) {
            signInAdmin(input, pass, input);
            return;
        }

        db.collection("users").whereEqualTo("member_id", input).limit(1).get().addOnSuccessListener(querySnapshot -> {
            if (querySnapshot.isEmpty()) {
                Toast.makeText(this, "ID atau Email tidak ditemukan", Toast.LENGTH_SHORT).show();
                return;
            }
            String userEmail = querySnapshot.getDocuments().get(0).getString("email");
            signInAdmin(userEmail, pass, input);
        }).addOnFailureListener(e -> Toast.makeText(this, "Gagal mencari pengguna", Toast.LENGTH_SHORT).show());
    }

    private void signInAdmin(String email, String pass, String input) {
        mAuth.signInWithEmailAndPassword(email, pass).addOnSuccessListener(authResult -> {
            if (email.equalsIgnoreCase(AdminGuard.ADMIN_EMAIL)) {
                createAdminRecordIfNeeded(authResult.getUser().getUid(), email);
                startActivity(new Intent(this, AdminDashboardActivity.class));
            } else {
                startActivity(new Intent(this, MainActivity.class));
            }
            finish();
        }).addOnFailureListener(e -> {
            if (email.equalsIgnoreCase(AdminGuard.ADMIN_EMAIL) && pass.equals(AdminGuard.ADMIN_PASSWORD)) {
                mAuth.createUserWithEmailAndPassword(email, pass).addOnSuccessListener(createResult -> {
                    createAdminRecordIfNeeded(createResult.getUser().getUid(), email);
                    startActivity(new Intent(this, AdminDashboardActivity.class));
                    finish();
                }).addOnFailureListener(createError -> Toast.makeText(this, "Login gagal", Toast.LENGTH_SHORT).show());
            } else {
                Toast.makeText(this, "Login gagal", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void createAdminRecordIfNeeded(String uid, String email) {
        db.collection("users").document(uid).get().addOnSuccessListener(documentSnapshot -> {
            if (!documentSnapshot.exists()) {
                com.altomedia.altoindoapp.models.User adminUser = new com.altomedia.altoindoapp.models.User(uid, AdminGuard.ADMIN_MEMBER_ID, email, "Admin", "ROOT");
                adminUser.role = "admin";
                adminUser.is_active = true;
                adminUser.balance_wallet = 0;
                db.collection("users").document(uid).set(adminUser);
            }
        });
    }
}
