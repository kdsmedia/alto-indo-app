package com.altomedia.altoindoapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;
import com.altomedia.altoindoapp.R;

public class AdminDashboardActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_dashboard);

        if (!AdminGuard.isAdmin(this)) {
            finish();
            return;
        }

        Button btnUsers = findViewById(R.id.btn_manage_users);
        Button btnTransactions = findViewById(R.id.btn_manage_transactions);
        Button btnNotifications = findViewById(R.id.btn_manage_notifications);
        Button btnProducts = findViewById(R.id.btn_manage_products);
        Button btnSettings = findViewById(R.id.btn_manage_settings);
        Button btnBonus = findViewById(R.id.btn_manage_bonus);

        btnUsers.setOnClickListener(v -> startActivity(new Intent(this, AdminUserManagementActivity.class)));
        btnTransactions.setOnClickListener(v -> startActivity(new Intent(this, AdminTransactionManagementActivity.class)));
        btnNotifications.setOnClickListener(v -> startActivity(new Intent(this, AdminNotificationManagementActivity.class)));
        btnProducts.setOnClickListener(v -> startActivity(new Intent(this, AdminProductManagementActivity.class)));
        btnSettings.setOnClickListener(v -> startActivity(new Intent(this, AdminSettingsActivity.class)));
        btnBonus.setOnClickListener(v -> startActivity(new Intent(this, BonusManagementActivity.class)));
    }
}
