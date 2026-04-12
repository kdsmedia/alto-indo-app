package com.altomedia.altoindoapp.activities;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.altomedia.altoindoapp.databinding.ActivityMainBinding;
import com.altomedia.altoindoapp.models.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import java.text.NumberFormat;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        loadUserData();

        // Navigasi ke Transfer
        binding.btnTransfer.setOnClickListener(v -> 
            startActivity(new Intent(this, TransferActivity.class)));

        // Logout
        binding.btnLogout.setOnClickListener(v -> {
            mAuth.signOut();
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        });
    }

    private void loadUserData() {
        String uid = mAuth.getCurrentUser().getUid();
        db.collection("users").document(uid).addSnapshotListener((value, error) -> {
            if (value != null && value.exists()) {
                User user = value.toObject(User.class);
                if (user != null) {
                    binding.tvMemberName.setText(user.full_name);
                    binding.tvMemberId.setText("ID: " + user.member_id);
                    binding.tvBalance.setText(formatRupiah(user.balance_wallet));
                    binding.tvPoints.setText(String.valueOf(user.points_personal) + " PV");
                }
            }
        });
    }

    private String formatRupiah(long amount) {
        NumberFormat nf = NumberFormat.getCurrencyInstance(new Locale("id", "ID"));
        return nf.format(amount);
    }
}
