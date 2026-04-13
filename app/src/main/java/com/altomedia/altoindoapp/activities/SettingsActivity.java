package com.altomedia.altoindoapp.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.altomedia.altoindoapp.R;
import com.altomedia.altoindoapp.models.AppSetting;
import com.google.firebase.firestore.FirebaseFirestore;

public class SettingsActivity extends AppCompatActivity {
    private TextView tvContactEmail, tvContactPhone, tvSocialUrl, tvOtherUrl;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        tvContactEmail = findViewById(R.id.tv_contact_email);
        tvContactPhone = findViewById(R.id.tv_contact_phone);
        tvSocialUrl = findViewById(R.id.tv_social_url);
        tvOtherUrl = findViewById(R.id.tv_other_url);
        db = FirebaseFirestore.getInstance();

        loadSettings();

        tvSocialUrl.setOnClickListener(v -> openUrl(tvSocialUrl.getText().toString()));
        tvOtherUrl.setOnClickListener(v -> openUrl(tvOtherUrl.getText().toString()));
    }

    private void loadSettings() {
        db.collection("app_settings").document("default").get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                AppSetting setting = documentSnapshot.toObject(AppSetting.class);
                if (setting != null) {
                    tvContactEmail.setText(setting.contactEmail != null ? setting.contactEmail : "");
                    tvContactPhone.setText(setting.contactPhone != null ? setting.contactPhone : "");
                    tvSocialUrl.setText(setting.socialUrl != null ? setting.socialUrl : "");
                    tvOtherUrl.setText(setting.otherUrl != null ? setting.otherUrl : "");
                }
            }
        }).addOnFailureListener(e -> Toast.makeText(this, "Gagal memuat setelan", Toast.LENGTH_SHORT).show());
    }

    private void openUrl(String url) {
        if (url == null || url.isEmpty()) {
            Toast.makeText(this, "URL tidak tersedia", Toast.LENGTH_SHORT).show();
            return;
        }
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        startActivity(intent);
    }
}
