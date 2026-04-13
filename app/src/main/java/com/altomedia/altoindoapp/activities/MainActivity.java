<<<<<<< HEAD
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.altomedia.altoindoapp.R;
import com.altomedia.altoindoapp.models.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class MainActivity extends AppCompatActivity {
    private TextView tvBalance, tvMemberId, tvName;
    private Button btnTransfer, btnTopup, btnProducts, btnProfile, btnWithdraw, btnNotifications, btnSettings, btnLogout;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
=======
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
>>>>>>> 255cf0eca7c683250211d8dc40b5a22fa4b71580

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
<<<<<<< HEAD
        setContentView(R.layout.activity_main);

        tvBalance = findViewById(R.id.tv_balance);
        tvMemberId = findViewById(R.id.tv_member_id);
        tvName = findViewById(R.id.tv_name);
        btnTransfer = findViewById(R.id.btn_transfer);
        btnTopup = findViewById(R.id.btn_topup);
        btnProducts = findViewById(R.id.btn_products);
        btnProfile = findViewById(R.id.btn_profile);
        btnWithdraw = findViewById(R.id.btn_withdraw);
        btnNotifications = findViewById(R.id.btn_notifications);
        btnSettings = findViewById(R.id.btn_settings);
        btnLogout = findViewById(R.id.btn_logout);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        if (mAuth.getCurrentUser() == null) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        if (AdminGuard.isAdmin(this)) {
            startActivity(new Intent(this, AdminDashboardActivity.class));
            finish();
            return;
        }

        loadUserData();

        btnTransfer.setOnClickListener(v -> startActivity(new Intent(this, TransferActivity.class)));
        btnTopup.setOnClickListener(v -> startActivity(new Intent(this, TopUpActivity.class)));
        btnProducts.setOnClickListener(v -> startActivity(new Intent(this, ProductsActivity.class)));
        btnProfile.setOnClickListener(v -> startActivity(new Intent(this, UserProfileActivity.class)));
        btnWithdraw.setOnClickListener(v -> startActivity(new Intent(this, WithdrawActivity.class)));
        btnNotifications.setOnClickListener(v -> startActivity(new Intent(this, NotificationsActivity.class)));
        btnSettings.setOnClickListener(v -> startActivity(new Intent(this, SettingsActivity.class)));
        btnLogout.setOnClickListener(v -> {
=======
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
>>>>>>> 255cf0eca7c683250211d8dc40b5a22fa4b71580
            mAuth.signOut();
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        });
    }

    private void loadUserData() {
        String uid = mAuth.getCurrentUser().getUid();
<<<<<<< HEAD
        db.collection("users").document(uid).get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                User user = documentSnapshot.toObject(User.class);
                tvBalance.setText("Saldo: Rp " + user.balance_wallet);
                tvMemberId.setText("ID Member: " + user.member_id);
                tvName.setText("Nama: " + user.full_name);
            }
        }).addOnFailureListener(e -> {
            Toast.makeText(this, "Gagal memuat data", Toast.LENGTH_SHORT).show();
        });
    }
}
=======
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
>>>>>>> 255cf0eca7c683250211d8dc40b5a22fa4b71580
