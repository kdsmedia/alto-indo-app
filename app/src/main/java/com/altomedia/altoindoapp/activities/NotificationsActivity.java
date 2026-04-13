package com.altomedia.altoindoapp.activities;

import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.altomedia.altoindoapp.R;
import com.altomedia.altoindoapp.models.NotificationMessage;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

public class NotificationsActivity extends AppCompatActivity {
    private LinearLayout notificationListContainer;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notifications);

        notificationListContainer = findViewById(R.id.notification_list_container);
        db = FirebaseFirestore.getInstance();
        loadNotifications();
    }

    private void loadNotifications() {
        db.collection("notifications").orderBy("createdAt", Query.Direction.DESCENDING).get().addOnSuccessListener(querySnapshot -> {
            notificationListContainer.removeAllViews();
            for (QueryDocumentSnapshot doc : querySnapshot) {
                NotificationMessage notification = doc.toObject(NotificationMessage.class);
                LinearLayout item = createNotificationItem(notification);
                notificationListContainer.addView(item);
            }
        }).addOnFailureListener(e -> Toast.makeText(this, "Gagal memuat notifikasi", Toast.LENGTH_SHORT).show());
    }

    private LinearLayout createNotificationItem(NotificationMessage notification) {
        LinearLayout item = new LinearLayout(this);
        item.setOrientation(LinearLayout.VERTICAL);
        item.setPadding(16, 16, 16, 16);
        item.setBackgroundResource(android.R.drawable.dialog_holo_light_frame);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(0, 0, 0, 16);
        item.setLayoutParams(params);

        TextView title = new TextView(this);
        title.setText(notification.title);
        title.setTextSize(18f);
        title.setTypeface(null, android.graphics.Typeface.BOLD);
        item.addView(title);

        TextView body = new TextView(this);
        body.setText(notification.body);
        body.setTextSize(14f);
        item.addView(body);

        TextView timestamp = new TextView(this);
        timestamp.setText("Dibuat: " + notification.createdAt);
        timestamp.setTextSize(12f);
        timestamp.setTextColor(android.graphics.Color.GRAY);
        item.addView(timestamp);

        return item;
    }
}
