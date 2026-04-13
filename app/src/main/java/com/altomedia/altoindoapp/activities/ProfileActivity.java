package com.altomedia.altoindoapp.activities;

import android.graphics.Bitmap;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.altomedia.altoindoapp.databinding.ActivityProfileBinding;
import com.altomedia.altoindoapp.models.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import net.glxn.qrgen.android.QRCode;

public class ProfileActivity extends AppCompatActivity {
    private ActivityProfileBinding binding;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        db = FirebaseFirestore.getInstance();
        loadProfile();
    }

    private void loadProfile() {
        String uid = FirebaseAuth.getInstance().getUid();
        db.collection("users").document(uid).get().addOnSuccessListener(documentSnapshot -> {
            User user = documentSnapshot.toObject(User.class);
            if (user != null) {
                binding.tvNameProfile.setText(user.full_name);
                binding.tvIdProfile.setText(user.member_id);
                binding.tvEmailProfile.setText(user.email);

                // Generate QR Code otomatis dari Member ID
                Bitmap myBitmap = QRCode.from(user.member_id).withSize(512, 512).bitmap();
                binding.ivQrCode.setImageBitmap(myBitmap);
            }
        });
    }
}
