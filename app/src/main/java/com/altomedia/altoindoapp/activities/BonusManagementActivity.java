package com.altomedia.altoindoapp.activities;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.altomedia.altoindoapp.R;
import com.altomedia.altoindoapp.models.BonusConfig;
import com.google.firebase.firestore.FirebaseFirestore;

public class BonusManagementActivity extends AppCompatActivity {
    private EditText etInviteBonus, etCheckinBonus, etSponsorBonus, etVideoBonus;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bonus_management);

        if (!AdminGuard.isAdmin(this)) {
            finish();
            return;
        }

        etInviteBonus = findViewById(R.id.et_invite_bonus);
        etCheckinBonus = findViewById(R.id.et_checkin_bonus);
        etSponsorBonus = findViewById(R.id.et_sponsor_bonus);
        etVideoBonus = findViewById(R.id.et_video_bonus);
        Button btnSave = findViewById(R.id.btn_save_bonus);
        db = FirebaseFirestore.getInstance();

        btnSave.setOnClickListener(v -> saveBonus());
        loadBonus();
    }

    private void loadBonus() {
        db.collection("bonus_config").document("default").get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                BonusConfig config = documentSnapshot.toObject(BonusConfig.class);
                if (config != null) {
                    etInviteBonus.setText(String.valueOf(config.affiliateBonusPercent));
                    etCheckinBonus.setText(String.valueOf(config.checkinBonusPercent));
                    etSponsorBonus.setText(String.valueOf(config.sponsorBonusPercent));
                    etVideoBonus.setText(String.valueOf(config.videoBonusPercent));
                }
            }
        }).addOnFailureListener(e -> Toast.makeText(this, "Gagal memuat bonus", Toast.LENGTH_SHORT).show());
    }

    private void saveBonus() {
        BonusConfig config = new BonusConfig(
            "default",
            parseDouble(etInviteBonus.getText().toString()),
            parseDouble(etCheckinBonus.getText().toString()),
            parseDouble(etSponsorBonus.getText().toString()),
            parseDouble(etVideoBonus.getText().toString())
        );

        db.collection("bonus_config").document("default").set(config).addOnSuccessListener(aVoid -> {
            Toast.makeText(this, "Bonus tersimpan", Toast.LENGTH_SHORT).show();
        }).addOnFailureListener(e -> Toast.makeText(this, "Gagal menyimpan bonus", Toast.LENGTH_SHORT).show());
    }

    private double parseDouble(String value) {
        try {
            return Double.parseDouble(value.trim());
        } catch (Exception e) {
            return 0;
        }
    }
}
