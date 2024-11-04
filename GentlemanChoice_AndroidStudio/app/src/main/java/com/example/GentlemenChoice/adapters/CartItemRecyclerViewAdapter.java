package com.example.GentlemenChoice.adapters;

import android.content.Context;
import android.content.Intent;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.GentlemenChoice.R;
import com.example.GentlemenChoice.activity.customer.ProductDetailActivity;
import com.example.GentlemenChoice.activity.customer.fragments.CartFragment;
import com.example.GentlemenChoice.api.CartItem.CartItemRepository;
import com.example.GentlemenChoice.api.CartItem.CartItemService;
import com.example.GentlemenChoice.api.Product.ProductRepository;
import com.example.GentlemenChoice.api.Product.ProductService;
import com.example.GentlemenChoice.model.CartItem;
import com.example.GentlemenChoice.model.Product;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CartItemRecyclerViewAdapter extends RecyclerView.Adapter<CartItemRecyclerViewAdapter.ViewHolder>{
    private List<CartItem> items;
    private Context context;
    private ProductService productService;

    private CartItemService cartItemService;

    private CartFragment cartFragment;

    public CartItemRecyclerViewAdapter(List<CartItem> items, Context context, CartFragment cartFragment) {
        this.items = items;
        this.context = context;
        this.cartFragment = cartFragment;
        this.cartItemService = CartItemRepository.getCartItemService();
        this.productService = ProductRepository.getProductService();
    }

    @NonNull
    @Override
    public CartItemRecyclerViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.cart_item, parent, false);
        return new CartItemRecyclerViewAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CartItemRecyclerViewAdapter.ViewHolder holder, int position) {
        CartItem cart = items.get(position);
        holder.textViewName.setText(cart.getProductVIew().getName());
        holder.textViewPrice.setText(String.format("%d VND", (int) cart.getProductVIew().getPrice()));
        holder.quantityEditText.setText(String.valueOf(cart.getQuantity()));
        // Load image using Glide
        if (cart.getProductVIew().getImages() != null && !cart.getProductVIew().getImages().isEmpty()) {
            String base64Image = cart.getProductVIew().getImages().get(0).getBase64StringImage();
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
        holder.itemView.setOnClickListener(v -> viewProductDetails(cart.getProductVIew().getProductId()));

        holder.btnDelete.setOnClickListener(v -> deleteCartItem(cart.getItemId(), position));

    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    private void viewProductDetails(int productId) {
        Call<Product> call = productService.find(productId);
        call.enqueue(new Callback<Product>() {
            @Override
            public void onResponse(Call<Product> call, Response<Product> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Product product = response.body();
                    Intent intent = new Intent(context, ProductDetailActivity.class);
                    if (product.getImages() != null && !product.getImages().isEmpty()) {
                        intent.putExtra("productImage", product.getImages().get(0).getBase64StringImage());
                    }
                    intent.putExtra("product", product);
                    context.startActivity(intent);
                } else {
                    Toast.makeText(context, "Failed to load product details", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Product> call, Throwable t) {
                Toast.makeText(context, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
    public void deleteCartItem(int itemId, int position) {
        Call<Void> call = cartItemService.deleteItem(itemId);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    items.remove(position);
                    notifyItemRemoved(position);
                    notifyItemRangeChanged(position, items.size());
                    Toast.makeText(context, "Xóa thành công", Toast.LENGTH_SHORT).show();
                    cartFragment.updateTotalPrice();
                } else {
                    Toast.makeText(context, "Xóa thất bại", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(context, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView textViewName;
        public TextView textViewPrice;
        public ImageView imageView;

        public EditText quantityEditText;

        AppCompatButton btnDelete;

        public ViewHolder(View itemView) {
            super(itemView);
            textViewName = itemView.findViewById(R.id.tvItemName);
            textViewPrice = itemView.findViewById(R.id.tvItemPrice);
            quantityEditText = itemView.findViewById(R.id.tvItemQuantity);

            imageView = itemView.findViewById(R.id.imgItem);
            btnDelete = itemView.findViewById(R.id.btnDeleteItemCart);
        }
    }
}
