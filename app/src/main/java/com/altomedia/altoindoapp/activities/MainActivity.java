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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
            mAuth.signOut();
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        });
    }

    private void loadUserData() {
        String uid = mAuth.getCurrentUser().getUid();
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