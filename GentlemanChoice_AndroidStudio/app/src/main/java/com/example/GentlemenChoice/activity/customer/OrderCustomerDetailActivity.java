package com.example.GentlemenChoice.activity.customer;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.GentlemenChoice.R;
import com.example.GentlemenChoice.activity.auth.JWTUtils;
import com.example.GentlemenChoice.adapters.OrderCustomerDetailRecycleViewAdapter;
import com.example.GentlemenChoice.api.CartItem.CartItemRepository;
import com.example.GentlemenChoice.api.CartItem.CartItemService;
import com.example.GentlemenChoice.api.order.OrderRepository;
import com.example.GentlemenChoice.api.order.OrderService;
import com.example.GentlemenChoice.model.CartItem;
import com.example.GentlemenChoice.model.OrderRequestDto;
import com.example.GentlemenChoice.model.VNPayUrl;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OrderCustomerDetailActivity extends AppCompatActivity {
    private RecyclerView itemRecyclerView;
    private OrderCustomerDetailRecycleViewAdapter orderAdapter;
    private List<CartItem> items;
    private int customerId;

    AppCompatButton buttonOrder;
    private TextView totalPriceTextView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_order_customer_detail);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Chi tiết đơn hàng");
        toolbar.setTitleTextColor(Color.WHITE);
        toolbar.setSubtitleTextColor(Color.WHITE);
        toolbar.setNavigationIcon(R.drawable.ic_back_arrow);
        toolbar.setNavigationOnClickListener(v -> {
            Intent intent = new Intent(OrderCustomerDetailActivity.this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            OrderCustomerDetailActivity.this.finish();
        });

        totalPriceTextView = findViewById(R.id.totalPrice);
        itemRecyclerView = findViewById(R.id.orders_recycler_view);
        buttonOrder = findViewById(R.id.btnOrder);
        buttonOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<OrderRequestDto> cartItems = new ArrayList<>();
                for (CartItem item : items) {
                    OrderRequestDto orderProduct = new OrderRequestDto();
                    orderProduct.setItemId(item.getItemId());
                    orderProduct.setCustomerId(item.getCustomerId());
                    orderProduct.setProductId(item.getProductId());
                    orderProduct.setQuantity(item.getQuantity());
                    cartItems.add(orderProduct);
                }


                OrderService orderService = OrderRepository.getOrderService();
                Call<VNPayUrl> call = orderService.createOrder(cartItems);
                call.enqueue(new Callback<VNPayUrl>() {
                    @Override
                    public void onResponse(Call<VNPayUrl> call, Response<VNPayUrl> response) {
                        if (response.isSuccessful()) {
//                            Toast.makeText(OrderCustomerDetailActivity.this, "Order successfully", Toast.LENGTH_SHORT).show();
//                            // Điều hướng về MainActivity sau khi đặt hàng thành công
//                            Intent intent = new Intent(OrderCustomerDetailActivity.this, MainActivity.class);
//                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
//                            startActivity(intent);
//                            finish();
                            VNPayUrl paymentUrl = response.body(); // Lấy URL từ response

                            Intent intent = new Intent(Intent.ACTION_VIEW);
                            intent.setData(Uri.parse(paymentUrl.getUrl()));
                            startActivity(intent);
                            finish();
                        } else {
                            try {
                                // Log the error body to understand the 400 error
                                String errorBody = response.errorBody().string();
                                Log.e("OrderError", "Error: " + errorBody);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            Toast.makeText(OrderCustomerDetailActivity.this, "Order fail", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<VNPayUrl> call, Throwable t) {
                        Toast.makeText(OrderCustomerDetailActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        String accessToken = getIntent().getStringExtra("accessToken");
        if (accessToken == null) {
            SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
            accessToken = sharedPreferences.getString("accessToken", null);
        }
        if (accessToken != null) {
            try {
                String[] decodedParts = JWTUtils.decoded(accessToken);
                String body = decodedParts[1];
                JsonObject jsonObject = JsonParser.parseString(body).getAsJsonObject();
                customerId = Integer.parseInt(jsonObject.get("CustomerId").getAsString());

            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(this, "Failed to decode token", Toast.LENGTH_SHORT).show();
            }
        }
        itemRecyclerView.setLayoutManager(new LinearLayoutManager(OrderCustomerDetailActivity.this, LinearLayoutManager.VERTICAL, false));

        CartItemService cartService = CartItemRepository.getCartItemService();
        Call<List<CartItem>> call = cartService.getCartFromCustomer(customerId);
        call.enqueue(new Callback<List<CartItem>>() {
            @Override
            public void onResponse(Call<List<CartItem>> call, Response<List<CartItem>> response) {
                if (response.isSuccessful()) {
                    items = response.body();
                    orderAdapter = new OrderCustomerDetailRecycleViewAdapter(items, OrderCustomerDetailActivity.this);
                    itemRecyclerView.setAdapter(orderAdapter);
                    calculateTotalPrice(items);
                }
            }

            @Override
            public void onFailure(Call<List<CartItem>> call, Throwable t) {
                Toast.makeText(OrderCustomerDetailActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });


    }
    private void calculateTotalPrice(List<CartItem> items) {
        double totalPrice = 0.0;
        for (CartItem item : items) {
            totalPrice += item.getProductVIew().getPrice() * item.getQuantity();
        }
        totalPriceTextView.setText(String.format("%.2f VND", totalPrice));
    }
}