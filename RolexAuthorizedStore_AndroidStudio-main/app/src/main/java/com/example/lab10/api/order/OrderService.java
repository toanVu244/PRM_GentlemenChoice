package com.example.lab10.api.order;

import com.example.lab10.model.OrderDtoResponse;
import com.example.lab10.model.OrderRequestDto;
import com.example.lab10.model.VNPayUrl;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface OrderService {
    @POST("createOrder")
    Call<VNPayUrl> createOrder(@Body List<OrderRequestDto> order);

    @GET("/api/v1/{CustomerId}")
    Call<List<OrderDtoResponse>> getOrdersByCustomerId(@Path("CustomerId") int customerId);

    @GET("admin")
    Call<List<OrderDtoResponse>> getAllOrders();

    @GET("detail/{id}")
    Call<OrderDtoResponse> getOrderById(@Path("id") int id);
}
