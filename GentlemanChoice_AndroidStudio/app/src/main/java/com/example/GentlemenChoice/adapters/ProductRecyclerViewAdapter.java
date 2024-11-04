package com.example.GentlemenChoice.adapters;

import android.content.Context;
import android.content.Intent;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.GentlemenChoice.R;
import com.example.GentlemenChoice.activity.customer.ProductDetailActivity;
import com.example.GentlemenChoice.model.Product;

import java.util.List;

public class ProductRecyclerViewAdapter extends RecyclerView.Adapter<ProductRecyclerViewAdapter.ViewHolder> {

    private List<Product> products;
    private Context context;

    public ProductRecyclerViewAdapter(List<Product> products, Context context) {
        this.products = products;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_product, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Product product = products.get(position);
        holder.textViewName.setText(product.getName());
        holder.textViewPrice.setText(String.format("%d VND", (int) product.getPrice()));

        // Load image using Glide
        if (product.getImages() != null && !product.getImages().isEmpty()) {
            String base64Image = product.getImages().get(0).getBase64StringImage();
            if (base64Image != null && !base64Image.isEmpty()) {
                byte[] imageBytes = Base64.decode(base64Image, Base64.DEFAULT);
                Glide.with(context)
                        .load(imageBytes)
                        .into(holder.imageView);
            } else {
                holder.imageView.setImageResource(R.drawable.product); // Default image
            }
        } else {
            holder.imageView.setImageResource(R.drawable.product); // Default image
        }

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, ProductDetailActivity.class);
            intent.putExtra("product", product);
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return products.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView textViewName;
        public TextView textViewPrice;
        public ImageView imageView;

        public ViewHolder(View itemView) {
            super(itemView);
            textViewName = itemView.findViewById(R.id.product_name);
            textViewPrice = itemView.findViewById(R.id.product_price);
            imageView = itemView.findViewById(R.id.product_image);
        }
    }
}
