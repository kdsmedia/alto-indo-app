package com.altomedia.altoindoapp.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.altomedia.altoindoapp.R;
import com.altomedia.altoindoapp.models.Product;
import com.bumptech.glide.Glide;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import java.util.List;

public class ProductsActivity extends AppCompatActivity {
    private LinearLayout productListContainer;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_products);

        productListContainer = findViewById(R.id.product_list_container);
        db = FirebaseFirestore.getInstance();
        loadProducts();
    }

    private void loadProducts() {
        db.collection("products").whereEqualTo("active", true).get().addOnSuccessListener(querySnapshot -> {
            productListContainer.removeAllViews();
            for (QueryDocumentSnapshot doc : querySnapshot) {
                Product product = doc.toObject(Product.class);
                LinearLayout item = createProductItem(product);
                productListContainer.addView(item);
            }
        }).addOnFailureListener(e -> Toast.makeText(this, "Gagal memuat produk", Toast.LENGTH_SHORT).show());
    }

    private LinearLayout createProductItem(Product product) {
        LinearLayout parent = new LinearLayout(this);
        parent.setOrientation(LinearLayout.VERTICAL);
        parent.setPadding(16, 16, 16, 16);
        parent.setBackgroundResource(android.R.drawable.dialog_holo_light_frame);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(0, 0, 0, 16);
        parent.setLayoutParams(params);

        // Product Image
        if (product.imageUrls != null && !product.imageUrls.isEmpty()) {
            ImageView imageView = new ImageView(this);
            imageView.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, 200));
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            Glide.with(this).load(product.imageUrls.get(0)).into(imageView);
            parent.addView(imageView);
        }

        TextView title = new TextView(this);
        title.setText(product.name + " - Rp " + product.price);
        title.setTextSize(16f);
        title.setTypeface(null, android.graphics.Typeface.BOLD);
        parent.addView(title);

        TextView body = new TextView(this);
        StringBuilder description = new StringBuilder(product.description);
        if (product.discountPrice > 0) {
            description.append("\nDiskon: Rp ").append(product.discountPrice);
        }
        if (product.variants != null && !product.variants.isEmpty()) {
            description.append("\nVarian: ").append(String.join(", ", product.variants));
        }
        description.append("\nKomisi: ").append(product.commissionPercent).append("%");
        body.setText(description.toString());
        parent.addView(body);

        LinearLayout buttonLayout = new LinearLayout(this);
        buttonLayout.setOrientation(LinearLayout.HORIZONTAL);

        Button btnBuy = new Button(this);
        btnBuy.setText("Beli");
        btnBuy.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1));
        btnBuy.setOnClickListener(v -> {
            // Show product details in a dialog or toast
            String details = "Nama: " + product.name + "\nHarga: Rp " + product.price +
                           "\nDeskripsi: " + product.description;
            Toast.makeText(this, details, Toast.LENGTH_LONG).show();
        });
        buttonLayout.addView(btnBuy);

        Button btnShare = new Button(this);
        btnShare.setText("Share");
        btnShare.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1));
        btnShare.setOnClickListener(v -> {
            Intent send = new Intent();
            send.setAction(Intent.ACTION_SEND);
            send.putExtra(Intent.EXTRA_TEXT, "Lihat produk: " + product.name + " - " + product.description);
            send.setType("text/plain");
            startActivity(Intent.createChooser(send, "Bagikan produk"));
        });
        buttonLayout.addView(btnShare);

        parent.addView(buttonLayout);

        return parent;
    }
}
