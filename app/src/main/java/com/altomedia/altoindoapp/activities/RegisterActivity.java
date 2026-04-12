package com.altomedia.altoindoapp.activities;

import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.altomedia.altoindoapp.databinding.ActivityRegisterBinding;
import com.altomedia.altoindoapp.models.User;
import com.altomedia.altoindoapp.utils.IDGenerator;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class RegisterActivity extends AppCompatActivity {
    private ActivityRegisterBinding binding;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRegisterBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        binding.btnRegister.setOnClickListener(v -> {
            String email = binding.etEmail.getText().toString();
            String pass = binding.etPassword.getText().toString();
            String name = binding.etFullName.getText().toString();
            String upline = binding.etUplineId.getText().toString();

            if (email.isEmpty() || pass.isEmpty() || name.isEmpty()) {
                Toast.makeText(this, "Lengkapi data!", Toast.LENGTH_SHORT).show();
                return;
            }

            mAuth.createUserWithEmailAndPassword(email, pass).addOnSuccessListener(authResult -> {
                String uid = authResult.getUser().getUid();
                String memberId = IDGenerator.generateMemberID(); // Generate 8 digit

                User newUser = new User(uid, memberId, email, name, upline.isEmpty() ? "ROOT" : upline);

                db.collection("users").document(uid).set(newUser)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(this, "Registrasi Berhasil! ID Anda: " + memberId, Toast.LENGTH_LONG).show();
                        finish();
                    });
            });
        });
    }
}
