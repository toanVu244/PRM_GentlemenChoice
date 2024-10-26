package com.example.GentlemenChoice.activity.admin;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.GentlemenChoice.R;
import com.example.GentlemenChoice.adapters.OrderAdminRecyclerViewAdapter;
import com.example.GentlemenChoice.api.APIClient;
import com.example.GentlemenChoice.api.order.OrderService;
import com.example.GentlemenChoice.model.OrderDtoResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OrderAdminActivity extends AppCompatActivity {

    private RecyclerView orderRecycleView;

    private OrderAdminRecyclerViewAdapter orderAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.order_admin);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Các đơn hàng");
        toolbar.setTitleTextColor(Color.WHITE);
        toolbar.setSubtitleTextColor(Color.WHITE);
        toolbar.setNavigationIcon(R.drawable.ic_back_arrow);
        toolbar.setNavigationOnClickListener(v -> {
            Intent intent = new Intent(OrderAdminActivity.this, AdminDashboardActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            OrderAdminActivity.this.finish();
        });

        orderRecycleView = findViewById(R.id.recyclerViewForOrder);

        orderRecycleView.setLayoutManager(new LinearLayoutManager(OrderAdminActivity.this, LinearLayoutManager.VERTICAL, false));

        loadOrders();
    }

    private void loadOrders() {
        OrderService orderService = APIClient.getClient().create(OrderService.class);
        Call<List<OrderDtoResponse>> call = orderService.getAllOrders();

        call.enqueue(new Callback<List<OrderDtoResponse>>() {
            @Override
            public void onResponse(Call<List<OrderDtoResponse>> call, Response<List<OrderDtoResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<OrderDtoResponse> orders = response.body();
                    orderAdapter = new OrderAdminRecyclerViewAdapter(orders, OrderAdminActivity.this);
                    orderRecycleView.setAdapter(orderAdapter);


                } else {
                    Toast.makeText(OrderAdminActivity.this, "Tải dữ liệu danh mục thất bại", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<OrderDtoResponse>> call, Throwable t) {
                Toast.makeText(OrderAdminActivity.this, "Lỗi: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}